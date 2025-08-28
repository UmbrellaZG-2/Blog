package com.website.backend.user.exception;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 用户未找到异常类
 * 用于处理用户查询操作中未找到用户的情况
 */
public class UserNotFoundException extends BusinessException {
    private static final String DEFAULT_ERROR_CODE = "USER_NOT_FOUND";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;

    public UserNotFoundException(String userId) {
        super("用户不存在，ID: " + userId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }

    public UserNotFoundException(String userId, Throwable cause) {
        super("用户不存在，ID: " + userId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}
