-- 插入测试数据

-- 插入角色数据
INSERT INTO roles (name) VALUES 
('ROLE_ADMIN'),
('ROLE_VISITOR');

-- 插入管理员用户数据
INSERT INTO users (username, password, email, role_id) 
SELECT 'admin', '$2a$10$wHjp1ZW8vH5b0QxSfV4TzO5a4v7.3H5b0QxSfV4TzO5a4v7.3H5b0', 'admin@example.com', id 
FROM roles WHERE name = 'ROLE_ADMIN';

-- 插入系统配置数据
INSERT INTO system_configs (config_key, config_value) VALUES 
('site_title', '测试博客'),
('site_description', '这是一个测试博客网站'),
('max_attachment_size', '10485760'),
('max_picture_size', '5242880');

-- 插入测试文章数据
INSERT INTO articles (article_id, title, content, category, add_attach, add_picture, create_time, update_time) VALUES 
('550e8400-e29b-41d4-a716-446655440000', '测试文章标题1', '这是第一篇测试文章的内容。', '技术', FALSE, FALSE, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440001', '测试文章标题2', '这是第二篇测试文章的内容。', '生活', FALSE, FALSE, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', '测试文章标题3', '这是第三篇测试文章的内容。', '技术', FALSE, FALSE, NOW(), NOW());

-- 插入测试标签数据
INSERT INTO tags (name, create_time) VALUES 
('Java', NOW()),
('Spring Boot', NOW()),
('测试', NOW());

-- 插入文章标签关联数据
INSERT INTO article_tags (article_id, tag_id) 
SELECT a.id, t.id 
FROM articles a, tags t 
WHERE a.title = '测试文章标题1' AND t.name = 'Java';

INSERT INTO article_tags (article_id, tag_id) 
SELECT a.id, t.id 
FROM articles a, tags t 
WHERE a.title = '测试文章标题1' AND t.name = 'Spring Boot';

INSERT INTO article_tags (article_id, tag_id) 
SELECT a.id, t.id 
FROM articles a, tags t 
WHERE a.title = '测试文章标题2' AND t.name = '测试';

-- 插入测试评论数据
INSERT INTO comments (article_id, nickname, content, create_time, ip_address) 
SELECT a.id, '测试用户', '这是一条测试评论。', NOW(), '127.0.0.1'
FROM articles a 
WHERE a.title = '测试文章标题1';

-- 插入测试附件数据
INSERT INTO attachments (file_name, file_path, file_type, file_size, upload_time, article_id) 
SELECT 'test.txt', '/uploads/test.txt', 'text/plain', 1024, NOW(), a.id
FROM articles a 
WHERE a.title = '测试文章标题1';

-- 插入测试文章图片数据
INSERT INTO article_pictures (file_name, file_path, file_type, file_size, upload_time, article_id) 
SELECT 'cover.jpg', '/uploads/cover.jpg', 'image/jpeg', 2048, NOW(), a.id
FROM articles a 
WHERE a.title = '测试文章标题2';