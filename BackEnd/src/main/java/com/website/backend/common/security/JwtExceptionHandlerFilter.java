package com.website.backend.common.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.backend.common.constant.HttpStatusConstants;
import com.website.backend.common.exception.CustomAuthenticationException;
import com.website.backend.common.model.ApiResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT异常处理过滤器，用于处理JwtAuthenticationFilter中抛出的异常
 */
@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter implements Ordered {

	private static final int FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 1; // 在JwtAuthenticationFilter之前执行

	@Override
	public int getOrder() {
		return FILTER_ORDER;
	}

	private static final Logger logger = LoggerFactory.getLogger(JwtExceptionHandlerFilter.class);
	private final ObjectMapper objectMapper;

	public JwtExceptionHandlerFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (CustomAuthenticationException e) {
			logger.error("JWT认证异常: {}", e.getMessage());
			handleException(response, HttpStatus.UNAUTHORIZED, HttpStatusConstants.UNAUTHORIZED,
					"认证失败: " + e.getMessage());
		} catch (Exception e) {
			logger.error("JWT处理异常: {}", e.getMessage(), e);
			handleException(response, HttpStatus.INTERNAL_SERVER_ERROR,
					HttpStatusConstants.INTERNAL_SERVER_ERROR, "服务器内部错误: " + e.getMessage());
		}
	}

	private void handleException(HttpServletResponse response, HttpStatus httpStatus, int statusCode, String message)
			throws IOException {
		response.setStatus(httpStatus.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ApiResponse<?> apiResponse = ApiResponse.fail(statusCode, message);
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}
}