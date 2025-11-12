# 快速开始指南

## 📦 文件清单

```
database2/
├── schema.sql              # 数据库SQL脚本（核心）
├── README.md               # 详细设计文档
├── ER-DIAGRAM.md           # ER图文档（Markdown）
├── er-diagram.html         # ER图（可视化HTML）
├── API-DOCUMENTATION.md    # API接口文档
└── QUICK-START.md          # 本文件
```

---

## 🚀 3步快速部署

### 步骤1：初始化数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行SQL脚本
source /path/to/database2/schema.sql

# 或使用命令行直接导入
mysql -u root -p < schema.sql
```

### 步骤2：验证数据库

```sql
-- 检查数据库
SHOW DATABASES LIKE 'car_consultation_system';

-- 使用数据库
USE car_consultation_system;

-- 查看表
SHOW TABLES;

-- 应该看到2张表：
-- +-----------------------------------+
-- | Tables_in_car_consultation_system |
-- +-----------------------------------+
-- | users                             |
-- | consultations                     |
-- +-----------------------------------+

-- 查看示例数据
SELECT username, role, name FROM users;
```

### 步骤3：测试登录

```sql
-- 测试管理员账号
SELECT id, username, role FROM users 
WHERE username = 'admin';

-- 测试普通用户账号
SELECT id, username, role, points FROM users 
WHERE username = 'user001';
```

---

## 🔐 默认账号

### 管理员账号
- **用户名**：`admin`
- **密码**：`admin123`
- **角色**：admin

### 测试用户账号
| 用户名 | 密码 | 姓名 | 积分 |
|-------|------|-----|------|
| user001 | 123456 | 张三 | 150 |
| user002 | 123456 | 李四 | 200 |
| user003 | 123456 | 王五 | 80 |

> ⚠️ **安全提示**：生产环境请立即修改默认密码！

---

## 📊 数据库概览

### 表结构

```
users (用户表)
├── 9个字段
├── 包含普通用户和管理员
└── 主要索引：username, role, status

consultations (咨询记录表)
├── 14个字段
├── 外键：user_id → users.id
└── 主要索引：user_id, ai_model, car_type
```

### 关系图

```
users (1) ──────────── (N) consultations
  ↓
role = 'user'     → 普通用户功能
role = 'admin'    → 管理员功能
```

---

## 💡 常用查询

### 用户相关

```sql
-- 1. 查询所有普通用户
SELECT username, name, phone, points, status 
FROM users 
WHERE role = 'user'
ORDER BY created_at DESC;

-- 2. 查询用户咨询统计
SELECT u.username, u.name, u.points,
       COUNT(c.id) as consult_count
FROM users u
LEFT JOIN consultations c ON u.id = c.user_id
WHERE u.role = 'user'
GROUP BY u.id;

-- 3. 启用/禁用用户
UPDATE users 
SET status = 'disabled' 
WHERE id = ? AND role = 'user';
```

### 咨询相关

```sql
-- 1. 查询用户的咨询列表
SELECT * FROM consultations
WHERE user_id = ?
ORDER BY created_at DESC;

-- 2. 查询所有咨询（管理员）
SELECT c.*, u.username, u.name
FROM consultations c
LEFT JOIN users u ON c.user_id = u.id
ORDER BY c.created_at DESC;

-- 3. 车型统计
SELECT car_type, COUNT(*) as count
FROM consultations
GROUP BY car_type
ORDER BY count DESC;

-- 4. AI模型使用统计
SELECT ai_model, 
       COUNT(*) as total,
       AVG(rating) as avg_rating
FROM consultations
WHERE rating IS NOT NULL
GROUP BY ai_model;
```

### 数据统计

```sql
-- 管理员数据概览
SELECT 
    (SELECT COUNT(*) FROM users WHERE role='user') as total_users,
    (SELECT COUNT(*) FROM consultations) as total_consultations,
    (SELECT COUNT(*) FROM consultations WHERE DATE(created_at)=CURDATE()) as today_consultations,
    (SELECT COUNT(*) FROM users WHERE role='user' AND status='active') as active_users;
