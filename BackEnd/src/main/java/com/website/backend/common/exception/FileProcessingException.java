package com.website.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 文件处理异常类
 * 用于处理文件上传、下载、删除等操作中的异常情况
 */
public class FileProcessingException extends BusinessException {
    
    /**
     * 构造文件处理异常
     * @param message 异常消息
     */
    public FileProcessingException(String message) {
        super(message, "FILE_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 构造文件处理异常
     * @param message 异常消息
     * @param cause 异常原因
     */
    public FileProcessingException(String message, Throwable cause) {
        super(message, "FILE_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}