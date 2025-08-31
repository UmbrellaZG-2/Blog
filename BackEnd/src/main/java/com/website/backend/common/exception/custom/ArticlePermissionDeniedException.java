package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 文章权限不足异常
 * 当用户尝试访问或操作没有权限的文章时抛出
 */
public class ArticlePermissionDeniedException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "ARTICLE_PERMISSION_DENIED";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.FORBIDDEN;
    
    /**
     * 构造文章权限不足异常
     * @param message 错误消息
     */
    public ArticlePermissionDeniedException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文章权限不足异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ArticlePermissionDeniedException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}