package com.website.backend.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.website.backend.entity.User;

import com.website.backend.common.exception.custom.NotAdminException;
import com.website.backend.security.JwtTokenProvider;
import com.website.backend.service.GuestService;
import com.website.backend.service.UserService;
import com.website.backend.service.VerificationCodeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final GuestService guestService;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private VerificationCodeService verificationCodeService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsService userDetailsService;

	public AuthController(GuestService guestService, JwtTokenProvider jwtTokenProvider,
			AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
		this.guestService = guestService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/get")
	public ResponseEntity<?> getAuthInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Auth API root endpoint");
		response.put("available_endpoints", "/api/auth/login, /api/auth/admin/login, /api/auth/register, /api/auth/admin/register, /api/auth/guest/login");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		try {
			// 完全依赖AuthenticationManager进行认证
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);
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

	@PostMapping("/admin/login")
	public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("用户名和密码不能为空");
		}

		try {
			// 验证管理员凭?
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// 检查是否为管理员角?
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

			if (!isAdmin) {
				log.warn("用户 {} 不是管理员", username);
				throw new NotAdminException("用户没有管理员权限");
			}

			String jwt = jwtTokenProvider.generateToken(authentication);

			Map<String, String> response = new HashMap<>();
			response.put("token", jwt);
			response.put("type", "Bearer");
			response.put("message", "管理员登录成功");

			return ResponseEntity.ok(response);
		} catch (NotAdminException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在");
		} catch (Exception e) {
			log.error("管理员登录失败: {}", e.getMessage(), e);
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

		// 检查用户名是否已存在
		log.debug("检查用户名是否已存在");
		if (userService.existsByUsername(username)) {
			log.warn("用户名已存在: {}", username);
			return ResponseEntity.badRequest().body("用户名已存在");
		}

		// 注册管理员
		try {
			log.debug("开始注册管理员");
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
		// 生成游客用户名和密码
		String guestUsername = guestService.generateGuestUsername();
		String guestPassword = passwordEncoder.encode("guest_password");

		// 保存游客信息到数据库
		guestService.saveGuestToRedis(guestUsername, guestPassword);

		// 使用JWT生成token
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				guestUsername, null, List.of(new SimpleGrantedAuthority("ROLE_VISITOR")));
		String token = jwtTokenProvider.generateToken(authentication);

		Map<String, String> response = new HashMap<>();
		response.put("token", token);
		response.put("type", "Bearer");
		response.put("message", "游客登录成功，有效期6小时");

		return ResponseEntity.ok(response);
	}

	@PostMapping("/register/send-code")
	public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");

		if (email == null || email.isEmpty()) {
			return ResponseEntity.badRequest().body("邮箱不能为空");
		}

		String verificationCode = verificationCodeService.generateCode(email);

		log.debug("用户 {} 的注册验证码已生成并存储到Redis中", email);

		return ResponseEntity.ok("验证码已发送，有效期5分钟");
	}

	@PostMapping("/register/verify")
	public ResponseEntity<?> verifyAndRegister(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String password = request.get("password");
		String verificationCode = request.get("verificationCode");
		Boolean isAdmin = Boolean.valueOf(request.getOrDefault("isAdmin", "false"));

		if (email == null || email.isEmpty() || password == null || password.isEmpty()
				|| verificationCode == null || verificationCode.isEmpty()) {
			return ResponseEntity.badRequest().body("邮箱、密码和验证码不能为空");
		}

		// 检查验证码是否有效
		boolean isValid = verificationCodeService.validateCode(email, verificationCode);

		if (!isValid) {
			return ResponseEntity.badRequest().body("验证码无效或已过期");
		}

		// 注册用户
		try {
			User user = userService.registerUser(email, password, isAdmin);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok("注册成功");
	}
}