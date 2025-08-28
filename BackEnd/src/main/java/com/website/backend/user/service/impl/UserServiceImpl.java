package com.website.backend.user.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.website.backend.user.entity.Role;
import com.website.backend.user.entity.User;
import com.website.backend.common.exception.ValidationException;
import com.website.backend.common.exception.BusinessException;
import com.website.backend.user.repository.RoleRepository;
import com.website.backend.user.repository.UserRepository;
import com.website.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final PasswordEncoder passwordEncoder;
	
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$");

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
public User registerUser(String username, String password) {
	return registerUser(username, password, false);
}

public User registerUser(String username, String password, boolean isAdmin) {
	log.info("开始注册用户: {}", username);
	
	// 参数验证
	validateUsername(username);
	validatePassword(password);
	
	if (userRepository.existsByUsername(username)) {
		log.error("用户名已存在: {}", username);
		throw new BusinessException("用户名已存在: " + username, "USER_EXISTS", null);
	}

	User user = new User();
	user.setUsername(username);
	user.setPassword(passwordEncoder.encode(password));
	log.info("用户信息创建完成: {}", username);

	Set<Role> roles = new HashSet<>();
	if (isAdmin) {
		Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> {
			log.error("角色不存在: ROLE_ADMIN");
			return new BusinessException("角色不存在: ROLE_ADMIN", "ROLE_NOT_FOUND", null);
		});
		roles.add(adminRole);
		log.info("用户 {} 被设置为管理员角色", username);
	} else {
		Role visitorRole = roleRepository.findByName("ROLE_VISITOR").orElseThrow(() -> {
			log.error("角色不存在: ROLE_VISITOR");
			return new BusinessException("角色不存在: ROLE_VISITOR", "ROLE_NOT_FOUND", null);
		});
		roles.add(visitorRole);
	}
	user.setRoles(roles);
	log.info("用户角色设置完成: {}, 角色: {}", username, roles);

	User savedUser = userRepository.save(user);
	log.info("用户注册成功: {}", savedUser.getUsername());
	return savedUser;
}

	@Override
	public Optional<User> findByUsername(String username) {
		log.info("查询用户: {}", username);
		return userRepository.findByUsername(username);
	}

	@Override
	public boolean existsByUsername(String username) {
		boolean exists = userRepository.existsByUsername(username);
		log.info("检查用户是否存在: {}, 结果: {}", username, exists);
		return exists;
	}
	
	/**
	 * 验证用户名格�?
	 * @param username 用户�?
	 */
	private void validateUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new ValidationException("用户名不能为空");
		}
		
		if (!USERNAME_PATTERN.matcher(username).matches()) {
			throw new ValidationException("用户名格式不正确，应为3-20位字母、数字或下划线");
		}
	}
	
	/**
	 * 验证密码强度
	 * @param password 密码
	 */
	private void validatePassword(String password) {
		if (password == null || password.isEmpty()) {
			throw new ValidationException("密码不能为空");
		}
		
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new ValidationException("密码必须至少8位，包含大小写字母和数字");
		}
	}

}
