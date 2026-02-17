# 雷达图核心功能 API 测试文档

<!--
  WHAT: 雷达图核心功能 API 测试指南
  WHY: 提供完整的接口测试示例，便于开发和验证
  WHEN: 2026-02-16
-->

## 概述

本文档提供雷达图核心功能所有 REST API 的测试示例。

## 基础信息

- **Base URL:** `http://localhost:8081`
- **API 文档:** `http://localhost:8081/doc.html` (Knife4j)
- **认证方式:** JWT Bearer Token

## 认证说明

所有 API（除认证接口外）都需要在请求头中携带 JWT Token：

```
Authorization: Bearer <your_token>
```

### 获取 Token

**接口:** `POST /api/auth/login`

**请求体:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**响应:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "testuser"
}
```

---

## API 接口测试

### 1. 创建雷达图

**接口:** `POST /api/radar-charts`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "title": "学生能力评估",
  "description": "期末考试成绩分析",
  "dimensions": [
    {
      "name": "语文",
      "minValue": 0,
      "maxValue": 100,
      "orderIndex": 0
    },
    {
      "name": "数学",
      "minValue": 0,
      "maxValue": 100,
      "orderIndex": 1
    },
    {
      "name": "英语",
      "minValue": 0,
      "maxValue": 100,
      "orderIndex": 2
    },
    {
      "name": "物理",
      "minValue": 0,
      "maxValue": 100,
      "orderIndex": 3
    },
    {
      "name": "化学",
      "minValue": 0,
      "maxValue": 100,
      "orderIndex": 4
    }
  ]
}
```

**期待响应 (201 Created):**
```json
{
  "id": 1,
  "title": "学生能力评估",
  "description": "期末考试成绩分析",
  "dimensions": [
    {
      "id": 1,
      "name": "语文",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 0
    },
    {
      "id": 2,
      "name": "数学",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 1
    },
    {
      "id": 3,
      "name": "英语",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 2
    },
    {
      "id": 4,
      "name": "物理",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 3
    },
    {
      "id": 5,
      "name": "化学",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 4
    }
  ],
  "series": [],
  "createTime": "2026-02-16T10:00:00",
  "updateTime": "2026-02-16T10:00:00"
}
```

---

### 2. 获取当前用户的雷达图列表

**接口:** `GET /api/radar-charts`

**请求头:**
```
Authorization: Bearer <token>
```

**期待响应 (200 OK):**
```json
[
  {
    "id": 1,
    "title": "学生能力评估",
    "description": "期末考试成绩分析",
    "dimensions": [...],
    "series": [...],
    "createTime": "2026-02-16T10:00:00",
    "updateTime": "2026-02-16T10:00:00"
  }
]
```

---

### 3. 获取雷达图详情

**接口:** `GET /api/radar-charts/{id}`

**请求头:**
```
Authorization: Bearer <token>
```

**URL 参数:**
- `id`: 雷达图ID

**期待响应 (200 OK):**
```json
{
  "id": 1,
  "title": "学生能力评估",
  "description": "期末考试成绩分析",
  "dimensions": [
    {
      "id": 1,
      "name": "语文",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 0
    },
    {
      "id": 2,
      "name": "数学",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 1
    },
    {
      "id": 3,
      "name": "英语",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 2
    },
    {
      "id": 4,
      "name": "物理",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 3
    },
    {
      "id": 5,
      "name": "化学",
      "minValue": 0.0,
      "maxValue": 100.0,
      "orderIndex": 4
    }
  ],
  "series": [
    {
      "id": 1,
      "name": "小明",
      "data": {
        "1": 85,
        "2": 92,
        "3": 78,
        "4": 88,
        "5": 90
      },
      "createTime": "2026-02-16T10:05:00"
    }
  ],
  "createTime": "2026-02-16T10:00:00",
  "updateTime": "2026-02-16T10:05:00"
}
```

---

### 4. 更新雷达图基本信息

**接口:** `PUT /api/radar-charts/{id}`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "title": "学生能力评估（更新）",
  "description": "期末考试成绩详细分析"
}
```

**期待响应 (200 OK):**
```json
{
  "id": 1,
  "title": "学生能力评估（更新）",
  "description": "期末考试成绩详细分析",
  "dimensions": [...],
  "series": [...],
  "createTime": "2026-02-16T10:00:00",
  "updateTime": "2026-02-16T10:10:00"
}
```

---

### 5. 删除雷达图

**接口:** `DELETE /api/radar-charts/{id}`

**请求头:**
```
Authorization: Bearer <token>
```

**期待响应 (204 No Content):**
```
(无响应体)
```

---

### 6. 添加数据系列

**接口:** `POST /api/radar-charts/{id}/series`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "name": "小明",
  "data": [
    {
      "dimensionId": 1,
      "value": 85
    },
    {
      "dimensionId": 2,
      "value": 92
    },
    {
      "dimensionId": 3,
      "value": 78
    },
    {
      "dimensionId": 4,
      "value": 88
    },
    {
      "dimensionId": 5,
      "value": 90
    }
  ]
}
```

