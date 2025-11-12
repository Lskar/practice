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

                               INDEX idx_user_id (user_id),
                               INDEX idx_ai_model (ai_model),
                               INDEX idx_car_type (car_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询记录表';