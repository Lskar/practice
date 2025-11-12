# 智能购车咨询系统 - API文档（精简版）

## 📋 目录

- [概述](#概述)
- [认证授权](#认证授权)
- [用户接口](#用户接口)
- [咨询接口](#咨询接口)
- [管理员接口](#管理员接口)
- [错误码](#错误码)
- [附录](#附录)

---

## 概述

### 基础信息

- **Base URL**: `http://api.example.com/v1`
- **协议**: HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8

### 通用响应格式

**成功响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1699699200000
}
```

**错误响应**：
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null,
  "timestamp": 1699699200000
}
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

---

## 认证授权

### 1. 用户注册

`POST /api/auth/register`

**请求参数**：

```json
{
  "username": "user001",           // 必填，3-20字符
  "password": "123456",             // 必填，至少6字符
  "confirmPassword": "123456",      // 必填，与password一致
  "name": "张三",                   // 必填
  "phone": "13800138000",           // 必填，11位手机号
  "email": "user@example.com"       // 可选，邮箱格式
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 123,
    "username": "user001"
  }
}
```

**数据库操作**：
```sql
INSERT INTO users (username, password, name, phone, email, role, points, status)
VALUES (?, ?, ?, ?, ?, 'user', 0, 'active');
```

**前端代码对应**：`RegisterView.vue` - `registerForm`

---

### 2. 用户登录

`POST /api/auth/login`

**请求参数**：

```json
{
  "username": "user001",
  "password": "123456"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 123,
      "username": "user001",
      "role": "user",
      "name": "张三",
      "phone": "13800138000",
      "email": "user@example.com",
      "points": 150
    }
  }
}
```

**数据库操作**：
```sql
SELECT id, username, role, name, phone, email, points, status
FROM users
WHERE username = ? AND password = ?;
```

**前端代码对应**：
- `LoginView.vue` - `loginForm`
- `user.js` - `login(userData)`

---

### 3. 退出登录

`POST /api/auth/logout`

**请求头**: 需要 `Authorization`

**响应示例**：

```json
{
  "code": 200,
  "message": "退出成功"
}
```

**前端代码对应**：
- `HomeView.vue` / `AdminView.vue` - `handleLogout()`
- `user.js` - `logout()`

---

## 用户接口

### 1. 获取个人信息

`GET /api/users/profile`

**请求头**: 需要 `Authorization`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 123,
    "username": "user001",
    "role": "user",
    "name": "张三",
    "phone": "13800138000",
    "email": "user@example.com",
    "points": 150
  }
}
```

**数据库操作**：
```sql
SELECT id, username, role, name, phone, email, points
FROM users
WHERE id = ?;
```

**前端代码对应**：`user.js` - `userInfo`

---

### 2. 更新个人信息

`PUT /api/users/profile`

**请求头**: 需要 `Authorization`

**请求参数**：

```json
{
  "name": "张三",                   // 可选
  "phone": "13800138001",           // 可选
  "email": "newemail@example.com"   // 可选
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 123,
    "name": "张三",
    "phone": "13800138001",
    "email": "newemail@example.com"
  }
}
```

**数据库操作**：
```sql
UPDATE users
SET name = ?, phone = ?, email = ?
WHERE id = ?;
```

**前端代码对应**：
- `ProfileSettings.vue` - `handleUpdateBasic()`
- `user.js` - `updateUserInfo(newInfo)`

---

### 3. 修改密码

`POST /api/users/change-password`

**请求头**: 需要 `Authorization`

**请求参数**：

```json
{
  "oldPassword": "123456",           // 必填
  "newPassword": "newpass123",       // 必填，至少6字符
  "confirmPassword": "newpass123"    // 必填，与newPassword一致
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "密码修改成功"
}
```

**数据库操作**：
```sql
-- 验证旧密码
SELECT id FROM users WHERE id = ? AND password = ?;

-- 更新密码
UPDATE users SET password = ? WHERE id = ?;
```

**前端代码对应**：`ProfileSettings.vue` - `handleUpdatePassword()`

---

## 咨询接口

### 1. 创建咨询

`POST /api/consultations`

**请求头**: 需要 `Authorization`

**请求参数**：

```json
{
  "title": "想买一辆家用SUV",                     // 必填
  "budget": "10-20万",                            // 必填
  "carType": "SUV",                               // 必填
  "useCase": ["通勤", "家庭"],                    // 必填，至少1个
  "fuelType": "混动",                             // 必填
  "brands": ["大众", "丰田", "比亚迪"],           // 可选，可空数组
  "description": "家里有两个孩子，需要空间大的车", // 必填，至少10字符
  "aiModel": "qwen"                               // 必填，qwen或zhipu
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "咨询创建成功",
  "data": {
    "id": 456,
    "userId": 123,
    "title": "想买一辆家用SUV",
    "budget": "10-20万",
    "carType": "SUV",
    "useCase": ["通勤", "家庭"],
    "fuelType": "混动",
    "brands": ["大众", "丰田", "比亚迪"],
    "description": "家里有两个孩子，需要空间大的车",
    "aiModel": "qwen",
    "result": {
      "recommendation": "推荐车型：\n1. 比亚迪宋PLUS DM-i\n2. 丰田威兰达双擎",
      "analysis": "基于您的家庭需求和预算，推荐混动SUV",
      "budgetAdvice": "建议预算控制在15-18万"
    },
    "points": 10,
    "createdAt": "2024-11-11T10:00:00Z"
  }
}
```

**业务逻辑**：
1. 调用AI服务获取推荐结果
2. 保存咨询记录到数据库
3. 用户积分 +10

**数据库操作**：
```sql
-- 插入咨询记录
INSERT INTO consultations 
(user_id, title, budget, car_type, use_case, fuel_type, brands, description, ai_model, result)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

-- 增加积分
UPDATE users SET points = points + 10 WHERE id = ?;
```

**前端代码对应**：
- `ConsultationForm.vue` - `form`, `handleSubmit()`
- `consultation.js` - `addConsultation()`

---

### 2. 获取我的咨询列表

`GET /api/consultations`

**请求头**: 需要 `Authorization`

**查询参数**：
```
page=1           // 可选，默认1
pageSize=10      // 可选，默认10
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 456,
        "title": "想买一辆家用SUV",
        "budget": "10-20万",
        "carType": "SUV",
        "aiModel": "qwen",
        "rating": 5,
        "createdAt": "2024-11-11T10:00:00Z"
      }
    ],
    "total": 15,
    "page": 1,
    "pageSize": 10
  }
}
```

**数据库操作**：
```sql
SELECT * FROM consultations
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT ? OFFSET ?;
```

**前端代码对应**：
- `ConsultationHistory.vue`
- `consultation.js` - `getUserConsultations(userId)`

---

### 3. 获取咨询详情

`GET /api/consultations/:id`

**请求头**: 需要 `Authorization`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 456,
    "userId": 123,
    "title": "想买一辆家用SUV",
    "budget": "10-20万",
    "carType": "SUV",
    "useCase": ["通勤", "家庭"],
    "fuelType": "混动",
    "brands": ["大众", "丰田", "比亚迪"],
    "description": "家里有两个孩子，需要空间大的车",
    "aiModel": "qwen",
    "result": {
      "recommendation": "推荐内容...",
      "analysis": "分析内容...",
      "budgetAdvice": "预算建议..."
    },
    "rating": 5,
    "createdAt": "2024-11-11T10:00:00Z",
    "updatedAt": "2024-11-11T10:05:00Z"
  }
}
```

**数据库操作**：
```sql
SELECT * FROM consultations
WHERE id = ? AND user_id = ?;
```

**前端代码对应**：`ConsultationHistory.vue` - 咨询详情展示

---

### 4. 评分咨询

`POST /api/consultations/:id/rating`

**请求头**: 需要 `Authorization`

**请求参数**：

```json
{
  "rating": 5        // 必填，1-5
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "评分成功，获得5积分",
  "data": {
    "consultationId": 456,
    "rating": 5,
    "points": 5
  }
}
```

**业务逻辑**：
1. 更新咨询评分
2. 用户积分 +5

**数据库操作**：
```sql
-- 更新评分
UPDATE consultations SET rating = ? WHERE id = ? AND user_id = ?;

-- 增加积分
UPDATE users SET points = points + 5 WHERE id = ?;
```

**前端代码对应**：`ConsultationForm.vue` - `handleRating()`

---

## 管理员接口

### 1. 获取数据概览

`GET /api/admin/dashboard`

**请求头**: 需要 `Authorization` (role='admin')

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 1286,
    "totalConsultations": 3542,
    "todayConsultations": 127,
    "activeUsers": 856
  }
}
```

**数据库操作**：
```sql
-- 总用户数
SELECT COUNT(*) as totalUsers FROM users WHERE role = 'user';

-- 总咨询数
SELECT COUNT(*) as totalConsultations FROM consultations;

-- 今日咨询
SELECT COUNT(*) as todayConsultations 
FROM consultations 
WHERE DATE(created_at) = CURDATE();

-- 活跃用户
SELECT COUNT(*) as activeUsers 
FROM users 
WHERE role = 'user' AND status = 'active';
```

**前端代码对应**：`AdminView.vue` - `mockData`

---

### 2. 获取用户列表

`GET /api/admin/users`

**请求头**: 需要 `Authorization` (role='admin')

**查询参数**：
```
page=1              // 可选，默认1
pageSize=20         // 可选，默认20
search=张三         // 可选，搜索用户名/姓名/手机
status=active       // 可选，筛选状态
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
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
    ],
    "total": 1286,
    "page": 1,
    "pageSize": 20
  }
}
```

**数据库操作**：
```sql
SELECT u.id, u.username, u.name, u.phone, u.email, u.points, u.status,
       (SELECT COUNT(*) FROM consultations WHERE user_id = u.id) as consultCount
FROM users u
WHERE u.role = 'user'
  AND (? IS NULL OR u.status = ?)
  AND (? IS NULL OR u.username LIKE ? OR u.name LIKE ? OR u.phone LIKE ?)
ORDER BY u.id DESC
LIMIT ? OFFSET ?;
```

**前端代码对应**：`AdminView.vue` - `mockUsers`

---

### 3. 更新用户状态

`PATCH /api/admin/users/:id/status`

**请求头**: 需要 `Authorization` (role='admin')

**请求参数**：

```json
{
  "status": "disabled"    // active 或 disabled
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "状态更新成功"
}
```

**数据库操作**：
```sql
UPDATE users 
SET status = ? 
WHERE id = ? AND role = 'user';
```

**前端代码对应**：`AdminView.vue` - `handleToggleUserStatus()`

---

### 4. 获取所有咨询记录

`GET /api/admin/consultations`

**请求头**: 需要 `Authorization` (role='admin')

**查询参数**：
```
page=1
pageSize=20
carType=SUV         // 可选
aiModel=qwen        // 可选
startDate=2024-01-01  // 可选
endDate=2024-12-31    // 可选
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 456,
        "userId": 123,
        "username": "user001",
        "name": "张三",
        "title": "想买一辆家用SUV",
        "carType": "SUV",
        "aiModel": "qwen",
        "rating": 5,
        "createdAt": "2024-11-11T10:00:00Z"
      }
    ],
    "total": 3542,
    "page": 1,
    "pageSize": 20
  }
}
```

**数据库操作**：
```sql
SELECT c.*, u.username, u.name
FROM consultations c
LEFT JOIN users u ON c.user_id = u.id
WHERE (? IS NULL OR c.car_type = ?)
  AND (? IS NULL OR c.ai_model = ?)
  AND (? IS NULL OR DATE(c.created_at) >= ?)
  AND (? IS NULL OR DATE(c.created_at) <= ?)
ORDER BY c.created_at DESC
LIMIT ? OFFSET ?;
```

**前端代码对应**：`AdminView.vue` - 咨询记录管理Tab

---

### 5. 数据统计 - 车型分布

`GET /api/admin/statistics/car-types`

**请求头**: 需要 `Authorization` (role='admin')

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "carType": "SUV", "count": 1250, "percentage": 42 },
    { "carType": "轿车", "count": 980, "percentage": 33 },
    { "carType": "MPV", "count": 456, "percentage": 15 },
    { "carType": "其他", "count": 296, "percentage": 10 }
  ]
}
```

**数据库操作**：
```sql
SELECT car_type as carType, 
       COUNT(*) as count,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY car_type
