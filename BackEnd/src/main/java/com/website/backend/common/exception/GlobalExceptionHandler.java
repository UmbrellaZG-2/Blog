package com.website.backend.common.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.website.backend.article.exception.TagNotFoundException;
import com.website.backend.common.constant.HttpStatusConstants;
import com.website.backend.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 处理业务异常基类
	@ExceptionHandler(BusinessException.class)
	public ApiResponse<?> handleBusinessException(BusinessException e) {
		logger.error("业务异常: {}, 错误�? {}", e.getMessage(), e.getErrorCode());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", e.getErrorCode());
		errorInfo.put("statusCode", e.getStatusCode());
		return ApiResponse.fail(e.getStatusCode(), e.getMessage(), errorInfo);
	}

	// 处理文章不存在异�?
	@ExceptionHandler(ArticleNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleArticleNotFoundException(ArticleNotFoundException e) {
		logger.error("文章不存在异�? {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", e.getErrorCode());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理草稿不存在异�?
	@ExceptionHandler(DraftNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleDraftNotFoundException(DraftNotFoundException e) {
		logger.error("草稿不存在异�? {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", e.getErrorCode());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	// 处理标签不存在异�?
	@ExceptionHandler(TagNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleTagNotFoundException(TagNotFoundException e) {
		logger.error("标签不存在异�? {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", e.getErrorCode());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleException(Exception e) {
		logger.error("发生未预期的异常: {}", e.getMessage(), e);
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "SERVER_ERROR");
		errorInfo.put("stackTrace", e.getStackTrace()[0].toString());
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "服务器内部错�? " + e.getMessage(), errorInfo);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleResourceNotFound(ResourceNotFoundException e) {
		logger.error("资源不存�? {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "RESOURCE_NOT_FOUND");
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage(), errorInfo);
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

	@ExceptionHandler(CustomAuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<?> handleAuthenticationException(CustomAuthenticationException e) {
		logger.error("认证失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "AUTHENTICATION_FAILED");
		return ApiResponse.fail(HttpStatusConstants.UNAUTHORIZED, "认证失败: 用户名或密码错误", errorInfo);
	}

	@ExceptionHandler(FileUploadException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<?> handleFileUploadException(FileUploadException e) {
		logger.error("文件上传失败: {}", e.getMessage());
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "FILE_UPLOAD_FAILED");
		return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, "文件上传失败: " + e.getMessage(), errorInfo);
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleIOException(IOException e) {
		logger.error("IO异常: {}", e.getMessage(), e);
		Map<String, Object> errorInfo = new HashMap<>();
		errorInfo.put("errorCode", "IO_ERROR");
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "文件操作失败: " + e.getMessage(), errorInfo);
	}

}