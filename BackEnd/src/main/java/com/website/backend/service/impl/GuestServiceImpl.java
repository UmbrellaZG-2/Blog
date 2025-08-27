package com.website.backend.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.website.backend.entity.Role;
import com.website.backend.repository.jpa.RoleRepository;
import com.website.backend.service.GuestService;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Service
public class GuestServiceImpl implements GuestService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RoleRepository roleRepository;

	@Value("${app.guest.expirationMs}")
	private long guestExpirationMs;

	private static final String GUEST_PREFIX = "guest_";

	private static final String REDIS_KEY_PREFIX = "guest:";

	@Override
	public String generateGuestUsername() {
		return GUEST_PREFIX + UUID.randomUUID();
	}

	@Override
	public void saveGuestToRedis(String username, String password) {
		log.info("保存游客信息到Redis: {}", username);

		log.info("开始查询ROLE_VISITOR角色");
		Optional<Role> visitorRoleOptional = roleRepository.findByName("ROLE_VISITOR");
		if (!visitorRoleOptional.isPresent()) {
			log.warn("游客角色不存在，正在创建ROLE_VISITOR角色");
			Role visitorRole = new Role();
			visitorRole.setName("ROLE_VISITOR");
			visitorRole = roleRepository.save(visitorRole);
			log.info("已创建ROLE_VISITOR角色: {}", visitorRole.getName());
			visitorRoleOptional = Optional.of(visitorRole);
		} else {
			log.info("成功查询到ROLE_VISITOR角色");
		}
		Role visitorRole = visitorRoleOptional.orElseThrow(() -> new RuntimeException("游客角色获取失败"));

		Map<String, Object> guestInfo = new HashMap<>();
		guestInfo.put("username", username);
		guestInfo.put("password", password);
		guestInfo.put("role", visitorRole.getName());

		String redisKey = REDIS_KEY_PREFIX + username;
		redisTemplate.opsForHash().putAll(redisKey, guestInfo);
		redisTemplate.expire(redisKey, guestExpirationMs, TimeUnit.MILLISECONDS);

		log.info("游客信息已保存到Redis，过期时间: {}毫秒", guestExpirationMs);
	}

	@Override
	public Map<String, Object> getGuestFromRedis(String username) {
		String redisKey = REDIS_KEY_PREFIX + username;
		Map<String, Object> result = new HashMap<>();
		redisTemplate.opsForHash().entries(redisKey).forEach((key, value) -> {
			if (key instanceof String) {
				result.put((String) key, value);
			}
		});
		return result;
	}

	@Override
	public boolean existsInRedis(String username) {
		String redisKey = REDIS_KEY_PREFIX + username;
		return redisTemplate.hasKey(redisKey);
	}

	@Override
	public String generateGuestToken(String username) {
		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException("用户名不能为空");
		}

		log.info("为游客生成访问标识: {}", username);

		Map<String, Object> guestInfo = getGuestFromRedis(username);
		if (guestInfo.isEmpty()) {
			throw new RuntimeException("游客信息不存在于Redis");
		}

		return username;
	}
}