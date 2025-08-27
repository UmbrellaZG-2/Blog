package com.website.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import com.website.backend.entity.VerificationCode;
import com.website.backend.service.GuestService;
import com.website.backend.service.UserService;
import com.website.backend.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@GetMapping("/get")
public ResponseEntity<?> getAuthInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Auth API root endpoint");
		response.put("available_endpoints", "/api/auth/login, /api/auth/admin/login, /api/auth/register, /api/auth/admin/register, /api/auth/guest/login");
		return ResponseEntity.ok(response);
	}

	private final GuestService guestService;

	@Autowired
	private VerificationCodeService verificationCodeService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsService userDetailsService;

	public AuthController(GuestService guestService) {
		this.guestService = guestService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");
		
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

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

	@PostMapping("/admin/login")
	public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		try {
			Map<String, String> response = new HashMap<>();
			response.put("message", "管理员登录成功");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("管理员登录失败: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录失败");
		}
	}

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

		log.info("检查用户名是否已存在");
		if (userService.existsByUsername(username)) {
			log.warn("用户名已存在: {}", username);
			return ResponseEntity.badRequest().body("用户名已存在");
		}

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

	@PostMapping("/guest/login")
	public ResponseEntity<?> guestLogin() {
		log.info("处理游客登录请求(POST)");
		return processGuestLogin();
	}

	@GetMapping("/guest/login")
	public ResponseEntity<?> guestLoginGet() {
		log.info("处理游客登录请求(GET) - 重定向到POST处理");
		return processGuestLogin();
	}

	private ResponseEntity<?> processGuestLogin() {
		String guestUsername = guestService.generateGuestUsername();
		String guestPassword = "guest_password";

		guestService.saveGuestToRedis(guestUsername, guestPassword);

		String token = guestService.generateGuestToken(guestUsername);

		Map<String, String> response = new HashMap<>();
		response.put("token", token);
		response.put("type", "Bearer");
		response.put("message", "游客登录成功，有效期6小时");

		return ResponseEntity.ok(response);
	}

	@PostMapping("/register/send-code")
	public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		if (userService.existsByUsername(username)) {
			return ResponseEntity.badRequest().body("用户名已存在");
		}

		String verificationCode = generateVerificationCode();

		// 计算过期时间（1分钟后）
		long expirationTime = System.currentTimeMillis() + 60 * 1000;

		// 创建验证码实体并保存到数据库
		VerificationCode code = new VerificationCode();
		code.setUsername(username);
		code.setCode(verificationCode);
		code.setExpireTime(java.time.LocalDateTime.ofInstant(
		        java.time.Instant.ofEpochMilli(expirationTime),
		        java.time.ZoneId.systemDefault()
		));
		code.setCreateTime(java.time.LocalDateTime.now());
		code.setUpdateTime(java.time.LocalDateTime.now());
		verificationCodeService.saveVerificationCode(code);

		log.info("用户 {} 的注册验证码: {}", username, verificationCode);

		return ResponseEntity.ok("验证码已发送，有效期1分钟");
	}

	@PostMapping("/register/verify")
	public ResponseEntity<?> verifyAndRegister(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");
		String verificationCode = request.get("verificationCode");
		Boolean isAdmin = Boolean.valueOf(request.getOrDefault("isAdmin", "false"));

		if (username == null || username.isEmpty() || password == null || password.isEmpty() || verificationCode == null || verificationCode.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名、密码和验证码不能为空");
		}

		// 从数据库中获取验证码
		Optional<VerificationCode> storedCodeOptional = verificationCodeService.findByUsername(username);

		if (!storedCodeOptional.isPresent() || !storedCodeOptional.get().getCode().equals(verificationCode) || 
		    storedCodeOptional.get().getExpireTime().isBefore(java.time.LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("验证码无效或已过期");
		}

		try {
			User user = userService.registerUser(username, password, isAdmin);
			// 删除已使用的验证码
			verificationCodeService.deleteVerificationCode(storedCodeOptional.get().getId());
			return ResponseEntity.ok("注册成功");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	private String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}
	
}