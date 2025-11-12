CREATE TABLE users (
                       user_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
                       username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一',
                       email VARCHAR(100) UNIQUE COMMENT '邮箱，唯一',
                       phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号，唯一',
                       password_hash VARCHAR(255) NOT NULL COMMENT '加密后的密码',
                       real_name VARCHAR(50) COMMENT '真实姓名',
                        role INT COMMENT '角色',
                       PRIMARY KEY (user_id),
                       INDEX idx_phone (phone),
                       INDEX idx_email (email),
                       INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';