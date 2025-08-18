package com.website.backend.config;



import java.io.IOException;
import java.util.List;
import java.util.Enumeration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.website.backend.security.JwtAuthenticationFilter;
import com.website.backend.security.JwtTokenProvider;
import com.website.backend.service.impl.RedisUserDetailsService;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

	private final RedisUserDetailsService redisUserDetailsService;

	public SecurityConfig(RedisUserDetailsService redisUserDetailsService) {
		this.redisUserDetailsService = redisUserDetailsService;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return redisUserDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService());
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("https://101.200.43.186:8082"));
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth
			.requestMatchers("/api/tags", "/api/tags/**", "/api/articles/categories", "/auth/**", "/attachments/**")
			.permitAll()
			.requestMatchers("/admin/**")
			.hasRole("ADMIN")
			.anyRequest()
			.authenticated())
		.sessionManagement(session -> session
			.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
		.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 请求日志过滤器，用于记录请求详细信息
	 */
	@Bean
	public OncePerRequestFilter requestLoggingFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
				// 包装请求以缓存请求体
				ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
				
				log.info("接收到请求: {} {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());
				
				// 记录请求头
				Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
				StringBuilder headers = new StringBuilder();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					headers.append(headerName).append(": ").append(wrappedRequest.getHeader(headerName)).append(", ");
				}
				if (headers.length() > 0) {
					headers.setLength(headers.length() - 2); // 移除最后一个逗号和空格
				}
				log.info("请求头: {}", headers.toString());
				
				// 记录请求参数（URL参数）
				log.info("请求参数: {}", wrappedRequest.getParameterMap());
				
				// 记录请求体（如果是POST请求且内容类型是JSON）
				if ("POST".equals(wrappedRequest.getMethod()) &&
						wrappedRequest.getContentType() != null &&
						wrappedRequest.getContentType().contains("application/json")) {
					byte[] requestBody = wrappedRequest.getContentAsByteArray();
					if (requestBody.length > 0) {
						String body = new String(requestBody, wrappedRequest.getCharacterEncoding());
						log.info("请求体: {}", body);
					}
				}
				
				log.info("请求IP: {}", wrappedRequest.getRemoteAddr());

				// 继续过滤链
				filterChain.doFilter(wrappedRequest, response);

				log.info("请求处理完成: {} {}，状态码: {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI(), response.getStatus());
			}
		};
	}

}