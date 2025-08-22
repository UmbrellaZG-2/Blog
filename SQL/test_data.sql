-- ========================================
-- UmbrellaZG 博客系统测试数据插入脚本
-- ========================================

USE `umbrellazg`;

-- 插入测试文章数据
INSERT INTO `articles` (`article_id`, `title`, `content`, `category`, `add_attach`, `add_picture`, `create_time`, `update_time`) VALUES 
('550e8400-e29b-41d4-a716-446655440000', '测试文章标题1', '这是第一篇测试文章的内容。', '技术', FALSE, FALSE, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440001', '测试文章标题2', '这是第二篇测试文章的内容。', '生活', FALSE, FALSE, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', '测试文章标题3', '这是第三篇测试文章的内容。', '技术', FALSE, FALSE, NOW(), NOW());

-- 插入测试标签数据
INSERT INTO `tags` (`name`, `create_time`) VALUES 
('Java', NOW()),
('Spring Boot', NOW()),
('测试', NOW());

-- 插入文章标签关联数据
INSERT INTO `article_tags` (`article_id`, `tag_id`) 
SELECT a.id, t.id 
FROM `articles` a, `tags` t 
WHERE a.title = '测试文章标题1' AND t.name = 'Java';

INSERT INTO `article_tags` (`article_id`, `tag_id`) 
SELECT a.id, t.id 
FROM `articles` a, `tags` t 
WHERE a.title = '测试文章标题1' AND t.name = 'Spring Boot';

INSERT INTO `article_tags` (`article_id`, `tag_id`) 
SELECT a.id, t.id 
FROM `articles` a, `tags` t 
WHERE a.title = '测试文章标题2' AND t.name = '测试';

-- 插入测试评论数据
INSERT INTO `comments` (`article_id`, `nickname`, `content`, `create_time`, `ip_address`) 
SELECT a.id, '测试用户', '这是一条测试评论。', NOW(), '127.0.0.1'
FROM `articles` a 
WHERE a.title = '测试文章标题1';

-- 插入测试附件数据
INSERT INTO `attachments` (`file_name`, `file_path`, `file_type`, `file_size`, `upload_time`, `article_id`) 
SELECT 'test.txt', '/uploads/test.txt', 'text/plain', 1024, NOW(), a.id
FROM `articles` a 
WHERE a.title = '测试文章标题1';

-- 插入测试文章图片数据
INSERT INTO `article_pictures` (`file_name`, `file_path`, `file_type`, `file_size`, `upload_time`, `article_id`) 
SELECT 'cover.jpg', '/uploads/cover.jpg', 'image/jpeg', 2048, NOW(), a.id
FROM `articles` a 
WHERE a.title = '测试文章标题2';