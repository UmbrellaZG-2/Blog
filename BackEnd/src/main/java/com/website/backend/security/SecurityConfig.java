package com.website.backend.security;

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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import io.micrometer.common.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
			"http://101.200.43.186:8082", 
			"http://101.200.43.186:8083", 
			"http://localhost:8082", 
			"http://localhost:8083",
			"http://localhost:8081",
			"http://101.200.43.186:8081"));
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
                // 公开接口 - 无需认证
                .requestMatchers(HttpMethod.GET, "/api/articles").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/articles/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/articles/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/articles/category/get/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/articles/*/comments/put").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/articles/*/comments/get").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/articles/categories/get").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/attachments/download/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/attachments/article/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/get").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/admin/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/guest/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/guest/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/home").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/home/redirect/aboutMe").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/home/aboutMe").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/images/article/*/cover/get").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/images/article/*/getAll").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tags/get").permitAll()
                
                // 管理员接口 - 需要认证
                .requestMatchers(HttpMethod.POST, "/api/articles/create").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/articles/update/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/articles/delete/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/articles/*/tags/put").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/articles/*/tags/delete/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/attachments/upload").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/attachments/delete/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/attachments/get").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/images/article/*/cover/update").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/images/article/*/cover/delete").hasRole("ADMIN")
                
                // 隐藏接口 - 特殊处理
                .requestMatchers(HttpMethod.POST, "/api/auth/admin/register").permitAll() // 实际在控制器中限制访问
                .requestMatchers(HttpMethod.POST, "/api/auth/register/send-code").permitAll() // 实际在控制器中限制访问
                .requestMatchers(HttpMethod.POST, "/api/auth/register/verify").permitAll() // 实际在控制器中限制访问
                
                // 其他所有请求拒绝访问
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
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