-- 创建数据库表结构初始化脚本

-- 用户表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL
    );

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
    );

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        article_id CHAR(36) NOT NULL UNIQUE,
    user_id BIGINT,
    title VARCHAR(100),
    content TEXT,
    category VARCHAR(255),
    add_attach BOOLEAN DEFAULT FALSE,
    add_picture BOOLEAN DEFAULT FALSE,
    is_draft BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    attachment_path VARCHAR(255),
    picture_path VARCHAR(255)
    );

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(50) NOT NULL UNIQUE,
    create_time TIMESTAMP NOT NULL
    );

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS article_tags (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            article_id BIGINT NOT NULL,
                                            tag_id BIGINT NOT NULL,
                                            FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
    );

-- 文章点赞表
CREATE TABLE IF NOT EXISTS article_likes (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             article_id BIGINT NOT NULL,
    user_id BIGINT,
    create_time TIMESTAMP NOT NULL,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        article_id BIGINT NOT NULL,
                                        parent_id BIGINT,
                                        nickname VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    ip_address VARCHAR(50),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
    );

-- 附件表
CREATE TABLE IF NOT EXISTS attachments (
                                           attachment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    upload_time TIMESTAMP,
    article_id BIGINT NOT NULL,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
    );

-- 文章图片表
CREATE TABLE IF NOT EXISTS article_pictures (
                                                picture_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    upload_time TIMESTAMP,
    article_id BIGINT NOT NULL,
    is_cover BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
    );

-- 访客用户表
CREATE TABLE IF NOT EXISTS guest_users (
                                           id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255),
    expire_time TIMESTAMP,
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );

-- IP限流表
CREATE TABLE IF NOT EXISTS ip_rate_limits (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              ip VARCHAR(255),
    endpoint VARCHAR(255),
    expiry_time TIMESTAMP,
    ip_address VARCHAR(255),
    request_count INT,
    window_start_time TIMESTAMP,
    window_end_time TIMESTAMP,
    is_blocked BOOLEAN,
    block_end_time TIMESTAMP,
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );

-- 系统配置表
CREATE TABLE IF NOT EXISTS tb_cfg_system (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT,
    config_desc TEXT,
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );

-- 验证码表
CREATE TABLE IF NOT EXISTS verification_codes (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  email VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    UNIQUE KEY unique_email_code (email, code)
    );

-- 插入基础角色数据
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_GUEST')
    ON DUPLICATE KEY UPDATE name=name;