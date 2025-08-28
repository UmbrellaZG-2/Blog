package com.website.backend.article.exception;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 标签不存在异常
 * 当请求的标签不存在时抛出
 */
public class TagNotFoundException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "TAG_NOT_FOUND";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;
    
    /**
     * 构造标签不存在异常
     * @param tagId 标签ID
     */
    public TagNotFoundException(Long tagId) {
        super("标签不存在，ID: " + tagId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造标签不存在异常
     * @param message 异常消息
     * @param tagId 标签ID
     */
    public TagNotFoundException(String message, Long tagId) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造标签不存在异常
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param tagId 标签ID
     */
    public TagNotFoundException(String message, String errorCode, Long tagId) {
        super(message, errorCode, DEFAULT_HTTP_STATUS);
    }
}