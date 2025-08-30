package com.website.backend.common.constant;

/**
 * 应用程序常量�? * 包含应用程序中使用的各种常量
 */
public class ApplicationConstants {
    
    // JWT相关常量
    public static final String JWT_SECRET_DEFAULT = "======================UmbrellaZG=Spring===========================";
    public static final long JWT_EXPIRATION_DEFAULT = 86400000; // 24小时
    public static final long JWT_REFRESH_EXPIRATION_DEFAULT = 604800000; // 7�?    
    // 文件相关常量
    public static final String UPLOAD_DIR = "uploads/";
    public static final String ARTICLE_IMAGE_DIR = UPLOAD_DIR + "article_images/";
    public static final String COVER_IMAGE_DIR = UPLOAD_DIR + "cover_images/";
    public static final String ATTACHMENT_DIR = UPLOAD_DIR + "attachments/";
    
    // 用户角色常量
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // 分页相关常量
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    // 文件类型常量
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_GIF = "image/gif";
    
    // HTTP请求相关常量
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    private ApplicationConstants() {
        // 私有构造函数防止实例化
    }
}