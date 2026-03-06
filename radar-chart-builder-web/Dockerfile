# 构建阶段
FROM node:24-alpine AS builder

WORKDIR /radar-chart-builder-web

# 复制 package 文件并安装依赖
COPY package*.json ./
RUN npm ci

# 复制配置文件
COPY index.html vite.config.ts tsconfig*.json env.d.ts ./

# 复制源代码和公共资源
COPY src ./src
COPY public ./public

# 生产环境 API 地址（通过 Nginx 代理）
ENV VITE_API_BASE_URL=/api

# 构建项目
RUN npm run build-only

# 运行阶段
FROM nginx:alpine

WORKDIR /radar-chart-builder-web

# 设置时区为亚洲/上海
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 复制构建产物
COPY --from=builder /radar-chart-builder-web/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
