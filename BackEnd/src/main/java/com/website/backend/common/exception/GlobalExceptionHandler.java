package com.website.backend.common.exception;

import com.website.backend.article.exception.ArticleNotFoundException;
import com.website.backend.article.exception.DraftNotFoundException;
import com.website.backend.comment.exception.CommentNotFoundException;
import com.website.backend.common.exception.custom.*;
import com.website.backend.file.exception.FileUploadException;
import com.website.backend.common.constant.HttpStatusConstants;
import com.website.backend.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleException(Exception e) {
		logger.error("发生未预期的异常: {}", e.getMessage(), e);
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "SYS-5001");
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "系统服务暂时不可用，请稍后再试", errorInfo);
	}

	// 处理资源未找到异常
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleResourceNotFound(ResourceNotFoundException e) {
		logger.error("资源不存在: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "RESOURCE_NOT_FOUND");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理文章未找到异常
	@ExceptionHandler(ArticleNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleArticleNotFound(ArticleNotFoundException e) {
		logger.error("文章不存在: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "ARTICLE_NOT_FOUND");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理草稿未找到异常
	@ExceptionHandler(DraftNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleDraftNotFound(DraftNotFoundException e) {
		logger.error("草稿不存在: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "DRAFT_NOT_FOUND");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理评论未找到异常
	@ExceptionHandler(CommentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleCommentNotFound(CommentNotFoundException e) {
		logger.error("评论不存在: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "COMMENT_NOT_FOUND");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理标签未找到异常
	@ExceptionHandler(TagNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleTagNotFound(TagNotFoundException e) {
		logger.error("标签不存在: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "TAG_NOT_FOUND");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理文章权限不足异常
	@ExceptionHandler(com.website.backend.common.exception.custom.ArticlePermissionDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<?> handleArticlePermissionDenied(com.website.backend.common.exception.custom.ArticlePermissionDeniedException e) {
		logger.error("文章权限不足: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "ARTICLE_PERMISSION_DENIED");
		return ApiResponse.fail(HttpStatusConstants.FORBIDDEN, e.getMessage(), errorInfo);
	}

	// 处理请求的资源未找到异常(Spring Boot 3.x)
	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleNoResourceFoundException(NoResourceFoundException e) {
		logger.error("请求的资源未找到: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "RES-3001");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "请求的资源不存在", errorInfo);
	}

	// 处理权限不足异常
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<?> handleAccessDenied(AccessDeniedException e) {
		logger.error("权限不足: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "ACCESS_DENIED");
		return ApiResponse.fail(HttpStatusConstants.FORBIDDEN, "没有足够的权限执行此操作", errorInfo);
	}

	// 处理认证异常
	@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<?> handleAuthenticationException(org.springframework.security.core.AuthenticationException e) {
		logger.error("认证失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "AUTH-1001");
		return ApiResponse.fail(HttpStatusConstants.UNAUTHORIZED, "认证失败，请检查用户名或密码", errorInfo);
	}

	// 处理自定义认证异常
	@ExceptionHandler(CustomAuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<?> handleCustomAuthenticationException(CustomAuthenticationException e) {
		logger.error("自定义认证失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "AUTH-1002");
		return ApiResponse.fail(HttpStatusConstants.UNAUTHORIZED, "认证失败，请重新登录", errorInfo);
	}

	// 处理文件上传异常
	@ExceptionHandler(FileUploadException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<?> handleFileUploadException(FileUploadException e) {
		logger.error("文件上传失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "FILE-2001");
		return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, "文件上传失败，请检查文件格式或大小", errorInfo);
	}

	// 处理文件处理异常
	@ExceptionHandler(FileProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleFileProcessingException(FileProcessingException e) {
		logger.error("文件处理失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "FILE-2002");
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "文件处理失败，请稍后重试", errorInfo);
	}

	// 处理IO异常
	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleIOException(IOException e) {
		logger.error("IO异常: {}", e.getMessage(), e);
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "SYS-5002");
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "系统IO操作失败，请稍后重试", errorInfo);
	}

	// 处理数据验证异常
	@ExceptionHandler(DataValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<?> handleDataValidation(DataValidationException e) {
		logger.error("数据验证失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "DATA_VALIDATION_FAILED");
		return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, e.getMessage(), errorInfo);
	}

	// 处理业务异常
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<?> handleBusinessException(BusinessException e) {
		logger.error("业务异常: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", e.getErrorCode());
		return ApiResponse.fail(e.getHttpStatus().value(), e.getMessage(), errorInfo);
	}

	// 处理类型转换异常
	@ExceptionHandler(ClassCastException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleClassCastException(ClassCastException e) {
		logger.error("类型转换异常: {}", e.getMessage(), e);
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "AUTH-1003");
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "登录状态异常，请重新尝试", errorInfo);
	}
}