```

---

## 🎯 积分规则

| 操作 | 积分变化 | SQL示例 |
|-----|---------|---------|
| 完成咨询 | +10 | `UPDATE users SET points=points+10 WHERE id=?` |
| 提供评分 | +5 | `UPDATE users SET points=points+5 WHERE id=?` |
| 推荐好友 | +20 | `UPDATE users SET points=points+20 WHERE id=?` |

**后端代码示例**：
```javascript
const POINTS_RULES = {
  CONSULTATION: 10,
  RATING: 5,
  REFERRAL: 20
}
```

---

## 📝 枚举值快速参考

### users.role
```sql
'user' | 'admin'
```

### users.status
```sql
'active' | 'disabled'
```

### budget（预算）
```sql
'10万以下' | '10-20万' | '20-30万' | '30-50万' | '50万以上'
```

### car_type（车型）
```sql
'SUV' | '轿车' | 'MPV' | '跑车' | '越野车'
```

### fuel_type（燃料）
```sql
'燃油' | '电动' | '混动' | '不限'
```

### consultations.ai_model
```sql
'qwen' | 'zhipu'
```

### JSON数组示例

**use_case**:
```json
["通勤", "家庭"]
```

**brands**:
```json
["大众", "丰田", "比亚迪"]
```

**result**:
```json
{
  "recommendation": "推荐车型信息",
  "analysis": "详细分析",
  "budgetAdvice": "预算建议"
}
```

---

## 🔧 后端集成

### 环境变量配置

创建 `.env` 文件：

```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=car_consultation_system

# JWT配置
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRES_IN=7d

# AI API密钥
QWEN_API_KEY=your_qwen_api_key
ZHIPU_API_KEY=your_zhipu_api_key
```

### Node.js连接示例

```javascript
const mysql = require('mysql2/promise');

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

module.exports = pool;
```

### 用户注册API示例

```javascript
const bcrypt = require('bcrypt');

async function register(req, res) {
  const { username, password, name, phone, email, budget, preferredType, useCase, fuelType } = req.body;
  
  try {
    // 密码加密
    const hashedPassword = await bcrypt.hash(password, 10);
    
    // 插入数据库
    const [result] = await pool.execute(
      `INSERT INTO users (username, password, name, phone, email, budget, preferred_type, use_case, fuel_type)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [username, hashedPassword, name, phone, email, budget, preferredType, JSON.stringify(useCase), fuelType]
    );
    
    res.json({
      code: 200,
      message: '注册成功',
      data: {
        userId: result.insertId,
        username
      }
    });
  } catch (error) {
    if (error.code === 'ER_DUP_ENTRY') {
      res.status(400).json({ code: 1001, message: '用户名已存在' });
    } else {
      res.status(500).json({ code: 9999, message: '系统错误' });
    }
  }
}
```

---

## 📖 查看完整文档

- **数据库设计**：[README.md](./README.md)
- **ER图**：[er-diagram.html](./er-diagram.html)（浏览器打开）
- **API文档**：[API-DOCUMENTATION.md](./API-DOCUMENTATION.md)
- **ER图Markdown**：[ER-DIAGRAM.md](./ER-DIAGRAM.md)

---

## ⚠️ 注意事项

### 1. 密码安全
- ✅ 使用 bcrypt 或 argon2 加密
- ✅ 盐值轮数至少10轮
- ❌ 不要使用MD5或SHA1

### 2. SQL注入防护
- ✅ 使用参数化查询
- ✅ 使用ORM框架
- ❌ 不要直接拼接SQL

### 3. 权限控制
- ✅ 验证用户角色
- ✅ 检查资源所有权
- ❌ 不要信任前端传来的role

### 4. 数据验证
- ✅ 后端验证所有输入
- ✅ 验证枚举值范围
- ✅ 验证JSON格式

---

## 🐛 常见问题

### Q1: 导入SQL文件失败？
```bash
# 确保文件编码为UTF-8
# 检查MySQL版本（需要8.0+）
mysql --version

# 如果是字符集问题
mysql -u root -p --default-character-set=utf8mb4 < schema.sql
```

### Q2: JSON字段查询？
```sql
-- 查询use_case包含"通勤"的记录
SELECT * FROM users 
WHERE JSON_CONTAINS(use_case, '"通勤"');

-- 查询brands数组长度
SELECT *, JSON_LENGTH(brands) as brand_count 
FROM consultations;
```

### Q3: 外键约束错误？
```sql
-- 检查外键
SELECT * FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'car_consultation_system';

-- 临时禁用外键检查（谨慎使用）
SET FOREIGN_KEY_CHECKS=0;
-- 执行操作
SET FOREIGN_KEY_CHECKS=1;
```

---

## 📧 技术支持

如有问题，请查阅：
1. [README.md](./README.md) - 完整设计文档
2. [ER-DIAGRAM.md](./ER-DIAGRAM.md) - 详细ER图说明
3. [API-DOCUMENTATION.md](./API-DOCUMENTATION.md) - API接口文档

---

**版本**：v2.0（精简版）  
**最后更新**：2024-11-11
