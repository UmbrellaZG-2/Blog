package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 文件上传异常
 * 当文件上传失败时抛出
 */
public class FileUploadException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "FILE_UPLOAD_FAILED";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;
    
    /**
     * 构造文件上传异常
     * @param message 错误消息
     */
    public FileUploadException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文件上传异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public FileUploadException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}