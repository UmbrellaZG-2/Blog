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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtExceptionHandlerFilter jwtExceptionHandlerFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtExceptionHandlerFilter = jwtExceptionHandlerFilter;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
			"http://101.200.43.186:8081", 
			"http://101.200.43.186:8082", 
			"http://101.200.43.186:8083",
			"http://localhost:8081",
			"http://localhost:8082", 
			"http://localhost:8083"));
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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/articles").permitAll()
                .requestMatchers(HttpMethod.GET, "/articles/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/articles/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/articles/category/get/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/articles/*/comments/put").permitAll()
                .requestMatchers(HttpMethod.GET, "/articles/*/comments/get").permitAll()
                .requestMatchers(HttpMethod.GET, "/articles/categories/get").permitAll()
                .requestMatchers(HttpMethod.GET, "/attachments/download/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/attachments/article/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/auth/get").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/admin/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/auth/guest/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/guest/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/home").permitAll()
                .requestMatchers(HttpMethod.GET, "/home/redirect/aboutMe").permitAll()
                .requestMatchers(HttpMethod.GET, "/home/aboutMe").permitAll()
                .requestMatchers(HttpMethod.GET, "/images/article/*/cover/get").permitAll()
                .requestMatchers(HttpMethod.GET, "/images/article/*/getAll").permitAll()
                .requestMatchers(HttpMethod.GET, "/tags/get").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/articles/create").hasRole("ADMIN")
		.requestMatchers(HttpMethod.PUT, "/articles/update/**").hasRole("ADMIN")
		.requestMatchers(HttpMethod.DELETE, "/articles/delete/**").hasRole("ADMIN")
		.requestMatchers(HttpMethod.POST, "/articles/*/tags/put").hasRole("ADMIN")
		.requestMatchers(HttpMethod.DELETE, "/articles/*/tags/delete/**").hasRole("ADMIN")
		.requestMatchers(HttpMethod.POST, "/attachments/upload").hasRole("ADMIN")
		.requestMatchers(HttpMethod.DELETE, "/attachments/delete/**").hasRole("ADMIN")
		.requestMatchers(HttpMethod.GET, "/attachments/get").hasRole("ADMIN")
		.requestMatchers(HttpMethod.POST, "/images/article/*/cover/update").hasRole("ADMIN")
		.requestMatchers(HttpMethod.DELETE, "/images/article/*/cover/delete").hasRole("ADMIN")
		
		.requestMatchers(HttpMethod.POST, "/auth/admin/register").permitAll()
		.requestMatchers(HttpMethod.POST, "/auth/register/send-code").permitAll()
		.requestMatchers(HttpMethod.POST, "/auth/register/verify").permitAll()
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

	@Bean
	public OncePerRequestFilter requestLoggingFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
					throws ServletException, IOException {
				ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
				
				log.info("接收到请求: {} {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());
				
				Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
				StringBuilder headers = new StringBuilder();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					headers.append(headerName).append(": ").append(wrappedRequest.getHeader(headerName)).append(", ");
				}
				if (headers.length() > 0) {
					headers.setLength(headers.length() - 2);
				}
				log.info("请求头: {}", headers.toString());
				
				log.info("请求参数: {}", wrappedRequest.getParameterMap());
				
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

				filterChain.doFilter(wrappedRequest, response);

				log.info("请求处理完成: {} {}，状态码: {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI(), response.getStatus());
			}
		};
	}

}