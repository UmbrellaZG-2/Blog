package com.website.backend.article.exception;

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
     * @param articleId 文章ID
     */
    public ArticleNotFoundException(String articleId) {
        super("文章不存在，ID: " + articleId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文章不存在异常
     * @param articleId 文章ID
     * @param cause 异常原因
     */
    public ArticleNotFoundException(String articleId, Throwable cause) {
        super("文章不存在，ID: " + articleId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
    
    /**
     * 构造文章不存在异常
     * @param message 错误消息
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     */
    protected ArticleNotFoundException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
    
    /**
     * 构造文章不存在异常
     * @param message 错误消息
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     * @param cause 异常原因
     */
    protected ArticleNotFoundException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, errorCode, httpStatus, cause);
    }
}