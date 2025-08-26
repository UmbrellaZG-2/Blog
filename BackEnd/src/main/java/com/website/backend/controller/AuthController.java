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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.website.backend.entity.User;
import com.website.backend.service.GuestService;
import com.website.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	/**
	 * 处理/api/auth根路径的GET请求
	 */
	@GetMapping("/get")
public ResponseEntity<?> getAuthInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Auth API root endpoint");
		response.put("available_endpoints", "/api/auth/login, /api/auth/admin/login, /api/auth/register, /api/auth/admin/register, /api/auth/guest/login");
		return ResponseEntity.ok(response);
	}

	private final GuestService guestService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * 构造函数注入依赖
	 */
	public AuthController(GuestService guestService) {
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
			// 注意：由于移除了权限控制，这里不再验证密码

			Map<String, String> response = new HashMap<>();
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
	 * 管理员登录
	 */
	@PostMapping("/admin/login")
	public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		try {
			// 验证管理员凭据
			// 注意：由于移除了权限控制，这里不再验证密码和角色

			Map<String, String> response = new HashMap<>();
			response.put("message", "管理员登录成功");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("管理员登录失败: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录失败");
		}
	}

	/**
	 * 管理员注册
	 */
	@PostMapping("/admin/register")
	public ResponseEntity<?> adminRegister(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");
		String email = request.get("email");

		log.info("开始处理管理员注册请求，用户名: {}", username);

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			log.warn("用户名或密码为空");
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		// 检查用户名是否已存在
		log.info("检查用户名是否已存在");
		if (userService.existsByUsername(username)) {
			log.warn("用户名已存在: {}", username);
			return ResponseEntity.badRequest().body("用户名已存在");
		}

		// 注册管理员
		try {
			log.info("开始注册管理员");
			User user = userService.registerUser(username, password, true);
			log.info("管理员注册成功，用户名: {}", username);
			return ResponseEntity.ok("管理员注册成功");
		} catch (Exception e) {
			log.error("管理员注册失败: {}", e.getMessage(), e);
			return ResponseEntity.badRequest().body("注册失败: " + e.getMessage());
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

		// 生成访问标识
		String token = guestService.generateGuestToken(guestUsername);

		Map<String, String> response = new HashMap<>();
		response.put("token", token);
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
	public ResponseEntity<?> verifyAndRegister(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");
		String verificationCode = request.get("verificationCode");
		Boolean isAdmin = Boolean.valueOf(request.getOrDefault("isAdmin", "false"));

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
			User user = userService.registerUser(username, password, isAdmin);
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