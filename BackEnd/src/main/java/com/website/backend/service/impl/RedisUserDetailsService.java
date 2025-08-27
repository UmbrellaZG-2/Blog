package com.website.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.website.backend.entity.Role;
import com.website.backend.repository.jpa.UserRepository;
import com.website.backend.repository.jpa.RoleRepository;
import org.springframework.security.core.userdetails.User;
import java.util.List;
import com.website.backend.entity.GuestUser;
import com.website.backend.service.GuestUserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisUserDetailsService implements UserDetailsService {

	private static final String GUEST_PREFIX = "guest_";

	private String maskUsername(String username) {
		if (username == null) {
			return null;
		}

		if (username.startsWith(GUEST_PREFIX)) {
			return GUEST_PREFIX + "***";
		}
		else if (username.length() <= 3) {
			return "***";
		}
		else {
			return username.substring(0, 3) + "***";
		}
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private GuestUserService guestUserService;

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("尝试加载用户: {}", maskUsername(username));

		if (username.startsWith(GUEST_PREFIX)) {
			log.info("用户 {} 是游客，尝试从数据库加载", maskUsername(username));
			return loadGuestUserFromDatabase(username);
		}
		else {
			log.info("用户 {} 是普通用户，尝试从数据库加载", maskUsername(username));
			return loadRegularUserFromDatabase(username);
		}
    }

	private UserDetails loadGuestUserFromDatabase(String username) {
		GuestUser guestUser = guestUserService.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("游客不存在: " + username));

		Role visitorRole = roleRepository.findByName("ROLE_VISITOR")
			.orElseThrow(() -> new UsernameNotFoundException("游客角色不存在"));

		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(visitorRole.getName()));

		log.info("成功从数据库加载游客 {}", maskUsername(username));
		return new User(guestUser.getUsername(), guestUser.getPassword(), authorities);
    }

	private UserDetails loadRegularUserFromDatabase(String username) {
		com.website.backend.entity.User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

		List<SimpleGrantedAuthority> authorities = user.getRoles()
			.stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.toList();

		log.info("成功从数据库加载用户 {}", maskUsername(username));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}

}