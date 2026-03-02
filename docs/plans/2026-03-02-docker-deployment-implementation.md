# Docker 容器化部署实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为雷达图生成器项目创建完整的 Docker 容器化部署方案，支持本地 Mac 测试和云服务器迁移。

**Architecture:** 使用 Docker Compose 编排后端（Spring Boot）和前端（Nginx）容器，数据库采用外部部署（本地 Mac 使用 host.docker.internal 访问，云服务器可使用云数据库）。

**Tech Stack:** Docker Compose, Maven 3.9.12, Eclipse Temurin JRE 21, Node 24-alpine, Nginx Alpine

---

## 前置检查

在开始之前，确认以下环境：

```bash
# 检查 Docker Desktop 是否运行
docker --version
# 预期: Docker version 24.0.0 或更高

# 检查本地数据库
mysql -u root -p -e "SELECT VERSION();"
# 预期: 显示 MariaDB/MySQL 版本信息

# 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES LIKE 'radar_chart_builder';"
# 预期: radar_chart_builder 数据库已存在
```

---

## Task 1: 创建项目根目录配置文件

### Step 1.1: 创建 .dockerignore

**文件:** `.dockerignore`

```bash
# 创建文件
cat > .dockerignore << 'EOF'
# Git
.git
.gitignore
.claude

# IDE
.idea
*.iml
.DS_Store

# Docs
docs
*.md

# Logs
*.log

# Temp files
.env
*.tmp
EOF
```

**Step 1.2: 创建 .env.example

**文件:** `.env.example`

```bash
cat > .env.example << 'EOF'
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
EOF
```

**Step 1.3: 创建 .env（本地配置）

**文件:** `.env`

```bash
cat > .env << 'EOF'
DB_HOST=host.docker.internal
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=root
DB_PASSWORD=cgfhsc
JWT_SECRET=local-dev-secret-key-2026-change-in-production
JWT_EXPIRATION=43200000
FRONTEND_PORT=3000
EOF
```

**Step 1.4: 添加 .env 到 .gitignore

```bash
# 确保敏感配置不被提交
echo ".env" >> .gitignore
```

**Step 1.5: 提交配置文件

```bash
git add .dockerignore .env.example .gitignore
git commit -m "chore: add Docker config templates and ignore files"
```

---

## Task 2: 创建后端 Docker 配置

### Step 2.1: 创建后端 .dockerignore

**文件:** `radar-chart-builder-server/.dockerignore`

```bash
cat > radar-chart-builder-server/.dockerignore << 'EOF'
target/
*.iml
.idea/
.DS_Store
*.log
.git/
```
```

### Step 2.2: 创建后端 Dockerfile

**文件:** `radar-chart-builder-server/Dockerfile`

```dockerfile
# 构建阶段
FROM maven:3.9.12-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
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

### Step 2.3: 创建 application-prod.yml

**文件:** `radar-chart-builder-server/src/main/resources/application-prod.yml`

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

### Step 2.4: 提交后端配置

```bash
git add radar-chart-builder-server/Dockerfile radar-chart-builder-server/.dockerignore radar-chart-builder-server/src/main/resources/application-prod.yml
git commit -m "feat: add backend Dockerfile and prod config"
```

---

## Task 3: 创建前端 Docker 配置

### Step 3.1: 创建前端 .dockerignore

**文件:** `radar-chart-builder-web/.dockerignore`

```bash
cat > radar-chart-builder-web/.dockerignore << 'EOF'
node_modules/
dist/
.DS_Store
*.log
.env
.env.local
.git/
```
```

### Step 3.2: 创建前端 Dockerfile

**文件:** `radar-chart-builder-web/Dockerfile`

```dockerfile
# 构建阶段
FROM node:24-alpine AS builder

WORKDIR /app

# 复制 package 文件并安装依赖
COPY package*.json ./
RUN npm ci --only=production

# 复制源代码
COPY . .

# 生产环境 API 地址（通过 Nginx 代理）
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

### Step 3.3: 创建 nginx.conf

**文件:** `radar-chart-builder-web/nginx.conf`

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

### Step 3.4: 提交前端配置

