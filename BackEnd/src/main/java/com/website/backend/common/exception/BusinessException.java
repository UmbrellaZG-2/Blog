package com.website.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常基类
 * 所有业务相关的异常都应该继承此类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final int statusCode;
    private final HttpStatus httpStatus;
    private final String errorCode;
    
    /**
     * 构造业务异常
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     */
    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
    
    /**
     * 构造业务异常
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     * @param cause 异常原因
     */
    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
    
    /**
     * 获取错误代码
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取HTTP状态码
     * @return HTTP状态码
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    /**
     * 获取状态码
     * @return 状态码
     */
    public int getStatusCode() {
        return statusCode;
    }
}