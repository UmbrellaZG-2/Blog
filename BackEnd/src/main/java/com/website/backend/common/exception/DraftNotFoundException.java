package com.website.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 草稿不存在异常
 * 当请求的草稿不存在时抛出
 */
public class DraftNotFoundException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "DRAFT_NOT_FOUND";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;
    
    /**
     * 构造草稿不存在异常
     * @param draftId 草稿ID
     */
    public DraftNotFoundException(String draftId) {
        super("草稿不存在，ID: " + draftId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造草稿不存在异常
     * @param message 异常消息
     * @param draftId 草稿ID
     */
    public DraftNotFoundException(String message, String draftId) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造草稿不存在异常
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param draftId 草稿ID
     */
    public DraftNotFoundException(String message, String errorCode, String draftId) {
        super(message, errorCode, DEFAULT_HTTP_STATUS);
    }
}