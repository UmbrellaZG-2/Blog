package com.website.backend.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;
	
	@Value("${app.jwtRefreshExpirationMs}")
	private long jwtRefreshExpirationMs;

	public String generateToken(Authentication authentication) {
		return generateTokenWithExpiration(authentication, jwtExpirationMs);
	}
	
	/**
	 * 生成刷新令牌
	 * @param authentication 认证信息
	 * @return 刷新令牌
	 */
	public String generateRefreshToken(Authentication authentication) {
		return generateTokenWithExpiration(authentication, jwtRefreshExpirationMs);
	}

	/**
	 * 生成具有自定义过期时间的JWT令牌
	 * @param authentication 认证信息
	 * @param expirationMs 过期时间(毫秒)
	 * @return JWT令牌
	 */
	public String generateTokenWithExpiration(Authentication authentication, long expirationMs) {
		Object principal = authentication.getPrincipal();
		String username;
		
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof String) {
			username = (String) principal;
		} else {
			throw new ClassCastException("无法将类型 " + principal.getClass().getName() + " 转换为 UserDetails 或 String");
		}
		
		// 获取用户权限
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		return Jwts.builder()
			.subject(username)
			.claim("authorities", authorities)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + expirationMs))
			.signWith(getSignInKey())
			.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}
	
	/**
	 * 从JWT令牌中获取用户权限
	 * @param token JWT令牌
	 * @return 用户权限列表
	 */
	public String getAuthoritiesFromJwtToken(String token) {
		return (String) Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload().get("authorities");
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(authToken);
			return true;
		}
		catch (JwtException e) {
			logger.error("JWT无效: {}", e.getMessage());
		}
		catch (IllegalArgumentException e) {
			logger.error("JWT令牌参数非法: {}", e.getMessage());
		}
		return false;
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	/**
	 * 验证用户是否有特定权限
	 * @param token JWT令牌
	 * @param authority 权限
	 * @return 是否有权限
	 */
	public boolean hasAuthority(String token, String authority) {
		try {
			String authorities = getAuthoritiesFromJwtToken(token);
			if (authorities != null) {
				return authorities.contains(authority);
			}
		} catch (Exception e) {
			logger.error("验证权限时出错 {}", e.getMessage());
		}
		return false;
	}

}