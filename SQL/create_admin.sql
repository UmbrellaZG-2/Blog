-- 用户名: umbrellazg
-- 密码: UmbrellaZG@1121 (已使用BCrypt加密)

USE `umbrellazg`;

-- 创建管理员用户
INSERT INTO `users` (`username`, `password`)
VALUES (
    'umbrellazg',
    '$2a$10$0MEayd0VqxhkfdnjQ6dtZe8kLkZbO2Wo0vralGUISOTy9TZDZMZBi'
);

-- 为管理员用户分配管理员角色
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT
    u.`id`,
    r.`id`
FROM
    `users` u,
    `roles` r
WHERE
    u.`username` = 'umbrellazg'
    AND r.`name` = 'ROLE_ADMIN';