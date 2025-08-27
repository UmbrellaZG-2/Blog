package com.website.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.website.backend.repository.jpa")
@EnableRedisRepositories(basePackages = "com.website.backend.repository.redis")
public class RepositoryConfig {
}