package com.website.backend.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT认证过滤器 用于从HTTP请求中提取JWT令牌，验证其有效性，并设置用户认证信息到SecurityContext
 * 该过滤器继承自OncePerRequestFilter，确保每个请求只被过滤一次
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;

	private final UserDetailsService userDetailsService;

	/**
	 * 构造函数注入依赖
	 * @param tokenProvider JWT令牌提供器，用于生成和验证JWT令牌
	 * @param userDetailsService 用户详情服务，用于加载用户信息
	 */
	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
		this.tokenProvider = tokenProvider;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * 执行过滤器逻辑 从请求头中提取JWT令牌，验证其有效性，如果有效则设置用户认证信息
	 * @param request HTTP请求对象，用于获取请求头中的JWT令牌
	 * @param response HTTP响应对象
	 * @param filterChain 过滤器链，用于继续执行后续过滤器
	 * @throws ServletException 如果发生Servlet相关异常
	 * @throws IOException 如果发生IO异常
	 */
	// 公开路径列表，与SecurityConfig中的配置保持一致
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/articles/**",
        "/api/attachments/download/**",
        "/api/attachments/article/get/**",
        "/api/auth/get",
        "/api/auth/login",
        "/api/auth/admin/login",
        "/api/auth/guest/login",
        "/api/auth/admin/register",
        "/api/home/**",
        "/api/images/article/**/cover/get",
        "/api/images/article/**/getAll",
        "/api/tags/**",
        "/api/articles/categories/get",
        "/api/test/get",
        "/api/test/post"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 检查是否为公开路径
		String requestURI = request.getRequestURI();
		for (String publicPath : PUBLIC_PATHS) {
			if (pathMatcher.match(publicPath, requestURI)) {
				filterChain.doFilter(request, response);
				return;
			}
		}

		// 非公开路径，需要JWT认证
		try {
			String jwt = parseJwt(request);
			if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
				String username = tokenProvider.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Unauthorized: No valid JWT token provided");
			}
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Unauthorized: Invalid JWT token");
		}
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