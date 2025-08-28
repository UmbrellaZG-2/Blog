package com.website.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
    "com.website.backend.article.repository",
    "com.website.backend.comment.repository",
    "com.website.backend.file.repository",
    "com.website.backend.system.repository",
    "com.website.backend.user.repository"
})
public class RepositoryConfig {
}