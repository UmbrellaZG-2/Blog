package com.website.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * 仓库配置类，明确指定JPA和Redis仓库的扫描路径，避免冲突
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.website.backend.repository.jpa")
@EnableRedisRepositories(basePackages = "com.website.backend.repository.redis")
public class RepositoryConfig {
    // 配置类内容
}