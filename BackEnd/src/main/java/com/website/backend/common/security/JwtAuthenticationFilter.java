package com.website.backend.common.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import com.website.backend.common.config.SecurityPathConfig;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter implements Ordered {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtTokenProvider tokenProvider;
	private final UserDetailsService userDetailsService;

	private static final int FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 2; // 在JwtExceptionHandlerFilter之后执行
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public int getOrder() {
		return FILTER_ORDER;
	}

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
		this.tokenProvider = tokenProvider;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 检查是否为公开路径
		String requestURI = request.getRequestURI();
		
		logger.debug("检查请求URI: {}", requestURI);
		boolean isPublicPath = false;
		for (String publicPath : SecurityPathConfig.PUBLIC_PATHS) {
			if (pathMatcher.match(publicPath, requestURI)) {
				logger.debug("匹配成功: {} 对应 {}", publicPath, requestURI);
				isPublicPath = true;
				break;
			}
		}
		
		if (isPublicPath) {
			filterChain.doFilter(request, response);
			return;
		}

		// 非公开路径，需要JWT认证
		try {
			String jwt = parseJwt(request);
			logger.debug("解析JWT令牌: {}", jwt);
			if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
				String username = tokenProvider.getUserNameFromJwtToken(jwt);
				logger.debug("从JWT令牌中提取用户名: {}", username);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.debug("设置认证信息成功");
			} else {
				logger.debug("未提供有效的JWT令牌");
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * 从HTTP请求头中提取JWT令牌
	 * @param request HTTP请求对象
	 * @return 提取的JWT令牌，如果请求头中没有有效的JWT令牌则返回null
	 */
	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}
}