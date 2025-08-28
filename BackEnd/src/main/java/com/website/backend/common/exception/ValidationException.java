package com.website.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 参数验证异常�? * 用于处理参数验证失败的情�? */
public class ValidationException extends BusinessException {
    
    /**
     * 构造参数验证异�?     * @param message 异常消息
     */
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 构造参数验证异�?     * @param message 异常消息
     * @param cause 异常原因
     */
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        initCause(cause);
    }
}