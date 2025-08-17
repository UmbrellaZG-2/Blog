package com.website.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.website.backend.entity.User;
import com.website.backend.security.JwtTokenProvider;
import com.website.backend.service.GuestService;
import com.website.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

	/**
	 * 处理/api/auth根路径的GET请求
	 */
	@GetMapping
	public ResponseEntity<?> getAuthInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Auth API root endpoint");
		response.put("available_endpoints", "/api/auth/guest/login, /api/auth/register/send-code, /api/auth/register/verify");
		return ResponseEntity.ok(response);
	}

	private final AuthenticationManager authenticationManager;

	private final JwtTokenProvider jwtTokenProvider;

	private final GuestService guestService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 构造函数注入依赖
	 */
	public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
			GuestService guestService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.guestService = guestService;
	}

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		try {
			// 验证用户凭据
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (!passwordEncoder.matches(password, userDetails.getPassword())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
			}

			// 生成JWT令牌
Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
String jwt = jwtTokenProvider.generateToken(authentication);



			Map<String, String> response = new HashMap<>();
			response.put("token", jwt);
			response.put("type", "Bearer");
			response.put("message", "登录成功");

			return ResponseEntity.ok(response);
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在");
		} catch (Exception e) {
			log.error("登录失败: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录失败");
		}
	}

	/**
	 * 游客登录 (POST)
	 */
	@PostMapping("/guest/login")
	public ResponseEntity<?> guestLogin() {
		log.info("处理游客登录请求(POST)");
		return processGuestLogin();
	}

	/**
	 * 游客登录 (GET) - 为兼容错误的GET请求而添加
	 */
	@GetMapping("/guest/login")
	public ResponseEntity<?> guestLoginGet() {
		log.info("处理游客登录请求(GET) - 重定向到POST处理");
		return processGuestLogin();
	}

	/**
	 * 处理游客登录的核心方法
	 */
	private ResponseEntity<?> processGuestLogin() {
		// 生成游客用户名和密码
		String guestUsername = guestService.generateGuestUsername();
		String guestPassword = "guest_password";

		// 保存游客信息到Redis
		guestService.saveGuestToRedis(guestUsername, guestPassword);

		// 生成JWT令牌
		String jwt = guestService.generateGuestToken(guestUsername);

		Map<String, String> response = new HashMap<>();
		response.put("token", jwt);
		response.put("type", "Bearer");
		response.put("message", "游客登录成功，有效期6小时");

		return ResponseEntity.ok(response);
	}

	/**
	 * 发送验证码
	 */
	@PostMapping("/register/send-code")
	public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		// 检查用户名是否已存在
		if (userService.existsByUsername(username)) {
			return ResponseEntity.badRequest().body("用户名已存在");
		}

		// 生成验证码
		String verificationCode = generateVerificationCode();

		// 存储验证码到Redis，设置1分钟过期
		String redisKey = "verification_code:" + username;
		redisTemplate.opsForValue().set(redisKey, verificationCode, 1, TimeUnit.MINUTES);

		// 打印验证码到后端日志
		log.info("用户 {} 的注册验证码: {}", username, verificationCode);

		return ResponseEntity.ok("验证码已发送，有效期1分钟");
	}

	/**
	 * 验证验证码并注册
	 */
	@PostMapping("/register/verify")
	public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");
		String verificationCode = request.get("verificationCode");

		if (username == null || username.isEmpty() || password == null || password.isEmpty() || verificationCode == null || verificationCode.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名、密码和验证码不能为空");
		}

		// 检查验证码是否有效
		String redisKey = "verification_code:" + username;
		String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

		if (storedCode == null || !storedCode.equals(verificationCode)) {
			return ResponseEntity.badRequest().body("验证码无效或已过期");
		}

		// 注册用户
		try {
			User user = userService.registerUser(username, password);
			// 注册成功后删除验证码
			redisTemplate.delete(redisKey);
			return ResponseEntity.ok("注册成功");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	/**
	 * 生成6位数字验证码
	 */
	private String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}

}