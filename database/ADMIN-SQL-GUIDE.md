# 管理员功能SQL实现指南

## 📋 目录

- [概述](#概述)
- [1. 数据概览统计](#1-数据概览统计)
- [2. 用户管理](#2-用户管理)
- [3. 咨询记录管理](#3-咨询记录管理)
- [4. 数据统计分析](#4-数据统计分析)
- [5. 复合查询示例](#5-复合查询示例)


---



## 概述

本文档说明如何用**仅2张表**（`users` 和 `consultations`）实现API文档中所有管理员功能。

### 核心表结构

```
users:
├── id (主键)
├── username
├── password
├── role (ENUM: 'user', 'admin')
├── status (ENUM: 'active', 'disabled')
├── name
├── phone
├── email
└── points

consultations:
├── id (主键)
├── user_id (外键 → users.id)
├── title
├── budget
├── car_type
├── use_case (JSON)
├── fuel_type
├── brands (JSON)
├── description
├── ai_model (ENUM: 'qwen', 'zhipu')
├── result (JSON)
├── rating
├── created_at
└── updated_at
```

---

## 1. 数据概览统计

### 1.1 获取仪表盘核心数据

**API接口**：`GET /api/admin/dashboard`

**SQL实现**：

```sql
-- 方案A：多次查询（推荐，清晰易懂）
-- 总用户数（不含管理员）
SELECT COUNT(*) as totalUsers 
FROM users 
WHERE role = 'user';

-- 总咨询数
SELECT COUNT(*) as totalConsultations 
FROM consultations;

-- 今日咨询数
SELECT COUNT(*) as todayConsultations 
FROM consultations 
WHERE DATE(created_at) = CURDATE();

-- 活跃用户数
SELECT COUNT(*) as activeUsers 
FROM users 
WHERE role = 'user' AND status = 'active';
```

```sql
-- 方案B：单次查询（高性能）
SELECT 
  (SELECT COUNT(*) FROM users WHERE role = 'user') as totalUsers,
  (SELECT COUNT(*) FROM consultations) as totalConsultations,
  (SELECT COUNT(*) FROM consultations WHERE DATE(created_at) = CURDATE()) as todayConsultations,
  (SELECT COUNT(*) FROM users WHERE role = 'user' AND status = 'active') as activeUsers;
```

**返回示例**：
```json
{
  "totalUsers": 1286,
  "totalConsultations": 3542,
  "todayConsultations": 127,
  "activeUsers": 856
}
```

---

## 2. 用户管理

### 2.1 获取用户列表（带咨询统计）

**API接口**：`GET /api/admin/users`

**SQL实现**：

```sql
-- 基础查询（包含每个用户的咨询数）
SELECT 
    u.id,
    u.username,
    u.name,
    u.phone,
    u.email,
    u.points,
    u.status,
    (SELECT COUNT(*) FROM consultations WHERE user_id = u.id) as consultCount
FROM users u
WHERE u.role = 'user'
ORDER BY u.id DESC
LIMIT 20 OFFSET 0;
```

**带搜索和筛选**：

```sql
-- 搜索：用户名/姓名/手机号
-- 筛选：状态
SELECT 
    u.id,
    u.username,
    u.name,
    u.phone,
    u.email,
    u.points,
    u.status,
    (SELECT COUNT(*) FROM consultations WHERE user_id = u.id) as consultCount
FROM users u
WHERE u.role = 'user'
  -- 状态筛选（NULL表示不筛选）
  AND (? IS NULL OR u.status = ?)
  -- 关键词搜索（NULL表示不搜索）
  AND (? IS NULL OR 
       u.username LIKE CONCAT('%', ?, '%') OR 
       u.name LIKE CONCAT('%', ?, '%') OR 
       u.phone LIKE CONCAT('%', ?, '%'))
ORDER BY u.id DESC
LIMIT ? OFFSET ?;
```

**性能优化版（LEFT JOIN）**：

```sql
-- 使用LEFT JOIN，一次查询获取咨询数
SELECT 
    u.id,
    u.username,
    u.name,
    u.phone,
    u.email,
    u.points,
    u.status,
    COUNT(c.id) as consultCount
FROM users u
LEFT JOIN consultations c ON u.id = c.user_id
WHERE u.role = 'user'
GROUP BY u.id, u.username, u.name, u.phone, u.email, u.points, u.status
ORDER BY u.id DESC
LIMIT 20 OFFSET 0;
```

**返回示例**：
```json
{
  "id": 123,
  "username": "user001",
  "name": "张三",
  "phone": "13800138000",
  "email": "user@example.com",
  "points": 150,
  "consultCount": 8,
  "status": "active"
}
```

---

### 2.2 更新用户状态（启用/禁用）

**API接口**：`PATCH /api/admin/users/:id/status`

**SQL实现**：

```sql
-- 禁用用户
UPDATE users 
SET status = 'disabled' 
WHERE id = ? AND role = 'user';

-- 启用用户
UPDATE users 
SET status = 'active' 
WHERE id = ? AND role = 'user';
```

**安全检查**：
```sql
-- 确保不能修改管理员状态
UPDATE users 
SET status = ? 
WHERE id = ? AND role = 'user';  -- 限定只能改普通用户

-- 验证受影响行数
-- 如果 AFFECTED_ROWS = 0，说明用户不存在或是管理员
```

---

### 2.3 获取用户详细信息

**SQL实现**：

```sql
-- 用户基本信息 + 咨询统计
SELECT 
    u.*,
    COUNT(c.id) as totalConsultations,
    COALESCE(SUM(CASE WHEN c.rating IS NOT NULL THEN 1 ELSE 0 END), 0) as ratedConsultations,
    COALESCE(AVG(c.rating), 0) as avgRating
FROM users u
LEFT JOIN consultations c ON u.id = c.user_id
WHERE u.id = ? AND u.role = 'user'
GROUP BY u.id;
```

---

## 3. 咨询记录管理

### 3.1 获取所有咨询记录

**API接口**：`GET /api/admin/consultations`

**SQL实现**：

```sql
-- 基础查询（带用户信息）
SELECT 
    c.id,
    c.user_id,
    u.username,
    u.name,
    c.title,
    c.budget,
    c.car_type,
    c.fuel_type,
    c.ai_model,
    c.rating,
    c.created_at
FROM consultations c
LEFT JOIN users u ON c.user_id = u.id
ORDER BY c.created_at DESC
LIMIT 20 OFFSET 0;
```

**带筛选条件**：

```sql
-- 筛选：车型、AI模型、日期范围
SELECT 
    c.*,
    u.username,
    u.name
FROM consultations c
LEFT JOIN users u ON c.user_id = u.id
WHERE 1=1
  -- 车型筛选
  AND (? IS NULL OR c.car_type = ?)
  -- AI模型筛选
  AND (? IS NULL OR c.ai_model = ?)
  -- 开始日期筛选
  AND (? IS NULL OR DATE(c.created_at) >= ?)
  -- 结束日期筛选
  AND (? IS NULL OR DATE(c.created_at) <= ?)
ORDER BY c.created_at DESC
LIMIT ? OFFSET ?;
```

**按用户查询某用户的所有咨询**：

```sql
SELECT c.*
FROM consultations c
WHERE c.user_id = ?
ORDER BY c.created_at DESC;
```

---

### 3.2 获取咨询详情

**SQL实现**：

```sql
-- 完整咨询信息（包括JSON字段）
SELECT 
    c.*,
    u.username,
    u.name,
    u.phone,
    u.email
FROM consultations c
LEFT JOIN users u ON c.user_id = u.id
WHERE c.id = ?;
```

---

## 4. 数据统计分析

### 4.1 车型分布统计

**API接口**：`GET /api/admin/statistics/car-types`

**SQL实现**：

```sql
-- 车型分布及占比
SELECT 
    car_type as carType,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY car_type
ORDER BY count DESC;
```

**返回示例**：
```json
[
  { "carType": "SUV", "count": 1250, "percentage": 42 },
  { "carType": "轿车", "count": 980, "percentage": 33 },
  { "carType": "MPV", "count": 456, "percentage": 15 }
]
```

---

### 4.2 预算分布统计

**API接口**：`GET /api/admin/statistics/budgets`

**SQL实现**：

```sql
-- 预算区间分布及占比
SELECT 
    budget as `range`,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY budget
ORDER BY count DESC;
```

**按指定顺序排列**：

```sql
-- 自定义排序（从低到高）
SELECT 
    budget as `range`,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY budget
ORDER BY 
    CASE budget
        WHEN '10万以下' THEN 1
        WHEN '10-20万' THEN 2
        WHEN '20-30万' THEN 3
        WHEN '30-50万' THEN 4
        WHEN '50万以上' THEN 5
    END;
```

---

### 4.3 AI模型使用统计

**SQL实现**：

```sql
-- AI模型使用情况及平均评分
SELECT 
    ai_model,
    COUNT(*) as totalUsed,
    COUNT(rating) as ratedCount,
    COALESCE(AVG(rating), 0) as avgRating,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY ai_model
ORDER BY totalUsed DESC;
```

**返回示例**：
```json
[
  {
    "ai_model": "qwen",
    "totalUsed": 2100,
    "ratedCount": 1850,
    "avgRating": 4.3,
    "percentage": 59
  },
  {
    "ai_model": "zhipu",
    "totalUsed": 1442,
    "ratedCount": 1200,
    "avgRating": 4.1,
    "percentage": 41
  }
]
```

---

### 4.4 燃料类型统计

**SQL实现**：

```sql
-- 燃料类型偏好分布
SELECT 
    fuel_type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY fuel_type
ORDER BY count DESC;
```

---

### 4.5 热门话题分析

**API接口**：`GET /api/admin/statistics/topics`

**SQL实现**：

```sql
-- 方案A：按标题统计（简单）
SELECT 
    title as topic,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY title
ORDER BY count DESC
LIMIT 10;
```

```sql
-- 方案B：按关键词提取（推荐）
-- 结合description字段做关键词分析
SELECT 
    CASE
        WHEN title LIKE '%SUV%' OR description LIKE '%SUV%' THEN 'SUV推荐'
        WHEN title LIKE '%新能源%' OR description LIKE '%电动%' THEN '新能源车型'
        WHEN title LIKE '%家用%' OR description LIKE '%家庭%' THEN '家用车推荐'
        WHEN title LIKE '%轿车%' THEN '轿车推荐'
        ELSE '其他'
    END as topic,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY topic
ORDER BY count DESC
LIMIT 10;
```

---

### 4.6 时间趋势分析

**按日统计**：

```sql
-- 最近7天每天的咨询量
SELECT 
    DATE(created_at) as date,
    COUNT(*) as count
FROM consultations
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(created_at)
ORDER BY date ASC;
```

**按月统计**：

```sql
-- 最近12个月每月的咨询量
SELECT 
    DATE_FORMAT(created_at, '%Y-%m') as month,
    COUNT(*) as count
FROM consultations
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
GROUP BY DATE_FORMAT(created_at, '%Y-%m')
ORDER BY month ASC;
```

**按小时统计（热门时段）**：

```sql
-- 一天中哪个时段咨询最多
SELECT 
    HOUR(created_at) as hour,
    COUNT(*) as count
FROM consultations
GROUP BY HOUR(created_at)
ORDER BY hour ASC;
```

---

### 4.7 用户活跃度分析

**咨询次数排行榜**：

```sql
-- Top 10 活跃用户
SELECT 
    u.id,
    u.username,
    u.name,
    COUNT(c.id) as consultCount,
    u.points
FROM users u
LEFT JOIN consultations c ON u.id = c.user_id
WHERE u.role = 'user'
GROUP BY u.id, u.username, u.name, u.points
ORDER BY consultCount DESC
LIMIT 10;
```

**评分习惯分析**：

```sql
-- 用户评分统计
SELECT 
    rating,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations WHERE rating IS NOT NULL), 2) as percentage
FROM consultations
WHERE rating IS NOT NULL
GROUP BY rating
ORDER BY rating DESC;
```

---

## 5. 复合查询示例

### 5.1 综合数据报表

**月度运营报告**：

```sql
-- 本月关键指标
SELECT 
    -- 新增用户
    (SELECT COUNT(*) FROM users 
     WHERE role = 'user' 
     AND MONTH(created_at) = MONTH(CURDATE()) 
     AND YEAR(created_at) = YEAR(CURDATE())) as newUsers,
    
    -- 新增咨询
    (SELECT COUNT(*) FROM consultations 
     WHERE MONTH(created_at) = MONTH(CURDATE()) 
     AND YEAR(created_at) = YEAR(CURDATE())) as newConsultations,
    
    -- 活跃用户（本月有咨询的用户）
    (SELECT COUNT(DISTINCT user_id) FROM consultations 
     WHERE MONTH(created_at) = MONTH(CURDATE()) 
     AND YEAR(created_at) = YEAR(CURDATE())) as activeUsers,
    
    -- 平均评分
    (SELECT AVG(rating) FROM consultations 
     WHERE rating IS NOT NULL 
     AND MONTH(created_at) = MONTH(CURDATE()) 
     AND YEAR(created_at) = YEAR(CURDATE())) as avgRating;
```

---

### 5.2 用户画像分析

**单个用户的完整画像**：

```sql
-- 用户基本信息 + 咨询行为分析
SELECT 
    u.id,
    u.username,
    u.name,
    u.points,
    u.status,
    COUNT(c.id) as totalConsults,
    COUNT(CASE WHEN c.rating IS NOT NULL THEN 1 END) as ratedConsults,
    AVG(c.rating) as avgRating,
    -- 最常选择的车型
    (SELECT car_type FROM consultations 
     WHERE user_id = u.id 
     GROUP BY car_type 
     ORDER BY COUNT(*) DESC 
     LIMIT 1) as favoriteCarType,
    -- 最常选择的预算
    (SELECT budget FROM consultations 
     WHERE user_id = u.id 
     GROUP BY budget 
     ORDER BY COUNT(*) DESC 
     LIMIT 1) as favoriteBudget,
    -- 首次咨询时间
    MIN(c.created_at) as firstConsult,
    -- 最后咨询时间
    MAX(c.created_at) as lastConsult
FROM users u
LEFT JOIN consultations c ON u.id = c.user_id
WHERE u.id = ?
GROUP BY u.id, u.username, u.name, u.points, u.status;
```

---

### 5.3 品牌偏好分析（JSON查询）

**提取brands JSON数组中的品牌统计**：

```sql
-- MySQL 8.0+ JSON函数
SELECT 
    brand,
    COUNT(*) as count
FROM (
    SELECT JSON_UNQUOTE(JSON_EXTRACT(brands, CONCAT('$[', idx, ']'))) as brand
    FROM consultations
    CROSS JOIN (
        SELECT 0 as idx UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 
        UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
    ) indexes
    WHERE JSON_LENGTH(brands) > idx
) brand_list
WHERE brand IS NOT NULL
GROUP BY brand
ORDER BY count DESC;
```

**简化版（如果每个咨询只统计一次，不展开数组）**：

```sql
-- 包含某品牌的咨询数量
SELECT 
    '大众' as brand,
    COUNT(*) as count
FROM consultations
WHERE JSON_CONTAINS(brands, '"大众"')
UNION ALL
SELECT 
    '丰田' as brand,
    COUNT(*) as count
FROM consultations
WHERE JSON_CONTAINS(brands, '"丰田"')
UNION ALL
SELECT 
    '比亚迪' as brand,
    COUNT(*) as count
FROM consultations
WHERE JSON_CONTAINS(brands, '"比亚迪"')
-- ... 其他品牌
ORDER BY count DESC;
```

---

### 5.4 数据质量监控

**未评分咨询统计**：

```sql
-- 未评分的咨询记录
SELECT 
    COUNT(*) as unratedCount,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 2) as unratedPercentage
FROM consultations
WHERE rating IS NULL;
```

**缺少描述的咨询**：

```sql
-- 描述过短的咨询（可能质量不高）
SELECT 
    COUNT(*) as shortDescCount
FROM consultations
WHERE CHAR_LENGTH(description) < 20;
```

---

## 📊 性能优化建议

### 索引策略

```sql
-- users表索引
CREATE INDEX idx_role_status ON users(role, status);

-- consultations表索引
CREATE INDEX idx_user_id ON consultations(user_id);
CREATE INDEX idx_car_type ON consultations(car_type);
CREATE INDEX idx_ai_model ON consultations(ai_model);
CREATE INDEX idx_created_at ON consultations(created_at);
CREATE INDEX idx_rating ON consultations(rating);

-- 复合索引（常用组合查询）
CREATE INDEX idx_created_car ON consultations(created_at, car_type);
```

### 查询优化技巧

1. **避免子查询过多**：用JOIN代替多个子查询
2. **分页必须有LIMIT**：防止全表扫描
3. **统计用COUNT(*)**：比COUNT(column)更快
4. **日期查询用索引**：WHERE DATE(created_at) = ... 改为 WHERE created_at >= ... AND created_at < ...
5. **JSON查询慎用**：JSON函数性能较低，高频查询考虑反范式

---

## 🔧 常见问题

### Q1: 如何统计某个时间段的数据？

```sql
-- 本周
WHERE YEARWEEK(created_at) = YEARWEEK(CURDATE())

-- 本月
WHERE MONTH(created_at) = MONTH(CURDATE()) 
  AND YEAR(created_at) = YEAR(CURDATE())

-- 最近7天
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)

-- 指定日期范围
WHERE created_at BETWEEN '2024-01-01' AND '2024-12-31'
```

### Q2: 如何处理NULL值？

```sql
-- 使用COALESCE设置默认值
SELECT COALESCE(AVG(rating), 0) as avgRating FROM consultations;

-- 使用IFNULL
SELECT IFNULL(COUNT(rating), 0) as ratedCount FROM consultations;

-- 排除NULL
SELECT AVG(rating) FROM consultations WHERE rating IS NOT NULL;
```

### Q3: 如何做分页？

```sql
-- 第1页（每页20条）
LIMIT 20 OFFSET 0

-- 第2页
LIMIT 20 OFFSET 20

-- 第N页
LIMIT 20 OFFSET (N-1)*20
```

---

**版本**：v1.0  
**最后更新**：2024-11-11  
**对应数据库**：精简版（2张核心表）
