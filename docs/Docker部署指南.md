# 雷达图生成器 - Docker 部署指南

<!--
  WHAT: Docker 容器化部署完整指南（数据库外部部署）
  WHY: 使用 Docker 实现一键部署，确保环境一致性，简化运维
  WHEN: 首次部署或环境迁移时参考
-->

## 目录

1. [环境准备](#1-环境准备)
2. [Docker Desktop 安装](#2-docker-desktop-安装)
3. [外部数据库准备](#3-外部数据库准备)
4. [项目架构说明](#4-项目架构说明)
5. [部署方案设计](#5-部署方案设计)
6. [配置文件准备](#6-配置文件准备)
7. [部署步骤](#7-部署步骤)
8. [验证部署](#8-验证部署)
9. [常见问题](#9-常见问题)
10. [生产环境优化](#10-生产环境优化)

---

## 1. 环境准备

### 1.1 系统要求

| 组件 | 最低要求 | 推荐配置 |
|------|---------|---------|
| 操作系统 | Windows 10 64bit / macOS 11+ | Windows 11 / macOS 13+ |
| 内存 | 4GB | 8GB+ |
| 磁盘空间 | 5GB 可用空间 | 10GB+ 可用空间 |
| CPU | 2 核 | 4 核+ |

### 1.2 软件要求

- Docker Desktop 4.30+（Windows/Mac）
- Docker Compose 2.20+（随 Docker Desktop 安装）
- Git（用于克隆项目）
- MariaDB 10.6+ 或 MySQL 8.0+（外部部署）
- 文本编辑器（VS Code 推荐）

---

## 2. Docker Desktop 安装

### 2.1 Windows 安装

**步骤：**

1. 访问 Docker Desktop 官网：https://www.docker.com/products/docker-desktop/

2. 点击 "Download for Windows"

3. 运行安装程序 `Docker Desktop Installer.exe`

4. 安装过程中确保勾选：
   - ✅ Use WSL 2 instead of Hyper-V（推荐）
   - ✅ Add shortcut to desktop

5. 安装完成后重启计算机

6. 首次启动 Docker Desktop：
   - 接受服务协议
   - 登录 Docker Hub（可选）
   - 等待 Docker 启动（状态栏显示绿色图标）

**验证安装：**

```powershell
# 打开 PowerShell 或 Command Prompt
docker --version
docker-compose --version
```

### 2.2 macOS 安装

**步骤：**

1. 确认 Mac 芯片类型：
   - Apple Silicon（M1/M2/M3）
   - Intel 芯片

2. 访问：https://www.docker.com/products/docker-desktop/

3. 下载对应的 .dmg 文件

4. 打开 .dmg 文件，将 Docker 图标拖到 Applications 文件夹

5. 启动 Docker Desktop

6. 首次启动：
   - 接受服务协议
   - 授权安装系统组件
   - 等待 Docker 引擎启动

### 2.3 Docker Desktop 基础使用

**常用命令：**

```bash
# 查看版本
docker --version

# 查看运行中的容器
docker ps

# 查看镜像
docker images

# 查看日志
docker logs <容器名>
```

---

## 3. 外部数据库准备

### 3.1 数据库选项

| 数据库类型 | 推荐场景 | 版本要求 |
|-----------|---------|---------|
| MySQL | 兼容性最好 | 8.0+ |
| MariaDB | MySQL 的开源分支 | 10.6+ |
| 云数据库 | 阿里云 RDS、腾讯云等 | MySQL 8.0+ |

### 3.2 本地安装 MariaDB

**Windows 安装：**

1. 下载：https://mariadb.org/download/
2. 运行安装程序，使用默认配置
3. 设置 root 密码
4. 启动 MariaDB 服务

**Mac 安装：**

```bash
brew install mariadb
brew services start mariadb
mysql_secure_installation
```

### 3.3 创建数据库和用户

**连接数据库：**

```sql
mysql -u root -p
```

**创建数据库：**

```sql
CREATE DATABASE radar_chart_builder
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER 'radar'@'%' IDENTIFIED BY 'radar123456';
GRANT ALL PRIVILEGES ON radar_chart_builder.* TO 'radar'@'%';
FLUSH PRIVILEGES;
EXIT;
```

### 3.4 初始化数据库表

**执行脚本：**

```bash
# Windows PowerShell
cd radar-chart-builder
type radar-chart-builder-server\src\main\resources\sql\schema.sql | mysql -u radar -p radar_chart_builder

# Mac/Linux
cd radar-chart-builder
cat radar-chart-builder-server/src/main/resources/sql/schema.sql | mysql -u radar -p radar_chart_builder
```

或使用数据库客户端（Navicat、DBeaver、HeidiSQL）手动执行脚本。

---

## 4. 项目架构说明

### 4.1 技术栈

**后端：**
- Spring Boot 3.5.10
- Java 21
- MyBatis-Plus 3.5.16
- Spring Security + JWT

**前端：**
- Vue 3.5
- Vite 7.3
- Element Plus 2.13
- ECharts 6.0
- Nginx（生产环境）

### 4.2 应用端口

| 应用 | 端口 | 说明 |
|------|------|------|
| 后端服务 | 8081 | Docker 容器 |
| 前端服务 | 3000 | Docker 容器 |
| 数据库 | 3306 | 外部部署 |

---

## 5. 部署方案设计

### 5.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Docker Desktop                         │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              docker-compose.yml                        │ │
│  │  ┌────────────────┐    ┌──────────────┐               │ │
│  │  │  Nginx (Frontend)│───→│  radar-net    │               │ │
│  │  │  Port: 3000     │    │              │               │ │
│  │  └────────────────┘    │              │               │ │
│  │                        │              │               │ │
│  │  ┌────────────────┐    │              │               │ │
│  │  │Spring Boot API │───→│  (外部网络)   │──→ 外部数据库│ │
│  │  │Port: 8081       │    │              │    Port: 3306 │ │
│  │  └────────────────┘    └──────────────┘               │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌────────────────┐    ┌────────────────┐                  │
│  │  .env 文件     │    │  外部数据库    │                  │
│  │  (连接配置)     │    │  MariaDB/MySQL │                  │
│  └────────────────┘    └────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 容器规划

| 容器名 | 镜像 | 端口映射 | 说明 |
|--------|------|---------|------|
| radar-backend | radar-chart-builder-server:latest | 8081:8081 | 后端 API |
| radar-frontend | radar-chart-builder-web:latest | 3000:80 | 前端服务 |

### 5.3 网络配置

- **网络名称**：`radar-net`
- **驱动类型**：`bridge`
- **容器间通信**：通过服务名访问

---

## 6. 配置文件准备

### 6.1 后端 Dockerfile

**路径：** `radar-chart-builder-server/Dockerfile`

```dockerfile
# 多阶段构建：构建阶段
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制 pom.xml 并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 创建非 root 用户
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# 暴露端口
EXPOSE 8081

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### 6.2 后端生产环境配置

**路径：** `radar-chart-builder-server/src/main/resources/application-prod.yml`

```yaml
spring:
  application:
    name: radar-chart-builder-server
  datasource:
    url: jdbc:mariadb://${DB_HOST}:${DB_PORT:-3306}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.mariadb.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath:mapper/*.xml

server:
  port: 8081

logging:
  level:
    root: WARN
    com.radarchart: INFO
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n'

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:-43200000}
```

### 6.3 前端 Dockerfile

**路径：** `radar-chart-builder-web/Dockerfile`

```dockerfile
# 构建阶段
FROM node:22-alpine AS builder

WORKDIR /app

# 复制 package 文件
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 设置生产环境 API 地址
ENV VITE_API_BASE_URL=/api

# 构建项目
RUN npm run build-only

# 运行阶段
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
```

### 6.4 前端 Nginx 配置

**路径：** `radar-chart-builder-web/nginx.conf`

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    keepalive_timeout 65;
    client_max_body_size 20M;

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript
               application/json application/javascript application/xml+rss;

    server {
        listen 80;
        server_name _;
        root /usr/share/nginx/html;
        index index.html;

        # 静态资源缓存
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }

        # API 代理
        location /api/ {
            proxy_pass http://radar-backend:8081/api/;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_cache_bypass $http_upgrade;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        # Vue Router history 模式
        location / {
            try_files $uri $uri/ /index.html;
        }

        # 安全头
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;
    }
}
```

### 6.5 Docker Compose 配置

**路径：** `docker-compose.yml`

```yaml
version: '3.8'

services:
  # 后端服务
  radar-backend:
    build:
      context: ./radar-chart-builder-server
      dockerfile: Dockerfile
    container_name: radar-backend
    restart: unless-stopped
    environment:
      - DB_HOST=${DB_HOST:-host.docker.internal}
      - DB_PORT=${DB_PORT:-3306}
      - DB_NAME=${DB_NAME:-radar_chart_builder}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=${JWT_EXPIRATION:-43200000}
    networks:
      - radar-net
    extra_hosts:
      - "host.docker.internal:host-gateway"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # 前端服务
  radar-frontend:
    build:
      context: ./radar-chart-builder-web
      dockerfile: Dockerfile
    container_name: radar-frontend
    restart: unless-stopped
    ports:
      - "3000:80"
    depends_on:
      - radar-backend
    networks:
      - radar-net

networks:
  radar-net:
    driver: bridge
```

### 6.6 环境变量配置

**路径：** `.env.example`

```bash
# 数据库配置（外部数据库）
DB_HOST=host.docker.internal  # Windows/Mac Docker Desktop
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=radar
DB_PASSWORD=radar123456  # 请修改为强密码

# JWT 配置
JWT_SECRET=your-256-bit-secret-key-change-this-in-production
JWT_EXPIRATION=43200000  # 12小时（毫秒）
```

**生产环境 .env 示例：**

```bash
# 云数据库配置示例
DB_HOST=rm-xxxxx.mysql.rds.aliyuncs.com
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=radar_user
DB_PASSWORD=YourStrongPassword123!

# JWT 配置
JWT_SECRET=$(openssl rand -base64 32)
JWT_EXPIRATION=43200000
```

**使用方法：**

```bash
# 复制模板
cp .env.example .env

# 编辑 .env 文件
# 修改数据库连接信息
# 修改 JWT_SECRET 为随机字符串
```

---

## 7. 部署步骤

### 7.1 准备工作

**1. 准备数据库**

参考 [第3章：外部数据库准备](#3-外部数据库准备)

**2. 克隆项目（如需要）**

```bash
git clone https://github.com/tuokun/radar-chart-builder.git
cd radar-chart-builder
```

**3. 创建配置文件**

```bash
cp .env.example .env

# 编辑 .env 文件，配置数据库连接信息
```

### 7.2 使用 Docker Desktop 部署

#### 方法一：使用命令行（推荐）

**Windows PowerShell：**

```powershell
cd C:\Users\YourName\Projects\radar-chart-builder
docker-compose up -d --build
```

**Mac Terminal：**

```bash
cd ~/Projects/radar-chart-builder
docker-compose up -d --build
```

#### 方法二：使用 Docker Desktop 界面

1. 启动 Docker Desktop

2. 配置文件共享：
   - Settings → Resources → File Sharing
   - 添加项目路径
   - 点击 "Apply & Restart"

3. 打开项目目录：
   - Docker Desktop → 选择 "Compose"
   - 点击 "Browse" 选择项目目录
   - 选择 docker-compose.yml
   - 点击 "Up" 按钮

### 7.3 查看服务状态

```bash
# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker logs radar-backend
docker logs radar-frontend
```

---

## 8. 验证部署

### 8.1 检查容器状态

```bash
docker ps
```

预期输出：
```
CONTAINER ID   IMAGE              COMMAND
radar-backend   radar-backend      "java -jar app.jar"
radar-frontend  radar-frontend     "nginx -g daemon off"
```

### 8.2 测试数据库连接

```bash
# 测试后端容器能否访问外部数据库
docker exec radar-backend ping host.docker.internal
```

### 8.3 访问应用

| 功能 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost:3000 | 主页面 |
| 后端 API | http://localhost:3000/api/actuator/health | 健康检查 |
| API 文档 | http://localhost:3000/api/doc.html | Swagger 文档 |

### 8.4 功能测试

1. 注册新用户
2. 登录系统
3. 创建雷达图（添加维度）
4. 添加数据组
5. 查看图表预览
6. 刷新页面验证数据持久化

---

## 9. 常见问题

### 9.1 无法连接数据库

**问题：** 后端日志显示 "Could not connect to database"

**解决方案：**

1. 检查 .env 配置是否正确
2. 检查外部数据库是否运行

```bash
# Windows
telnet 127.0.0.1 3306

# Mac/Linux
nc -zv 127.0.0.1 3306
```

3. 检查防火墙设置

4. **Windows Docker Desktop 特殊配置：**

在 `.env` 中使用 `host.docker.internal` 访问主机：

```bash
DB_HOST=host.docker.internal
```

**如果 host.docker.internal 不工作，尝试：**

```bash
# Windows: 使用主机 IP 地址
DB_HOST=192.168.x.x

# 或者添加 extra_hosts（已在 docker-compose.yml 中配置）
```

### 9.2 容器启动失败

**查看日志：**

```bash
docker logs radar-backend
docker logs radar-frontend
docker-compose logs
```

**重新构建：**

```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### 9.3 前端无法访问后端 API

**检查网络：**

```bash
docker network inspect radar-chart-builder_radar-net
```

**测试容器间通信：**

```bash
docker exec radar-frontend ping radar-backend
```

### 9.4 Windows WSL2 问题

**重置 WSL2：**

```powershell
wsl --shutdown
# 等待几秒后重启 Docker Desktop
```

### 9.5 端口冲突

**修改端口映射：**

编辑 `docker-compose.yml`：

```yaml
radar-frontend:
  ports:
    - "8080:80"  # 改为其他端口
```

---

## 10. 生产环境优化

### 10.1 安全配置

**1. 使用强密码**

```bash
# 生成强密码
openssl rand -base64 32
```

**2. 配置 HTTPS**

使用 Nginx 反向代理 + SSL 证书

**3. 限制资源**

编辑 `docker-compose.yml`：

```yaml
radar-backend:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 512M
```

### 10.2 日志管理

```yaml
services:
  radar-backend:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 10.3 健康检查

已内置在 docker-compose.yml 中，可通过以下方式查看：

```bash
curl http://localhost:3000/api/actuator/health
```

---

## 附录：Docker 常用命令

```bash
# 容器管理
docker ps                    # 查看运行中的容器
docker logs <容器名>         # 查看日志
docker logs -f <容器名>      # 实时查看日志
docker exec -it <容器名> sh # 进入容器
docker-compose up -d        # 后台启动
docker-compose down         # 停止所有服务
docker-compose logs -f       # 查看所有日志
docker-compose restart      # 重启服务
docker-compose build        # 重新构建镜像
```

---

**更新日志：**

| 日期 | 版本 | 更新内容 |
|------|------|---------|
| 2025-02-25 | 1.0 | 初始版本（数据库外部部署） |
