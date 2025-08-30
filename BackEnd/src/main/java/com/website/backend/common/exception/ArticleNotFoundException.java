package com.website.backend.common.exception;

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
     * @param articleId 文章ID
     */
    public ArticleNotFoundException(String articleId) {
        super("文章不存在，ID: " + articleId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文章不存在异常
     * @param message 异常消息
     * @param articleId 文章ID
     */
    public ArticleNotFoundException(String message, String articleId) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文章不存在异常
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param articleId 文章ID
     */
    public ArticleNotFoundException(String message, String errorCode, String articleId) {
        super(message, errorCode, DEFAULT_HTTP_STATUS);
    }
}