```bash
git add radar-chart-builder-web/Dockerfile radar-chart-builder-web/.dockerignore radar-chart-builder-web/nginx.conf
git commit -m "feat: add frontend Dockerfile and nginx config"
```

---

## Task 4: 创建 Docker Compose 配置

### Step 4.1: 创建 docker-compose.yml

**文件:** `docker-compose.yml`

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

### Step 4.2: 提交 Docker Compose 配置

```bash
git add docker-compose.yml
git commit -m "feat: add docker-compose configuration"
```

---

## Task 5: 创建云服务器部署配置

### Step 5.1: 创建 deploy 目录

```bash
mkdir -p deploy
```

### Step 5.2: 创建云服务器环境变量模板

**文件:** `deploy/cloud.env.example`

```bash
cat > deploy/cloud.env.example << 'EOF'
# 数据库配置
# 选项1：云数据库（推荐生产环境）
DB_HOST=your-cloud-db.tencentcdb.com
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=radar_user
DB_PASSWORD=your-strong-password

# 选项2：服务器本地数据库（参考本地 .env）
# DB_HOST=host.docker.internal
# DB_PORT=3306
# DB_NAME=radar_chart_builder
# DB_USERNAME=root
# DB_PASSWORD=your-password

# JWT 配置（生产环境必须使用强密钥）
JWT_SECRET=production-secret-key-please-change-me
JWT_EXPIRATION=43200000

# 前端端口（云服务器使用 80 端口）
FRONTEND_PORT=80
EOF
```

### Step 5.3: 创建部署说明

**文件:** `deploy/README.md`

```markdown
# 云服务器部署指南

## 1. 服务器环境准备

### 安装 Docker

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
```

### 安装 Docker Compose

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

## 2. 部署步骤

```bash
# 克隆项目
git clone <your-repo-url> /opt/radar-chart-builder
cd /opt/radar-chart-builder

# 创建环境配置
cp deploy/cloud.env.example .env
# 编辑 .env 填写实际配置

# 启动服务
docker-compose up -d --build

# 检查状态
docker-compose ps
docker-compose logs -f
```

## 3. 防火墙配置

```bash
# 开放 80 端口
sudo ufw allow 80/tcp
# 或 CentOS
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --reload
```

## 4. 访问验证

访问: http://服务器IP
```

### Step 5.4: 提交部署配置

```bash
git add deploy/
git commit -m "feat: add cloud deployment configuration"
```

---

## Task 6: 数据库初始化验证

### Step 6.1: 检查数据库是否存在

```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'radar_chart_builder';"
```

**预期输出:**
```
+--------------------------------+
| Database (radar_chart_builder) |
+--------------------------------+
| radar_chart_builder            |
+--------------------------------+
```

### Step 6.2: 如果数据库不存在，创建数据库

```bash
mysql -u root -p << 'EOF'
CREATE DATABASE IF NOT EXISTS radar_chart_builder
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
EOF
```

### Step 6.3: 检查表是否存在

```bash
mysql -u root -p radar_chart_builder -e "SHOW TABLES;"
```

**预期输出:**
```
+--------------------------------+
| Tables_in_radar_chart_builder  |
+--------------------------------+
| data_series                    |
| dimension                      |
| radar_chart                    |
| series_data                    |
| users                          |
+--------------------------------+
```

### Step 6.4: 如果表不存在，执行建表脚本

```bash
mysql -u root -p radar_chart_builder < radar-chart-builder-server/src/main/resources/sql/schema.sql
```

### Step 6.5: 验证表结构

```bash
mysql -u root -p radar_chart_builder -e "DESCRIBE users;"
```

**预期输出:** 显示 users 表的字段结构

---

## Task 7: 构建和启动容器

### Step 7.1: 构建 Docker 镜像

```bash
docker-compose build --no-cache
```

**预期输出:** 构建过程日志，最终显示成功构建镜像

### Step 7.2: 启动容器

```bash
docker-compose up -d
```

**预期输出:**
```
[+] Running 3/3
 ✔ Network radar-chart-builder_radar-net  Created
 ✔ Container radar-backend               Started
 ✔ Container radar-frontend              Started
