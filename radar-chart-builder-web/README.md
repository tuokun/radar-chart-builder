# 雷达图生成器 - 前端项目

一个基于 Vue 3 + TypeScript + Element Plus 的雷达图生成和可视化工具。

## 功能特性

- 用户认证（登录/注册）
- 雷达图列表管理
- 创建/编辑雷达图
- 实时图表预览（基于 ECharts）
- 维度和数据系列管理

## 技术栈

| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架 |
| TypeScript | 类型系统 |
| Vite | 构建工具 |
| Pinia | 状态管理 |
| Vue Router | 路由管理 |
| Element Plus | UI 组件库 |
| Axios | HTTP 客户端 |
| vue-echarts | 图表渲染 |

## 项目结构

```
src/
├── api/                    # API 请求封装
│   ├── index.ts           # 统一导出
│   ├── auth.ts            # 认证相关 API
│   └── radarChart.ts      # 雷达图相关 API
├── types/                  # TypeScript 类型定义
│   ├── api.ts             # API 响应类型
│   ├── models.ts          # 数据模型类型
│   └── auth.ts            # 认证类型
├── stores/                 # Pinia 状态管理
│   ├── auth.ts            # 认证状态
│   └── radarChart.ts      # 雷达图状态
├── router/                 # 路由配置
│   └── index.ts           # 路由定义
├── views/                  # 页面组件
│   ├── auth/
│   │   ├── LoginView.vue  # 登录页
│   │   └── RegisterView.vue # 注册页
│   ├── radar/
│   │   ├── RadarListView.vue    # 雷达图列表
│   │   └── RadarEditView.vue    # 创建/编辑页
│   └── HomeView.vue       # 首页
├── components/             # 通用组件
│   ├── RadarChartPreview.vue   # 雷达图预览
│   ├── DimensionEditor.vue     # 维度编辑器
│   └── SeriesEditor.vue        # 数据系列编辑器
├── composables/            # 组合式函数
│   ├── useAuth.ts         # 认证逻辑
│   └── useChart.ts        # 图表操作逻辑
├── utils/                  # 工具函数
│   └── request.ts         # axios 请求封装
└── styles/                 # 样式文件
    └── variables.css      # CSS 变量
```

## 开发指南

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

开发服务器默认运行在 `http://localhost:5173`

### 类型检查

```bash
npm run type-check
```

### 代码检查

```bash
npm run lint
```

### 构建生产版本

```bash
npm run build
```

## 环境配置

### 开发环境 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:8081/api
```

### 生产环境 (.env.production)

```env
VITE_API_BASE_URL=/api
```

## 后端 API 对接

前端通过 axios 与后端 API 通信，所有请求会自动携带 JWT Token。

### API 响应格式

后端直接返回数据，无包装：

```typescript
// 登录/注册响应
{ token: string, user: User }

// 雷达图列表响应
RadarChart[]  // 数组，无分页

// 单个雷达图响应
RadarChart
```

### 字段映射

| 前端字段 | 后端字段 |
|---------|---------|
| createTime | createTime |
| updateTime | updateTime |

## 认证流程

1. 用户登录/注册后，后端返回 JWT Token
2. Token 存储在 localStorage
3. 所有 API 请求自动携带 Authorization 头
4. 路由守卫保护需要认证的页面

## 浏览器支持

- Chrome (推荐)
- Edge
- Firefox
- Safari

## IDE 推荐

- [VS Code](https://code.visualstudio.com/)
- [Vue - Official (Volar)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

## 许可证

MIT
