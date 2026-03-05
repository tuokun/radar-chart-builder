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
