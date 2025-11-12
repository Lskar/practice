-- ============================================
-- 智能购车咨询系统 - 精简数据库设计
-- 仅包含前端代码实际使用的功能
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS car_consultation_system
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE car_consultation_system;

-- ============================================
-- 1. 用户表 (users)
-- 用途：存储用户和管理员信息
-- ============================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    
    -- 账号信息
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名(3-20字符)',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储，至少6字符）',
    role ENUM('user', 'admin') DEFAULT 'user' COMMENT '角色：user-普通用户, admin-管理员',
    status ENUM('active', 'disabled') DEFAULT 'active' COMMENT '状态：active-正常, disabled-禁用',
    
    -- 基本信息
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号(11位)',
    email VARCHAR(100) COMMENT '邮箱地址',
    
    -- 积分系统
    points INT DEFAULT 0 COMMENT '用户积分',
    
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表(包含普通用户和管理员)';

-- ============================================
-- 2. 咨询记录表 (consultations)
-- 用途：存储用户的购车咨询记录和AI分析结果
-- ============================================
CREATE TABLE consultations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '咨询ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 咨询基本信息
    title VARCHAR(200) NOT NULL COMMENT '咨询标题',
    budget VARCHAR(50) NOT NULL COMMENT '购车预算：10万以下|10-20万|20-30万|30-50万|50万以上',
    car_type VARCHAR(50) NOT NULL COMMENT '偏好车型：SUV|轿车|MPV|跑车|越野车',
    use_case JSON NOT NULL COMMENT '使用场景（数组）：通勤|家庭|商务|越野|其他',
    fuel_type VARCHAR(50) NOT NULL COMMENT '燃料类型：燃油|电动|混动|不限',
    brands JSON COMMENT '品牌偏好（数组）：大众|丰田|本田|比亚迪|特斯拉|宝马|奔驰|奥迪等',
    description TEXT NOT NULL COMMENT '详细需求描述(至少10字符)',
    
    -- AI咨询信息
    ai_model ENUM('qwen', 'zhipu') NOT NULL COMMENT 'AI模型：qwen-通义千问, zhipu-智谱AI',
    result JSON COMMENT 'AI咨询结果（JSON格式）：{recommendation, analysis, budgetAdvice}',
    
    -- 用户反馈
    rating TINYINT COMMENT '用户评分：1-5星',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_ai_model (ai_model),
    INDEX idx_car_type (car_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询记录表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员账号（密码：admin123，实际应使用bcrypt加密）
INSERT INTO users (username, password, name, role, phone, email, points) VALUES
('admin', '$2a$10$examplehashedpassword', '系统管理员', 'admin', '13800000000', 'admin@example.com', 0);

-- 插入示例普通用户（密码：123456）
INSERT INTO users (username, password, name, phone, email, role, points, status) VALUES
('user001', '$2a$10$examplehashedpassword', '张三', '13800138000', 'zhangsan@example.com', 'user', 150, 'active'),
('user002', '$2a$10$examplehashedpassword', '李四', '13800138001', 'lisi@example.com', 'user', 200, 'active'),
('user003', '$2a$10$examplehashedpassword', '王五', '13800138002', 'wangwu@example.com', 'user', 80, 'active');

-- 插入示例咨询记录
INSERT INTO consultations (user_id, title, budget, car_type, use_case, fuel_type, brands, description, ai_model, result, rating) VALUES
(2, '想买一辆家用SUV', '10-20万', 'SUV', '["通勤", "家庭"]', '混动', '["大众", "丰田", "比亚迪"]', '家里有两个孩子，需要空间大的车，平时上下班也要用', 'qwen', 
'{"recommendation": "推荐比亚迪宋PLUS DM-i、丰田威兰达双擎", "analysis": "基于您的家庭需求和预算，推荐混动SUV性价比高", "budgetAdvice": "建议预算控制在15-18万"}', 5),
(3, '20万左右的电动轿车推荐', '20-30万', '轿车', '["商务", "通勤"]', '电动', '["特斯拉", "比亚迪"]', '公司有充电桩，想买纯电动车', 'zhipu', 
'{"recommendation": "推荐比亚迪海豹、特斯拉Model 3", "analysis": "充电条件良好，适合购买纯电车型", "budgetAdvice": "建议预算22-26万"}', 4);

-- ============================================
-- 说明
-- ============================================

/*
数据库设计说明：

1. 表结构精简原则
   - 仅包含前端代码实际使用的字段
   - 合并用户和管理员到同一张表，通过role字段区分
   - 不包含未使用的扩展功能表

2. 枚举值说明（以前端代码为准）
   
   users.role:
   - 'user': 普通用户
   - 'admin': 管理员
   
   users.status:
   - 'active': 正常
   - 'disabled': 禁用
   
   consultations.budget:
   - '10万以下'
   - '10-20万'
   - '20-30万'
   - '30-50万'
   - '50万以上'
   
   consultations.car_type:
   - 'SUV'
   - '轿车'
   - 'MPV'
   - '跑车'
   - '越野车'
   
   consultations.fuel_type:
   - '燃油'
   - '电动'
   - '混动'
   - '不限'
   
   consultations.use_case (JSON数组):
   - '通勤'
   - '家庭'
   - '商务'
   - '越野'
   - '其他'
   
   consultations.brands (JSON数组):
   - '大众'
   - '丰田'
   - '本田'
   - '比亚迪'
   - '特斯拉'
   - '宝马'
   - '奔驰'
   - '奥迪'
   
   consultations.ai_model:
   - 'qwen': 通义千问
   - 'zhipu': 智谱AI
   
   consultations.rating:
   - 1-5: 星级评分

3. JSON字段格式
   
   use_case示例:
   ["通勤", "家庭"]
   
   brands示例:
   ["大众", "丰田", "比亚迪"]
   
   result示例:
   {
     "recommendation": "推荐车型信息",
     "analysis": "详细分析",
     "budgetAdvice": "预算建议"
   }

4. 积分规则（代码硬编码）
   - 完成咨询：+10 分
   - 提供评分：+5 分
   - 推荐好友：+20 分

5. 外键约束
   - consultations.user_id → users.id (ON DELETE CASCADE)
   - 删除用户时自动删除其所有咨询记录
*/
