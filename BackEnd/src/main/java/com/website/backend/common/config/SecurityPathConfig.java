package com.website.backend.common.config;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * 统一的安全路径配置类，管理所有公开路径
 * 避免在多个地方维护相同的路径列表
 */
@Component
public class SecurityPathConfig {
    
    // 公开路径列表，无需认证即可访问
    public static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/articles",
            "/articles/",
            "/articles/**",
            "/auth/get",
            "/auth/login",
            "/auth/admin/login",
            "/auth/admin/register",
            "/auth/guest/login",
            "/auth/register/send-code",
            "/auth/register/verify",
            "/attachments/**",
            "/images/**",
            "/home",
            "/home/**",
            "/tags",
            "/tags/",
            "/tags/**",
            "/tags/get",
            "/articles/categories/get"
    );
    
    // 需要管理员权限的路径
    public static final List<String> ADMIN_PATHS = Arrays.asList(
            "/articles/create",
            "/articles/update/**",
            "/articles/delete/**",
            "/articles/draft/**",
            "/images/article/*/cover/update",
            "/images/article/*/cover/delete",
            "/attachments/upload",
            "/attachments/delete/**",
            "/comments/delete/**",
            "/comments/edit/**",
            "/tags/create",
            "/tags/delete/**"
    );
    
    // 敏感路径，需要特殊处理
    public static final List<String> SENSITIVE_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/admin/login",
            "/auth/register/verify"
    );
}