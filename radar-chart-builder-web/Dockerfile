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
