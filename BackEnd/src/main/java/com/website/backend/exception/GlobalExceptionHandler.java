package com.website.backend.exception;

import com.website.backend.common.exception.custom.*;
import com.website.backend.constant.HttpStatusConstants;
import com.website.backend.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleException(Exception e) {
		logger.error("发生未预期的异常: {}", e.getMessage(), e);
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "服务器内部错误: " + e.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleResourceNotFound(ResourceNotFoundException e) {
		logger.error("资源不存在: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<?> handleAccessDenied(AccessDeniedException e) {
		logger.error("权限不足: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.FORBIDDEN, "没有足够的权限执行此操作");
	}

	@ExceptionHandler(CustomAuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<?> handleAuthenticationException(CustomAuthenticationException e) {
		logger.error("认证失败: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.UNAUTHORIZED, "认证失败: 用户名或密码错误");
	}

	@ExceptionHandler(FileUploadException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<?> handleFileUploadException(FileUploadException e) {
		logger.error("文件上传失败: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, "文件上传失败: " + e.getMessage());
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleIOException(IOException e) {
		logger.error("IO异常: {}", e.getMessage(), e);
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "文件操作失败: " + e.getMessage());
	}

	// 新增自定义异常处理
	
	@ExceptionHandler(PasswordErrorException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<?> handlePasswordError(PasswordErrorException e) {
		logger.error("密码错误: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<?> handleAccountNotFound(AccountNotFoundException e) {
		logger.error("账号不存在: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(NotAdminException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<?> handleNotAdmin(NotAdminException e) {
		logger.error("用户不是管理员: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.FORBIDDEN, e.getMessage());
	}

	@ExceptionHandler(com.website.backend.common.exception.custom.ArticleNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleArticleNotFound(com.website.backend.common.exception.custom.ArticleNotFoundException e) {
		logger.error("文章不存在: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(ArticlePermissionDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<?> handleArticlePermissionDenied(ArticlePermissionDeniedException e) {
		logger.error("文章权限不足: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.FORBIDDEN, e.getMessage());
	}

	@ExceptionHandler(DataValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<?> handleDataValidation(DataValidationException e) {
		logger.error("数据验证失败: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(FileOperationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<?> handleFileOperation(FileOperationException e) {
		logger.error("文件操作失败: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	@ExceptionHandler(CommentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleCommentNotFound(CommentNotFoundException e) {
		logger.error("评论不存在: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(TagNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<?> handleTagNotFound(TagNotFoundException e) {
		logger.error("标签不存在: {}", e.getMessage());
		return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, e.getMessage());
	}
}