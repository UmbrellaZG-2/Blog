package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 用户不是管理员异常
 * 当普通用户尝试访问需要管理员权限的接口时抛出
 */
public class NotAdminException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "NOT_ADMIN";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.FORBIDDEN;
    
    /**
     * 构造用户不是管理员异常
     * @param message 错误消息
     */
    public NotAdminException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造用户不是管理员异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public NotAdminException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}