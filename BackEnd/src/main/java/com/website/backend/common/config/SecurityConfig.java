package com.website.backend.common.config;

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

import com.website.backend.common.config.SecurityPathConfig;
import com.website.backend.common.security.JwtAuthenticationFilter;
import com.website.backend.common.security.JwtExceptionHandlerFilter;
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
            .authorizeHttpRequests(auth -> {
                // 配置公开路径，允许所有HTTP方法访问
                for (String publicPath : SecurityPathConfig.PUBLIC_PATHS) {
                    auth.requestMatchers(publicPath).permitAll();
                }
                
                // 配置需要管理员权限的路�?
                for (String adminPath : SecurityPathConfig.ADMIN_PATHS) {
                    auth.requestMatchers(adminPath).hasRole("ADMIN");
                }
                
                // 其他所有请求都需要认�?
                auth.anyRequest().authenticated();
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}