# 智能购车咨询系统 - 数据库设计文档（精简版）

## 📋 目录

- [概述](#概述)
- [设计原则](#设计原则)
- [数据库结构](#数据库结构)
- [表结构详细说明](#表结构详细说明)
- [枚举值说明](#枚举值说明)
- [功能分类](#功能分类)
- [使用说明](#使用说明)

---

## 概述

本数据库设计严格基于前端代码的实际使用情况，仅包含必要的功能表，不包含任何未使用的扩展功能。

### 核心功能

- **用户管理**：用户注册、登录、信息管理
- **角色权限**：普通用户和管理员
- **咨询服务**：记录购车咨询和AI分析结果
- **积分系统**：简单的积分记录（存储在users表）

### 技术栈

- **数据库**：MySQL 8.0+
- **字符集**：utf8mb4
- **存储引擎**：InnoDB

---

## 设计原则

### ✅ 精简原则

1. **仅两张核心表**：users、consultations
2. **字段完全匹配前端**：所有字段都是前端实际使用的
3. **无扩展功能**：不包含未使用的表和字段
4. **枚举值以前端为准**：所有选项值严格按照前端定义

### ❌ 删除的表

以下表在前端代码中**未实际使用**，因此不包含：

- ~~notification_settings~~ - 通知设置（前端已移除该功能）
- ~~points_history~~ - 积分历史（前端未使用详细历史）
- ~~system_logs~~ - 系统日志（前端未使用）
- ~~cars~~ - 汽车信息（前端未使用车辆库）
- ~~system_config~~ - 系统配置（改为代码管理）

---

## 数据库结构

### 核心表（2张）

```
1. users          - 用户表（包含普通用户和管理员）
2. consultations  - 咨询记录表
```

### 关系图

```
users (1) ──────────── (N) consultations
  │
  ├─ role: user     → 普通用户功能
  └─ role: admin    → 管理员功能
```

---

## 表结构详细说明

### 1. users - 用户表

**用途**：存储所有用户信息（包括普通用户和管理员）

| 字段名 | 类型 | 说明 | 约束 | 前端对应 |
|-------|------|------|------|---------|
| **id** | BIGINT | 用户ID | PRIMARY KEY, AUTO_INCREMENT | userInfo.id |
| **username** | VARCHAR(50) | 用户名 | UNIQUE, NOT NULL | userInfo.username |
| **password** | VARCHAR(255) | 密码（加密） | NOT NULL | registerForm.password |
| **role** | ENUM | 角色 | DEFAULT 'user' | userInfo.role |
| **status** | ENUM | 状态 | DEFAULT 'active' | row.status |
| **name** | VARCHAR(100) | 姓名 | NOT NULL | userInfo.name |
| **phone** | VARCHAR(20) | 手机号 | NOT NULL | userInfo.phone |
| **email** | VARCHAR(100) | 邮箱 | | userInfo.email |
| **points** | INT | 积分 | DEFAULT 0 | userInfo.points |

**索引**：
- PRIMARY KEY: `id`
- UNIQUE: `username`
- INDEX: `role`, `status`

---

### 2. consultations - 咨询记录表

**用途**：存储用户的购车咨询记录和AI分析结果

| 字段名 | 类型 | 说明 | 约束 | 前端对应 |
|-------|------|------|------|---------|
| **id** | BIGINT | 咨询ID | PRIMARY KEY, AUTO_INCREMENT | consultation.id |
| **user_id** | BIGINT | 用户ID | FOREIGN KEY, NOT NULL | consultation.userId |
| **title** | VARCHAR(200) | 咨询标题 | NOT NULL | form.title |
| **budget** | VARCHAR(50) | 购车预算 | NOT NULL | form.budget |
| **car_type** | VARCHAR(50) | 偏好车型 | NOT NULL | form.carType |
| **use_case** | JSON | 使用场景 | NOT NULL | form.useCase |
| **fuel_type** | VARCHAR(50) | 燃料类型 | NOT NULL | form.fuelType |
| **brands** | JSON | 品牌偏好 | | form.brands |
| **description** | TEXT | 详细需求 | NOT NULL | form.description |
| **ai_model** | ENUM | AI模型 | NOT NULL | form.aiModel |
| **result** | JSON | AI结果 | | consultResult |
| **rating** | TINYINT | 评分(1-5) | | rating |
| **created_at** | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP | consultation.createdAt |
| **updated_at** | TIMESTAMP | 更新时间 | ON UPDATE CURRENT_TIMESTAMP | |

**索引**：
- PRIMARY KEY: `id`
- FOREIGN KEY: `user_id` → `users(id)` ON DELETE CASCADE
- INDEX: `user_id`, `ai_model`, `car_type`, `created_at`

---

## 枚举值说明

> 以下所有枚举值完全按照前端代码定义

### users.role - 用户角色

| 值 | 说明 | 前端代码位置 |
|----|------|-------------|
| `user` | 普通用户 | user.js: `userRole.value` |
| `admin` | 管理员 | user.js: `userRole.value` |

### users.status - 账号状态

| 值 | 说明 | 前端代码位置 |
|----|------|-------------|
| `active` | 正常 | AdminView.vue: `row.status === 'active'` |
| `disabled` | 禁用 | AdminView.vue: `row.status === 'disabled'` |

### budget - 购车预算

**位置**：`consultations.budget`

| 值 | 前端代码位置 |
|----|-------------|
| `10万以下` | ConsultationForm.vue |
| `10-20万` | ConsultationForm.vue |
| `20-30万` | ConsultationForm.vue |
| `30-50万` | ConsultationForm.vue |
| `50万以上` | ConsultationForm.vue |

### car_type - 车型

**位置**：`consultations.car_type`

| 值 | 前端代码位置 |
|----|-------------|
| `SUV` | ConsultationForm.vue |
| `轿车` | ConsultationForm.vue |
| `MPV` | ConsultationForm.vue |
| `跑车` | ConsultationForm.vue |
| `越野车` | ConsultationForm.vue |

### fuel_type - 燃料类型

**位置**：`consultations.fuel_type`

| 值 | 前端代码位置 |
|----|-------------|
| `燃油` | ConsultationForm.vue |
| `电动` | ConsultationForm.vue |
| `混动` | ConsultationForm.vue |
| `不限` | ConsultationForm.vue |

### use_case - 使用场景（JSON数组）

**位置**：`consultations.use_case`

| 值 | 前端代码位置 |
|----|-------------|
| `通勤` | ConsultationForm.vue |
| `家庭` | ConsultationForm.vue |
| `商务` | ConsultationForm.vue |
| `越野` | ConsultationForm.vue |
| `其他` | ConsultationForm.vue |

**JSON格式示例**：
```json
["通勤", "家庭"]
```

### brands - 品牌偏好（JSON数组）

**位置**：`consultations.brands`

| 值 | 前端代码位置 |
|----|-------------|
| `大众` | ConsultationForm.vue |
| `丰田` | ConsultationForm.vue |
| `本田` | ConsultationForm.vue |
| `比亚迪` | ConsultationForm.vue |
| `特斯拉` | ConsultationForm.vue |
| `宝马` | ConsultationForm.vue |
| `奔驰` | ConsultationForm.vue |
| `奥迪` | ConsultationForm.vue |

**JSON格式示例**：
```json
["大众", "丰田", "比亚迪"]
```

### consultations.ai_model - AI模型

| 值 | 说明 | 前端代码位置 |
|----|------|-------------|
| `qwen` | 通义千问 | ConsultationForm.vue: `form.aiModel` |
| `zhipu` | 智谱AI | ConsultationForm.vue: `form.aiModel` |

### consultations.rating - 评分

| 值 | 说明 | 前端代码位置 |
|----|------|-------------|
| `1-5` | 星级评分 | ConsultationForm.vue: `rating` |

### consultations.result - AI结果（JSON对象）

**JSON格式**：
```json
{
  "recommendation": "推荐车型信息",
  "analysis": "详细分析",
  "budgetAdvice": "预算建议"
}
```

**前端代码位置**：ConsultationForm.vue: `consultResult`

---

## 功能分类

### 👤 用户功能（role='user'）

使用的表和字段：

#### users表
- 基本信息：`username`, `password`, `name`, `phone`, `email`
- 积分：`points`

#### consultations表
- 所有字段（创建、查看自己的咨询）

### 👨‍💼 管理员功能（role='admin'）

使用的表和字段：

#### users表
- 查看所有用户
- 管理用户状态：`status`（启用/禁用）
- 查看用户信息：`username`, `name`, `phone`, `email`, `points`

#### consultations表
- 查看所有咨询记录
- 统计分析：按 `car_type`, `budget`, `ai_model` 分组统计

**统计字段**（AdminView.vue中使用）：
- 总用户数：`COUNT(users)`
- 总咨询数：`COUNT(consultations)`
- 今日咨询：`COUNT(consultations WHERE DATE(created_at) = CURDATE())`
- 活跃用户：`COUNT(users WHERE status='active')`

---

## 使用说明

### 初始化数据库

```bash
mysql -u root -p < schema.sql
```

### 默认账号

**管理员账号**：
- 用户名：`admin`
- 密码：`admin123`（实际部署时应修改）

**测试用户**：
- 用户名：`user001`
- 密码：`123456`

### API集成建议

#### 用户注册
```sql
INSERT INTO users (username, password, name, phone, email, budget, preferred_type, use_case, fuel_type)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
```

#### 用户登录
```sql
SELECT id, username, role, name, phone, email, budget, preferred_type, points, status
FROM users
WHERE username = ? AND password = ?;
```

#### 创建咨询
```sql
INSERT INTO consultations (user_id, title, budget, car_type, use_case, fuel_type, brands, description, ai_model, result)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
```

#### 获取用户咨询列表
```sql
SELECT * FROM consultations
WHERE user_id = ?
ORDER BY created_at DESC;
```

#### 管理员 - 获取所有用户
```sql
SELECT id, username, name, phone, email, points, status, created_at,
       (SELECT COUNT(*) FROM consultations WHERE user_id = users.id) as consultCount
FROM users
WHERE role = 'user'
ORDER BY created_at DESC;
```

#### 管理员 - 获取统计数据
```sql
-- 总用户数
SELECT COUNT(*) as totalUsers FROM users WHERE role = 'user';

-- 总咨询数
SELECT COUNT(*) as totalConsultations FROM consultations;

-- 今日咨询
SELECT COUNT(*) as todayConsultations
FROM consultations
WHERE DATE(created_at) = CURDATE();

-- 车型分布
SELECT car_type, COUNT(*) as count
FROM consultations
GROUP BY car_type
ORDER BY count DESC;
```

### 积分规则（硬编码）

```javascript
// 后端代码中定义
const POINTS_RULES = {
  CONSULTATION: 10,  // 完成咨询
  RATING: 5,         // 提供评分
  REFERRAL: 20       // 推荐好友
}
```

### 环境变量配置

```bash
# .env
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=car_consultation_system

# AI API密钥
QWEN_API_KEY=your_qwen_api_key
ZHIPU_API_KEY=your_zhipu_api_key
```

---

## 数据迁移

如果从旧版数据库迁移：

```sql
-- 1. 备份旧数据
mysqldump -u root -p car_consultation_system > backup.sql

-- 2. 导出用户数据
SELECT id, username, password, name, phone, email, role, 
       budget, preferred_type, fuel_preference as fuel_type, 
       brand_preference as use_case, points, status, 
       created_at, updated_at, last_login_at
FROM users;

-- 3. 导出咨询数据
SELECT id, user_id, title, budget, car_type, 
       use_case, fuel_type, brands, description, 
       ai_model, result, rating, created_at, updated_at
FROM consultations;

-- 4. 导入到新数据库
-- 使用新的 schema.sql 初始化
-- 然后导入导出的数据
```

---

## 性能优化建议

### 索引优化
- ✅ 已创建：用户名、角色、状态、外键
- ✅ 已创建：创建时间（用于排序）
- ✅ 已创建：车型、AI模型（用于统计）

### 查询优化
```sql
-- 使用索引查询
SELECT * FROM consultations 
WHERE user_id = ? AND car_type = 'SUV'
ORDER BY created_at DESC
LIMIT 10;

-- 避免全表扫描
SELECT * FROM users 
WHERE status = 'active' AND role = 'user';
```

### 分页查询
```sql
-- 分页获取咨询列表
SELECT * FROM consultations
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT ? OFFSET ?;
```

---

## 安全建议

1. **密码加密**：使用 bcrypt 或 argon2
2. **SQL注入防护**：使用参数化查询
3. **API密钥**：存储在环境变量，不要硬编码
4. **权限控制**：严格检查用户角色
5. **数据验证**：后端验证所有输入

---

**版本**：v2.0（精简版）  
**最后更新**：2024-11-11  
**维护者**：智能购车咨询系统团队
