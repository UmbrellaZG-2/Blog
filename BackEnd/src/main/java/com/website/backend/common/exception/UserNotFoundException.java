package com.website.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 用户未找到异常类
 * 用于处理用户查询操作中未找到用户的情况
 */
public class UserNotFoundException extends BusinessException {
    
    /**
     * 构造用户未找到异常
     * @param message 异常消息
     */
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}