```

### Step 7.3: 检查容器状态

```bash
docker ps
```

**预期输出:**
```
CONTAINER ID   IMAGE                      COMMAND
radar-backend  radar-chart-builder-server "java -jar app.jar"
radar-frontend radar-chart-builder-web    "nginx -g daemon off"
```

### Step 7.4: 检查容器日志

```bash
# 查看所有日志
docker-compose logs

# 实时查看后端日志
docker-compose logs -f radar-backend

# 实时查看前端日志
docker-compose logs -f radar-frontend
```

---

## Task 8: 功能验证测试

### Step 8.1: 测试后端健康检查

```bash
curl http://localhost:3000/api/actuator/health
```

**预期输出:**
```json
{"status":"UP"}
```

### Step 8.2: 测试前端页面访问

在浏览器打开: http://localhost:3000

**预期结果:** 显示登录页面

### Step 8.3: 测试用户注册功能

1. 在浏览器点击"注册"
2. 填写用户信息
3. 提交注册

**预期结果:** 注册成功，跳转到登录页

### Step 8.4: 测试用户登录功能

1. 输入刚注册的用户名和密码
2. 点击登录

**预期结果:** 登录成功，显示雷达图列表

### Step 8.5: 测试创建雷达图功能

1. 点击"新建雷达图"
2. 填写雷达图信息（名称、描述）
3. 添加维度
4. 保存

**预期结果:** 创建成功，显示在列表中

### Step 8.6: 测试数据持久化

1. 刷新浏览器页面
2. 检查刚才创建的雷达图是否还在

**预期结果:** 数据正常显示，持久化成功

### Step 8.7: 验证数据库数据

```bash
mysql -u root -p radar_chart_builder -e "SELECT id, title, create_time FROM radar_chart ORDER BY create_time DESC LIMIT 5;"
```

**预期输出:** 显示刚才创建的雷达图记录

---

## Task 9: 常用操作验证

### Step 9.1: 测试停止服务

```bash
docker-compose down
```

**预期输出:**
```
[+] Running 3/3
 ✔ Container radar-frontend    Removed
 ✔ Container radar-backend     Removed
 ✔ Network radar-chart-builder_radar-net Removed
```

### Step 9.2: 测试重启服务

```bash
docker-compose up -d
```

**预期输出:** 容器重新启动

### Step 9.3: 测试查看容器内网络

```bash
# 测试前端容器能否访问后端
docker exec radar-frontend ping -c 2 radar-backend
```

**预期输出:**
```
PING radar-backend (172.x.x.x): 56 data bytes
64 bytes from 172.x.x.x: seq=0 ttl=64 time=0.xxx ms
64 bytes from 172.x.x.x: seq=1 ttl=64 time=0.xxx ms
```

### Step 9.4: 测试后端数据库连接

```bash
# 测试后端容器能否访问宿主机数据库
docker exec radar-backend ping -c 2 host.docker.internal
```

**预期输出:** 能 ping 通宿主机

---

## Task 10: 更新项目文档

### Step 10.1: 更新 task_plan.md

**修改:** `task_plan.md` 中 Phase 8 的状态

将 Phase 8 状态从 `planning` 改为 `complete`，并添加实施记录：

```markdown
### Phase 8: Docker 部署准备（已完成）
<!-- ... -->

**完成的任务：**
- [x] 编写 Docker Desktop 安装指南（Windows/Mac）
- [x] 编写外部数据库准备指南
- [x] 设计部署方案（后端 + 前端，数据库外部部署）
- [x] 编写完整的配置文件模板
- [x] 创建 Docker 配置文件（Dockerfile、docker-compose.yml、nginx.conf）
- [x] 本地 Docker Desktop 部署测试通过
- [x] 编写云服务器迁移指南
- [x] 添加常见问题解答
- [x] 添加生产环境优化建议

**实施日期：** 2026-03-02

**实施记录：**
- 本地 Mac Docker Desktop 测试通过
- 后端使用 Maven 3.9.12 + Eclipse Temurin JRE 21
- 前端使用 Node 24-alpine + Nginx Alpine
- 数据库使用外部部署（host.docker.internal）