**期待响应 (201 Created):**
```json
{
  "id": 1,
  "name": "小明",
  "data": {
    "1": 85,
    "2": 92,
    "3": 78,
    "4": 88,
    "5": 90
  },
  "createTime": "2026-02-16T10:05:00"
}
```

---

### 7. 获取所有数据系列

**接口:** `GET /api/radar-charts/{id}/series`

**请求头:**
```
Authorization: Bearer <token>
```

**期待响应 (200 OK):**
```json
[
  {
    "id": 1,
    "name": "小明",
    "data": {
      "1": 85,
      "2": 92,
      "3": 78,
      "4": 88,
      "5": 90
    },
    "createTime": "2026-02-16T10:05:00"
  },
  {
    "id": 2,
    "name": "小红",
    "data": {
      "1": 92,
      "2": 88,
      "3": 95,
      "4": 85,
      "5": 93
    },
    "createTime": "2026-02-16T10:06:00"
  }
]
```

---

### 8. 更新系列名称

**接口:** `PUT /api/radar-charts/{id}/series/{seriesId}`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "name": "小明（2024年）"
}
```

**期待响应 (204 No Content):**
```
(无响应体)
```

---

### 9. 删除数据系列

**接口:** `DELETE /api/radar-charts/{id}/series/{seriesId}`

**请求头:**
```
Authorization: Bearer <token>
```

**期待响应 (204 No Content):**
```
(无响应体)
```

---

### 10. 更新系列数据值

**接口:** `PUT /api/radar-charts/{id}/series/{seriesId}/data`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "data": [
    {
      "dimensionId": 1,
      "value": 88
    },
    {
      "dimensionId": 2,
      "value": 95
    },
    {
      "dimensionId": 3,
      "value": 82
    },
    {
      "dimensionId": 4,
      "value": 90
    },
    {
      "dimensionId": 5,
      "value": 92
    }
  ]
}
```

**期待响应 (204 No Content):**
```
(无响应体)
```

---

### 11. 获取图表数据（ECharts格式）

**接口:** `GET /api/radar-charts/{id}/export`

**请求头:**
```
Authorization: Bearer <token>
```

**期待响应 (200 OK):**
```json
{
  "dimensions": ["语文", "数学", "英语", "物理", "化学"],
  "series": [
    {
      "name": "小明",
      "data": [85, 92, 78, 88, 90]
    },
    {
      "name": "小红",
      "data": [92, 88, 95, 85, 93]
    }
  ]
}
```

---

## 错误响应示例

### 400 Bad Request

**请求参数验证失败：**
```json
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "标题不能为空"
}
```

**数据值超出范围：**
```json
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "维度[语文]的值必须在0.00到100.00之间"
}
```

### 401 Unauthorized

**Token 无效或过期：**
```json
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token已过期"
}
```

### 403 Forbidden

**无权操作：**
```json
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "无权操作此雷达图"
}
```

### 404 Not Found

**资源不存在：**
```json
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "雷达图不存在"
}
```

---

## 测试场景

### 场景1: 完整创建流程

1. 注册/登录获取 Token
2. 创建雷达图（含5个维度）
3. 添加第一个数据系列
4. 添加第二个数据系列
5. 获取图表数据
6. 更新系列数据
7. 删除一个系列

### 场景2: 异常测试

1. 创建少于3个维度的雷达图（应失败）
2. 添加超出范围的数据值（应失败）
3. 操作其他用户的雷达图（应失败）
4. 使用无效的 dimensionId（应失败）

---

## 使用 Knife4j 测试

1. 启动应用后访问：`http://localhost:8081/doc.html`
2. 点击右上角 "Authorize" 输入 Token
3. 选择接口点击 "调试"
4. 填写请求参数
5. 点击 "发送" 查看响应

---

## 数据库初始化说明

在测试 API 之前，需要先初始化数据库：

```bash
# 方式1：使用 mysql 客户端
mysql -h 192.168.1.109 -u root -p < radar-chart-builder-server/src/main/resources/sql/schema.sql

# 方式2：使用 MariaDB 客户端
mariadb -h 192.168.1.109 -u root -p < radar-chart-builder-server/src/main/resources/sql/schema.sql

# 方式3：在数据库客户端工具中执行 SQL 文件内容
```

验证表创建成功：

```sql
USE radar_chart_builder;
SHOW TABLES;
DESC radar_chart;
DESC dimension;
DESC data_series;
DESC series_data;
```
