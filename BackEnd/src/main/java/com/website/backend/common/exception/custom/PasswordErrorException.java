package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 密码错误异常
 * 当用户登录时提供的密码不正确时抛出
 */
public class PasswordErrorException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "INVALID_PASSWORD";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.UNAUTHORIZED;
    
    /**
     * 构造密码错误异常
     * @param message 错误消息
     */
    public PasswordErrorException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造密码错误异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public PasswordErrorException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}