**Status:** complete
```

### Step 10.2: 更新 DOCS_INDEX.md

添加新的文档索引：

```markdown
## 部署相关

- [Docker 容器化部署设计](docs/plans/2026-03-02-docker-deployment-design.md) - 部署架构设计文档
- [Docker 容器化部署实施计划](docs/plans/2026-03-02-docker-deployment-implementation.md) - 详细实施步骤
- [Docker 部署指南](docs/Docker部署指南.md) - 用户部署手册
- [云服务器部署](deploy/README.md) - 云服务器部署说明
```

### Step 10.3: 更新 progress.md

记录 Docker 部署实施进度：

```markdown
## 2026-03-02 - Docker 容器化部署实施

### 完成的工作

**配置文件创建：**
- docker-compose.yml - Docker Compose 编排文件
- radar-chart-builder-server/Dockerfile - 后端镜像构建
- radar-chart-builder-web/Dockerfile - 前端镜像构建
- radar-chart-builder-web/nginx.conf - Nginx 配置
- .env.example - 环境变量模板
- deploy/cloud.env.example - 云服务器配置模板

**技术选型：**
- 后端构建: maven:3.9.12-eclipse-temurin-21
- 后端运行: eclipse-temurin:21-jre
- 前端构建: node:24-alpine
- 前端运行: nginx:alpine

**测试结果：**
- 本地 Mac Docker Desktop 测试通过
- 前后端容器通信正常
- 数据库连接正常
- 功能验证通过（注册、登录、创建雷达图）
```

### Step 10.4: 提交文档更新

```bash
git add task_plan.md DOCS_INDEX.md progress.md
git commit -m "docs: update Phase 8 status to complete"
```

---

## Task 11: 最终验证和清理

### Step 11.1: 停止所有容器

```bash
docker-compose down
```

### Step 11.2: 清理未使用的镜像（可选）

```bash
docker image prune -f
```

### Step 11.3: 重新启动验证完整流程

```bash
docker-compose up -d
```

### Step 11.4: 等待服务启动

```bash
sleep 10
```

### Step 11.5: 执行完整功能测试

```bash
# 后端健康检查
curl http://localhost:3000/api/actuator/health

# 预期: {"status":"UP"}

# 在浏览器中手动测试完整流程
# 1. 打开 http://localhost:3000
# 2. 注册新用户
# 3. 登录
# 4. 创建雷达图
# 5. 刷新页面验证数据持久化
```

### Step 11.6: 检查是否有错误日志

```bash
docker-compose logs | grep -i error
docker-compose logs | grep -i exception
```

**预期输出:** 无错误日志

### Step 11.7: 最终提交

```bash
git status
git add .
git commit -m "feat: complete Docker containerization deployment"
```

---

## 验证清单

完成所有任务后，确认以下检查项：

- [ ] 所有 Docker 配置文件已创建
- [ ] docker-compose.yml 可以正常启动服务
- [ ] 前端页面可以正常访问 (http://localhost:3000)
- [ ] 后端 API 健康检查通过
- [ ] 用户注册功能正常
- [ ] 用户登录功能正常
- [ ] 创建雷达图功能正常
- [ ] 数据持久化验证通过
- [ ] 容器间网络通信正常
- [ ] 数据库连接正常
- [ ] 云服务器部署文档已创建
- [ ] 项目文档已更新
- [ ] 所有改动已提交到 Git

---

## 故障排查

### 问题：容器启动失败

```bash
# 查看详细日志
docker-compose logs radar-backend
docker-compose logs radar-frontend

# 重新构建
docker-compose build --no-cache
docker-compose up -d
```

### 问题：无法连接数据库

```bash
# 检查数据库是否运行
mysql -u root -p -e "SELECT 1"

# 检查容器能否访问宿主机
docker exec radar-backend ping host.docker.internal

# 检查 .env 配置
cat .env
```

### 问题：前端无法访问后端

```bash
# 检查容器网络
docker network inspect radar-chart-builder_radar-net

# 检查容器间通信
docker exec radar-frontend ping radar-backend
```

---

**实施计划版本:** 1.0
**创建日期:** 2026-03-02
**预计耗时:** 2-3 小时
