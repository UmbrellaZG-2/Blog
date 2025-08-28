package com.website.backend.system.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.website.backend.user.entity.Role;
import com.website.backend.system.entity.GuestUser;
import com.website.backend.user.repository.RoleRepository;
import com.website.backend.system.service.GuestService;
import com.website.backend.system.service.GuestUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestUserService guestUserService;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${app.guest.expirationMs}")
    private long guestExpirationMs;

    private static final String GUEST_PREFIX = "guest_";

    @Override
    public String generateGuestUsername() {
        return GUEST_PREFIX + UUID.randomUUID();
    }

    @Override
    public void saveGuestToRedis(String username, String password) {
        log.info("保存游客信息到数据库: {}", username);

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

        // 创建游客用户实体
        GuestUser guestUser = new GuestUser();
        guestUser.setUsername(username);
        guestUser.setPassword(password);
        guestUser.setRole(visitorRole.getName());
        
        // 计算过期时间
        long expirationTimeMillis = System.currentTimeMillis() + guestExpirationMs;
        guestUser.setExpireTime(java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(expirationTimeMillis),
                java.time.ZoneId.systemDefault()
        ));
        guestUser.setCreateTime(java.time.LocalDateTime.now());
        guestUser.setUpdateTime(java.time.LocalDateTime.now());

        // 保存到数据库
        guestUserService.saveGuestUser(guestUser);

        log.info("游客信息已保存到数据库，过期时间: {}毫秒", guestExpirationMs);
    }

    @Override
    public Map<String, Object> getGuestFromRedis(String username) {
        Map<String, Object> result = new HashMap<>();
        Optional<GuestUser> guestUserOptional = guestUserService.findByUsername(username);
        if (guestUserOptional.isPresent()) {
            GuestUser guestUser = guestUserOptional.get();
            result.put("username", guestUser.getUsername());
            result.put("password", guestUser.getPassword());
            result.put("role", guestUser.getRole());
        }
        return result;
    }

    @Override
    public boolean existsInRedis(String username) {
        return guestUserService.existsByUsername(username);
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
