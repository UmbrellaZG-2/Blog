package com.website.backend.config;



import java.io.IOException;
import java.util.List;
import java.util.Enumeration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.website.backend.security.JwtAuthenticationFilter;
import com.website.backend.security.JwtTokenProvider;
import com.website.backend.service.impl.RedisUserDetailsService;

import io.micrometer.common.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.backend.security.JwtExceptionHandlerFilter;

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
	public JwtExceptionHandlerFilter jwtExceptionHandlerFilter(ObjectMapper objectMapper) {
		return new JwtExceptionHandlerFilter(objectMapper);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://101.200.43.186:8082", "http://101.200.43.186:8083", "http://localhost:8082", "http://localhost:8083"));
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
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 公开访问的API
                .requestMatchers("/api/articles").permitAll()
                .requestMatchers("/api/articles/*").permitAll()
                .requestMatchers("/api/attachments/download").permitAll()
                .requestMatchers("/api/attachments/download/*").permitAll()
                .requestMatchers("/api/attachments/article/get").permitAll()
                .requestMatchers("/api/attachments/article/get/*").permitAll()
                .requestMatchers("/api/auth/get").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/admin/login").permitAll()
                .requestMatchers("/api/auth/guest/login").permitAll()
                .requestMatchers("/api/auth/admin/register").permitAll()
                .requestMatchers("/api/home").permitAll()
                .requestMatchers("/api/home/*").permitAll()
                .requestMatchers("/api/images/article/*/cover/get").permitAll()
                .requestMatchers("/api/images/article/*/getAll").permitAll()
                .requestMatchers("/api/tags").permitAll()
                .requestMatchers("/api/tags/*").permitAll()
                .requestMatchers("/api/articles/categories/get").permitAll()
                .requestMatchers("/api/test/get").permitAll()
                .requestMatchers("/api/test/post").permitAll()
                
                // 管理员访问的API
                .requestMatchers("/api/articles/create").hasRole("ADMIN")
                .requestMatchers("/api/articles/update/*").hasRole("ADMIN")
                .requestMatchers("/api/articles/delete/*").hasRole("ADMIN")
                .requestMatchers("/api/articles/*/tags").hasRole("ADMIN")
                .requestMatchers("/api/articles/*/tags/*").hasRole("ADMIN")
                .requestMatchers("/api/attachments/upload").hasRole("ADMIN")
                .requestMatchers("/api/attachments/delete/*").hasRole("ADMIN")
                .requestMatchers("/api/attachments/get").hasRole("ADMIN")
                .requestMatchers("/api/images/article/*/cover/update").hasRole("ADMIN")
                .requestMatchers("/api/images/article/*/cover/delete").hasRole("ADMIN")
                
                // 隐藏API（不应该被外部直接访问）
                .requestMatchers("/api/auth/register/send-code").denyAll()
                .requestMatchers("/api/auth/register/verify").denyAll()
                
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
			protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
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