ORDER BY count DESC;
```

**前端代码对应**：`AdminView.vue` - `mockCarTypes`

---

### 6. 数据统计 - 预算分布

`GET /api/admin/statistics/budgets`

**请求头**: 需要 `Authorization` (role='admin')

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "range": "10万以下", "count": 342, "percentage": 18 },
    { "range": "10-20万", "count": 756, "percentage": 40 },
    { "range": "20-30万", "count": 523, "percentage": 28 },
    { "range": "30万以上", "count": 265, "percentage": 14 }
  ]
}
```

**数据库操作**：
```sql
SELECT budget as range,
       COUNT(*) as count,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY budget
ORDER BY count DESC;
```

**前端代码对应**：`AdminView.vue` - `mockBudgets`

---

### 7. 数据统计 - 热门话题

`GET /api/admin/statistics/topics`

**请求头**: 需要 `Authorization` (role='admin')

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "topic": "家用SUV推荐", "count": 568, "percentage": 45 },
    { "topic": "新能源车型对比", "count": 432, "percentage": 34 },
    { "topic": "预算20万左右轿车", "count": 265, "percentage": 21 }
  ]
}
```

**数据库操作**：
```sql
-- 基于咨询标题的关键词分析
SELECT title as topic,
       COUNT(*) as count,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM consultations), 0) as percentage
