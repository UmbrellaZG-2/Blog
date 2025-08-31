package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 数据验证失败异常
 * 当请求数据不符合验证规则时抛出
 */
public class DataValidationException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "DATA_VALIDATION_FAILED";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;
    
    /**
     * 构造数据验证失败异常
     * @param message 错误消息
     */
    public DataValidationException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造数据验证失败异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DataValidationException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}