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
	// 路径匹配器，用于匹配URL路径模式
	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

	// 公开路径列表，不需要JWT认证
	private static final List<String> PUBLIC_PATHS = Arrays.asList(
			"/api/articles/categories",
			"/api/articles/categories/**",
			"/api/articles/category",
			"/api/articles/category/**",
			"/api/tags",
			"/api/tags/**",
			"/api/attachments",
			"/api/attachments/**",
			"/auth/**",
			"/login",
			"/",
			"/aboutMe",
			"/aboutMe.html",
			"/aboutme",
			"/articles/**",
			"/guestbook/**",
			"/api/query/**",
			"/api/search/**"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 检查是否是公开路径
		String requestPath = request.getRequestURI();
		log.info("请求路径: {}", requestPath);
		boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path -> PATH_MATCHER.match(path, requestPath));
		log.info("是否公开路径: {}", isPublicPath);

		if (isPublicPath) {
			// 公开路径直接放行
			log.info("公开路径，直接放行: {}", requestPath);
			filterChain.doFilter(request, response);
			return;
		}

		// 非公开路径进行JWT认证
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
				// 没有有效的JWT令牌，拒绝请求
				log.info("非公开路径，没有有效的JWT令牌，拒绝请求: {}", requestPath);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Unauthorized: No valid JWT token provided");
			}
		}
		catch (Exception e) {
			log.error("无法设置用户认证: {}", e.getMessage());
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