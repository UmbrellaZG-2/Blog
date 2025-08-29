package com.website.backend.comment.exception;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 评论不存在异常
 * 当请求的评论不存在时抛出
 */
public class CommentNotFoundException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "COMMENT_NOT_FOUND";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;

    public CommentNotFoundException(String commentId) {
        super("评论不存在，ID: " + commentId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }

    public CommentNotFoundException(String commentId, Throwable cause) {
        super("评论不存在，ID: " + commentId, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}