FROM consultations
GROUP BY title
ORDER BY count DESC
LIMIT 10;
```

**前端代码对应**：`AdminView.vue` - `mockTopics`

---

## 错误码

### HTTP状态码

| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问（权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 业务错误码

| 错误码 | 说明 |
|-------|------|
| 1001 | 用户名已存在 |
| 1002 | 用户名或密码错误 |
| 1003 | 用户不存在 |
| 1004 | 用户已被禁用 |
| 1005 | 原密码错误 |
| 1006 | 两次密码不一致 |
| 2001 | 咨询不存在 |
| 2002 | 无权访问此咨询 |
| 2003 | AI服务调用失败 |
| 3001 | Token已过期 |
| 3002 | Token无效 |
| 3003 | 权限不足（非管理员） |
| 9999 | 系统错误 |

**错误响应格式**：

```json
{
  "code": 1001,
  "message": "用户名已存在",
  "data": null,
  "timestamp": 1699699200000
}
```

---

## 附录

### 积分规则

| 操作 | 积分 | 说明 |
|-----|------|------|
| 完成咨询 | +10 | 创建咨询后自动获得 |
| 提供评分 | +5 | 为咨询结果评分 |
| 推荐好友 | +20 | 好友成功注册 |

**代码示例**：
```javascript
// 后端硬编码
const POINTS_RULES = {
  CONSULTATION: 10,
  RATING: 5,
  REFERRAL: 20
}
```

---

### 枚举值快速参考

#### 用户角色
```javascript
role: 'user' | 'admin'
```

#### 账号状态
```javascript
status: 'active' | 'disabled'
```

#### 购车预算
```javascript
budget: '10万以下' | '10-20万' | '20-30万' | '30-50万' | '50万以上'
```

#### 车型
```javascript
carType: 'SUV' | '轿车' | 'MPV' | '跑车' | '越野车'
```

#### 燃料类型
```javascript
fuelType: '燃油' | '电动' | '混动' | '不限'
```

#### 使用场景
```javascript
useCase: ['通勤', '家庭', '商务', '越野', '其他']  // 数组
```

#### 品牌偏好
```javascript
brands: ['大众', '丰田', '本田', '比亚迪', '特斯拉', '宝马', '奔驰', '奥迪']  // 数组
```

#### AI模型
```javascript
aiModel: 'qwen' | 'zhipu'
```

#### 评分
```javascript
rating: 1 | 2 | 3 | 4 | 5
```

---

### 环境变量配置

```bash
# .env
# 数据库
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=car_consultation_system

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRES_IN=7d

# AI API
QWEN_API_KEY=your_qwen_api_key
ZHIPU_API_KEY=your_zhipu_api_key
```

---

### 数据库表对应

| API接口 | 主要使用的表 | 操作类型 |
|---------|------------|---------|
| 用户注册 | users | INSERT |
| 用户登录 | users | SELECT |
| 个人信息 | users | SELECT, UPDATE |
| 创建咨询 | consultations, users | INSERT, UPDATE |
| 咨询列表 | consultations | SELECT |
| 评分咨询 | consultations, users | UPDATE |
| 用户管理 | users, consultations | SELECT, UPDATE |
| 数据统计 | consultations | SELECT (GROUP BY) |

---

**版本**：v2.0（精简版）  
**最后更新**：2024-11-11  
**前端对应版本**：Vue 3 + Pinia  
**数据库版本**：精简版（2张核心表）
