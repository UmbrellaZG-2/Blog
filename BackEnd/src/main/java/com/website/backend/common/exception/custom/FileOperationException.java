package com.website.backend.common.exception.custom;

import com.website.backend.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 文件操作异常
 * 当文件操作（上传、下载、删除等）失败时抛出
 */
public class FileOperationException extends BusinessException {
    
    private static final String DEFAULT_ERROR_CODE = "FILE_OPERATION_FAILED";
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    
    /**
     * 构造文件操作异常
     * @param message 错误消息
     */
    public FileOperationException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }
    
    /**
     * 构造文件操作异常
     * @param message 错误消息
     * @param cause 异常原因
     */
    public FileOperationException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, cause);
    }
}