# Docker 容器化部署设计文档

<!--
  WHAT: 雷达图生成器 Docker 容器化部署方案
  WHY: 实现本地测试和云服务器部署，最终为小程序上线做准备
  WHEN: 2026-03-02
-->

## 1. 概述

### 1.1 目标

- 在本地 Mac Docker Desktop 上完成容器化部署测试
- 实现一键启动后端 + 前端服务
- 为后续云服务器部署和 HTTPS 配置做准备

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| 后端 | Spring Boot 3.5 + Java 21 | Maven 3.9.12 |
| 前端 | Vue 3.5 + Vite 7.3 | Node 24.13.0 |
| 数据库 | MariaDB/MySQL 8.0+ | 外部部署（不容器化） |
| Web 服务器 | Nginx Alpine | 容器化部署 |

---

## 2. 架构设计

### 2.1 本地开发环境架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      本地 Mac 环境                               │
│                                                                  │
│  ┌──────────────────┐         ┌──────────────────────────────┐ │
│  │  Docker Compose   │         │   外部数据库（本地 MariaDB）   │ │
│  │                   │         │                              │ │
│  │  ┌──────────────┐│         │   Port: 3306                 │ │
│  │  │ Nginx 前端   ││         │   DB: radar_chart_builder    │ │
│  │  │ Port: 3000   ││←───────→│   User: root                 │ │
│  │  └──────────────┘│         │   Pass: cgfhsc               │ │
│  │         │         │         │                              │ │
│  │         ↓         │         │   通过 host.docker.internal │ │
│  │  ┌──────────────┐│         │   访问宿主机数据库           │ │
│  │  │Spring Boot   ││         └──────────────────────────────┘ │
│  │  │Port: 8081    ││                                         │
│  │  └──────────────┘│                                         │
│  └──────────────────┘                                         │
│                                                                  │
│  访问地址: http://localhost:3000                                │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 云服务器部署架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    腾讯云服务器                                  │
│                                                                  │
│  ┌──────────────────┐         ┌──────────────────────────────┐ │
│  │  Docker Compose   │         │   数据库（三种方案）          │ │
│  │                   │         │                              │ │
│  │  ┌──────────────┐│         │   A. 云数据库（推荐生产）     │ │
│  │  │ Nginx 前端   ││         │   B. Docker 容器（推荐测试）  │ │
│  │  │ Port: 80     ││←───────→│   C. 服务器本地安装          │ │
│  │  └──────────────┘│         │                              │ │
│  │         │         │         └──────────────────────────────┘ │
│  │         ↓         │                                         │
│  │  ┌──────────────┐│                                         │
│  │  │Spring Boot   ││                                         │
│  │  │Port: 8081    ││                                         │
│  │  └──────────────┘│                                         │
│  └──────────────────┘                                         │
│                                                                  │
│  访问地址: http://服务器IP                                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. 文件结构

```
radar-chart-builder/
├── docker-compose.yml          # Docker Compose 编排文件
├── .env                        # 环境变量配置（本地，不提交）
├── .env.example                # 环境变量模板（提交）
├── .dockerignore               # Docker 构建忽略文件
│
├── radar-chart-builder-server/
│   ├── Dockerfile              # 后端镜像构建文件
│   ├── .dockerignore           # 后端构建忽略
│   └── src/main/resources/
│       └── application-prod.yml  # 生产环境配置
│
├── radar-chart-builder-web/
│   ├── Dockerfile              # 前端镜像构建文件
│   ├── .dockerignore           # 前端构建忽略
│   └── nginx.conf              # Nginx 配置文件
│
└── deploy/                     # 部署相关目录
    ├── cloud.env.example       # 云服务器环境变量模板
    └── migrate.sh              # 迁移脚本（可选）
```

---

## 4. 核心配置文件

### 4.1 docker-compose.yml

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
    extra_hosts:
      - "host.docker.internal:host-gateway"
    networks:
      - radar-net

  # 前端服务
  radar-frontend:
    build:
      context: ./radar-chart-builder-web
      dockerfile: Dockerfile
    container_name: radar-frontend
    restart: unless-stopped
    ports:
      - "${FRONTEND_PORT:-3000}:80"
    depends_on:
      - radar-backend
    networks:
      - radar-net

networks:
  radar-net:
    driver: bridge
```

### 4.2 后端 Dockerfile

```dockerfile
# 构建阶段
FROM maven:3.9.12-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制 pom.xml 并下载依赖（利用缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# 非 root 用户运行
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### 4.3 前端 Dockerfile

```dockerfile
# 构建阶段
FROM node:24-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .

# 生产环境 API 地址（通过 Nginx 代理）
ENV VITE_API_BASE_URL=/api

RUN npm run build-only

# 运行阶段
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 4.4 nginx.conf

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

        # API 代理到后端
        location /api/ {
            proxy_pass http://radar-backend:8081/api/;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Vue Router history 模式
        location / {
            try_files $uri $uri/ /index.html;
        }

        # 安全头
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
    }
}
```

### 4.5 application-prod.yml

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

### 4.6 环境变量配置

**.env.example（模板，可提交）：**
```bash
# 数据库配置
DB_HOST=host.docker.internal
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=root
DB_PASSWORD=your-password

# JWT 配置
JWT_SECRET=change-this-to-a-random-key
JWT_EXPIRATION=43200000

# 前端端口
FRONTEND_PORT=3000
```

