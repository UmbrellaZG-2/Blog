package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 文章不存在异常
 * 当请求的文章不存在时抛出
 */
public class ArticleNotFoundException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "ARTICLE_NOT_FOUND";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;
    
    /**
     * 构造文章不存在异常
     * @param message 错误消息
     */
    public ArticleNotFoundException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文章不存在异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ArticleNotFoundException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}