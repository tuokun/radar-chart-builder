# Mac Docker Desktop - 后端镜像构建指南

<!--
  WHAT: 在 Mac Docker Desktop 上构建后端 Docker 镜像的完整操作指南
  WHEN: 首次构建或代码更新后重新构建
-->

## 前置检查

### 1. 检查 Docker Desktop 是否运行

```bash
docker --version
```

**预期输出：**
```
Docker version 24.0.0 或更高
```

**如果报错：** 请先启动 Docker Desktop 应用

---

### 2. 检查当前工作目录

```bash
pwd
```

**预期：** 你应该在项目根目录 `/Users/cgfhsc/dev/project/radar-chart-builder`

**如果不是：**
```bash
cd /Users/cgfhsc/dev/project/radar-chart-builder
```

---

### 3. 检查必要文件是否存在

```bash
ls -la radar-chart-builder-server/Dockerfile
ls -la radar-chart-builder-server/pom.xml
ls -la .env
```

**预期：** 所有文件都应该存在

---

## 构建步骤

### Step 1: 清理旧镜像（可选，如果之前构建过）

```bash
# 查看现有镜像
docker images | grep radar-backend

# 删除旧镜像
docker rmi radar-backend:latest
```

---

### Step 2: 构建后端镜像

```bash
docker-compose build radar-backend
```

**构建过程说明：**
- 第一步：下载 `maven:3.9.12-eclipse-temurin-21` 基础镜像
- 第二步：下载 Maven 依赖（首次构建会较慢）
- 第三步：编译打包 Java 项目
- 第四步：基于 `eclipse-temurin:21-jre` 创建运行镜像

**预期构建时间：**
- 首次构建：5-10 分钟（取决于网络速度）
- 重复构建：2-3 分钟（利用 Docker 缓存）

---

### Step 3: 验证镜像构建成功

```bash
docker images | grep radar-backend
```

**预期输出：**
```
radar-backend   latest   xxx   x seconds ago   xxx MB
```

---

### Step 4: 查看镜像详细信息

```bash
docker inspect radar-backend:latest
```

**检查要点：**
- 镜像大小应该在 200-400MB 之间
- 架构应该是 `arm64`（Apple Silicon）或 `amd64`（Intel Mac）

---

## 测试运行

### Step 5: 测试运行容器

```bash
# 运行容器（测试模式，启动后会自动退出）
docker --rm radar-backend:latest java -jar /app/app.jar --spring.profiles.active=prod --spring.datasource.url="jdbc:mariadb://host.docker.internal:3306/radar_chart_builder" --spring.datasource.username=root --spring.datasource.password=cgfhsc
```

**注意：** 这会尝试连接数据库，如果数据库未运行会报错（正常）

---

## 常见问题排查

### 问题 1: Docker Desktop 未运行

**错误信息：**
```
Cannot connect to the Docker daemon
```

**解决：** 启动 Docker Desktop 应用，等待状态栏图标变为绿色

---

### 问题 2: 端口被占用

**错误信息：**
```
port is already allocated
```

**解决：**
```bash
# 查看占用 8081 端口的容器
docker ps | grep 8081

# 停止占用端口的容器
docker stop <容器名或ID>
```

---

### 问题 3: Maven 依赖下载失败

**错误信息：**
```
Failed to download xxx from central
```

**解决：** 检查网络连接，或稍后重试（Maven 中央服务器可能不稳定）

---

### 问题 4: 内存不足

**错误信息：**
```
Java heap space / OutOfMemoryError
```

**解决：** 在 Docker Desktop 设置中增加内存分配
- Docker Desktop → Settings → Resources → Memory（建议 4GB+）

---

## 注意事项

### 1. 首次构建时间较长

首次构建需要：
- 下载基础镜像（~500MB）
- 下载 Maven 依赖（~200MB）
- 编译 Java 项目

**建议：** 在网络稳定时进行首次构建

---

### 2. Docker 缓存机制

第二次构建会利用缓存：
- `pom.xml` 未变化 → 跳过依赖下载
- `src/` 未变化 → 跳过编译

**强制重新构建（不用缓存）：**
```bash
docker-compose build --no-cache radar-backend
```

---

### 3. Apple Silicon 特殊注意

如果你使用 M1/M2/M3 Mac：
- 镜像架构是 `arm64`
- 镜像可能比 Intel Mac 稍小（因为 JRE 更精简）

---

### 4. .env 文件配置

确保 `.env` 文件配置正确：
```bash
cat .env
```

应该包含：
```
DB_HOST=host.docker.internal
DB_PORT=3306
DB_NAME=radar_chart_builder
DB_USERNAME=root
DB_PASSWORD=cgfhsc
JWT_SECRET=xxx
```

---

## 下一步

后端镜像构建成功后，可以：

1. **构建前端镜像：** `docker-compose build radar-frontend`
2. **同时构建两个镜像：** `docker-compose build`
3. **启动所有服务：** `docker-compose up -d`

---

## 快速命令参考

```bash
# 构建后端镜像
docker-compose build radar-backend

# 查看镜像
docker images | grep radar

# 删除镜像
docker rmi radar-backend:latest

# 查看构建日志
docker-compose build radar-backend 2>&1 | tee build.log

# 强制重新构建
docker-compose build --no-cache radar-backend
```

---

**文档版本：** 1.0
**创建日期：** 2026-03-05
**适用系统：** macOS + Docker Desktop
