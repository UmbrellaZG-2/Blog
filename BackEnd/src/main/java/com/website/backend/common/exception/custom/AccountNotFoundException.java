package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 账号不存在异常
 * 当用户尝试登录一个不存在的账号时抛出
 */
public class AccountNotFoundException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "ACCOUNT_NOT_FOUND";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.UNAUTHORIZED;
    
    /**
     * 构造账号不存在异常
     * @param message 错误消息
     */
    public AccountNotFoundException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造账号不存在异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}