**.env（本地实际配置，不提交）：**
```bash
DB_HOST=host.docker.internal
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=root
DB_PASSWORD=cgfhsc
JWT_SECRET=local-dev-secret-key-2026-change-in-production
JWT_EXPIRATION=43200000
FRONTEND_PORT=3000
```

---

## 5. 部署步骤

### 5.1 本地部署

**步骤 1：创建配置文件**
```bash
cd /Users/cgfhsc/dev/project/radar-chart-builder
cp .env.example .env
# 编辑 .env 文件，填入实际配置
```

**步骤 2：初始化数据库**
```bash
# 确保数据库存在
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS radar_chart_builder DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;"

# 执行建表脚本
mysql -u root -p radar_chart_builder < radar-chart-builder-server/src/main/resources/sql/schema.sql
```

**步骤 3：启动服务**
```bash
docker-compose up -d --build
```

**步骤 4：查看日志**
```bash
docker-compose logs -f
```

**步骤 5：验证**
访问 http://localhost:3000

### 5.2 常用命令

| 操作 | 命令 |
|------|------|
| 启动 | `docker-compose up -d` |
| 停止 | `docker-compose down` |
| 重启 | `docker-compose restart` |
| 查看日志 | `docker-compose logs -f` |
| 重新构建 | `docker-compose build --no-cache && docker-compose up -d` |
| 进入后端容器 | `docker exec -it radar-backend sh` |
| 进入前端容器 | `docker exec -it radar-frontend sh` |

---

## 6. 验证测试清单

| 检查项 | 命令/方式 | 预期结果 |
|--------|----------|----------|
| 容器运行 | `docker ps` | 2个容器运行中 |
| 后端健康 | `curl http://localhost:3000/api/actuator/health` | status UP |
| 前端访问 | 浏览器打开 localhost:3000 | 页面正常显示 |
| 用户注册 | 页面操作 | 注册成功 |
| 用户登录 | 页面操作 | 登录成功 |
| 创建雷达图 | 页面操作 | 数据保存成功 |
| 刷新页面 | 浏览器刷新 | 数据持久化 |
| 查看日志 | `docker-compose logs` | 无 ERROR |

---

## 7. 云服务器迁移

### 7.1 服务器环境准备

**系统要求：**
- CentOS 7+ / Ubuntu 20.04+
- 2核4G 内存最低
- 20GB 磁盘空间

**安装 Docker：**
```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
```

**安装 Docker Compose：**
```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 7.2 云数据库方案对比

| 方案 | 优点 | 缺点 | 推荐场景 |
|------|------|------|----------|
| **云数据库** | 自动备份、高可用、免维护 | 成本较高 | 生产环境 |
| **Docker 容器** | 统一管理、部署简单 | 需要手动备份 | 中小型项目 |
| **本地安装** | 性能最好、完全控制 | 运维成本高 | 特殊需求 |

### 7.3 云服务器配置

**deploy/cloud.env：**
```bash
# 数据库配置
DB_HOST=your-cloud-db.tencentcdb.com
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=radar_user
DB_PASSWORD=your-strong-password

# JWT 配置（生产环境必须更改）
JWT_SECRET=production-secret-key-$(openssl rand -base64 32)
JWT_EXPIRATION=43200000

# 前端端口（云服务器改为 80）
FRONTEND_PORT=80
```

### 7.4 部署步骤

```bash
# 1. 上传项目
git clone <repo> /opt/radar-chart-builder
cd /opt/radar-chart-builder

# 2. 创建配置
cp deploy/cloud.env .env
# 编辑 .env

# 3. 启动
docker-compose up -d --build

# 4. 检查
docker-compose ps
docker-compose logs -f
```

---

## 8. 故障排查

### 8.1 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 无法连接数据库 | 网络不通 | 检查 `host.docker.internal` |
| 容器启动失败 | 配置错误 | 查看日志 `docker-compose logs` |
| 前端无法访问后端 | 网络配置 | 检查容器网络 `docker network inspect` |
| 端口冲突 | 端口被占用 | 修改 `.env` 中的 `FRONTEND_PORT` |

### 8.2 调试命令

```bash
# 测试数据库连接
docker exec radar-backend ping host.docker.internal

# 测试容器间通信
docker exec radar-frontend ping radar-backend

# 查看详细日志
docker-compose logs --tail=100 radar-backend
docker-compose logs --tail=100 radar-frontend
```

---

## 9. 后续优化

### 9.1 HTTPS 配置（小程序上线必需）

使用 Nginx + Let's Encrypt：

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    # ... 其他配置
}

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

### 9.2 性能优化

- 启用 Nginx gzip 压缩（已配置）
- 静态资源缓存（已配置）
- 后端 JVM 参数调优
- 数据库连接池优化

---

## 10. 决策记录

| 决策 | 理由 |
|------|------|
| 数据库外部部署 | 本地已有数据库，便于调试 |
| 多阶段构建 | 减小镜像体积 |
| 非 root 用户 | 提升安全性 |
| host.docker.internal | Docker Desktop 访问宿主机的标准方式 |
| Nginx Alpine | 最小化前端镜像 |
| 环境变量配置 | 灵活切换环境 |

---

**文档版本：** 1.0
**创建日期：** 2026-03-02
