# 雷达图生成器

一个在线雷达图创建工具，支持自定义维度、多数据系列对比展示和实时预览。

## 项目概述

雷达图生成器允许用户：
- 自定义雷达图维度（名称、最小值、最大值）
- 添加多个数据系列进行对比
- 可视化生成多边形雷达图
- 实时预览和编辑

## 技术栈

### 后端
- SpringBoot 3.5 + Java 21
- MariaDB 11.4.7
- MyBatis-Plus 3.5.16
- JWT 认证
- Knife4j (API 文档)

### 前端
- Vue 3
- Element Plus
- ECharts 6.0
- Pinia (状态管理)
- Vue Router
- Axios

### 部署
- Docker 容器化
- Docker Compose 编排

## 系统架构

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   浏览器    │ ───▶│   Nginx      │ ───▶│  SpringBoot │
│  :3000      │     │  (前端静态)   │     │  (后端API)  │
└─────────────┘     └─────────────┘     └─────────────┘
                           │                   │
                           │                   ▼
                           │            ┌─────────────┐
                           │            │   MariaDB    │
                           │            │ (远程数据库) │
                           │            └─────────────┘
```

## 功能特性

- ✅ 用户注册/登录（JWT 认证）
- ✅ 自定义雷达图维度（名称、最小值、最大值）
- ✅ 多数据系列支持
- ✅ 系列颜色设置
- ✅ 实时预览
- ✅ 图表管理（查看、编辑、删除）
- ✅ 响应式界面

## 快速开始

### 前置要求

- Docker 和 Docker Compose
- MariaDB 数据库

### 使用 Docker Compose 部署

1. 配置环境变量（创建 `.env` 文件）：

```env
# 数据库配置
DB_HOST=your_db_host
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT 配置
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=43200000

# 前端端口
FRONTEND_PORT=3000
```

2. 启动服务：

```bash
docker-compose up -d
```

3. 访问应用：

- 前端：http://localhost:3000
- 后端 API 文档：http://localhost:8081/doc.html

### 本地开发

**后端：**
```bash
cd radar-chart-builder-server
mvn spring-boot:run
```

**前端：**
```bash
cd radar-chart-builder-web
npm install
npm run dev
```

## 项目结构

```
radar-chart-builder/
├── docs/                           # 项目文档
│   ├── 产品需求文档.md
│   ├── 项目规划文档.md
│   ├── 接口文档/
│   ├── 雷达图生成器登录认证说明.md
│   └── 前端测试说明.md
├── radar-chart-builder-server/     # 后端服务
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── radar-chart-builder-web/        # 前端服务
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
├── .gitignore
└── .dockerignore
```

## 数据库设计

采用四表关联结构：

1. **radar_chart** - 雷达图表
2. **dimension** - 维度配置
3. **data_series** - 数据系列
4. **series_data** - 系列数据

详细数据表结构请参考 `radar-chart-builder-server/src/main/resources/sql/schema.sql`

## 项目进度

| 阶段 | 状态 | 说明 |
|------|------|------|
| Phase 1: 后端基础架构 | ✅ 完成 | 用户认证 + MyBatis-Plus |
| Phase 2: 雷达图核心功能 | ✅ 完成 | CRUD + 四表结构 |
| Phase 4: 前端开发 | ✅ 完成 | Vue3 + Element Plus + ECharts |
| Phase 7: 前端联调测试 | ✅ 完成 | 功能优化 + UI/UX 改进 |
| Phase 8: Docker 部署 | ✅ 完成 | 容器化 + docker-compose |

## License

MIT License
