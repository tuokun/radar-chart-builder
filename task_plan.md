# Task Plan: 雷达图生成器项目开发

<!--
  WHAT: 雷达图生成器项目开发路线图
  WHY: 记录项目整体规划、技术选型、开发进度
  WHEN: 创建于项目启动时，随开发进度更新
-->

## Goal
构建一个基于SpringBoot 3.5 + Java 21的后端服务和Vue3 + ECharts的前端应用，实现雷达图生成功能，用户可自定义维度并生成可视化图表。

## Current Phase
Phase 8: Docker 部署准备（文档已完成，待实施）

## Phases

### Phase 1: 后端基础架构搭建（已完成）
<!-- WHAT: 搭建SpringBoot项目基础结构，实现用户认证功能 -->
<!-- WHY: 后端是项目核心，需要先完成基础架构 -->

**详细任务分解（参考 docs/plans/2025-02-12-后端初始化与认证.md）：**

- [x] Task 1: 创建SpringBoot项目基础结构
  - [x] 创建pom.xml（包含SpringBoot 3.5、MyBatis-Plus、Security、JWT、MariaDB依赖）
  - [x] 创建RadarChartApplication.java主类
  - [x] 创建application.yml配置文件
  - [x] 创建.gitignore
  - [x] 验证项目能正常构建
  - [x] 提交初始代码
- [x] Task 2: 创建User实体和Mapper
- [x] Task 3: 创建DTO类
- [x] Task 4: 实现JWT工具类
- [x] Task 5: 实现Spring Security配置
- [x] Task 6: 实现用户认证Service
- [x] Task 7: 实现认证Controller
- [x] Task 8: JPA迁移到MyBatis-Plus（已完成）
   - [x] 修改pom.xml，替换依赖
   - [x] 修改User实体（JPA注解 → MyBatis-Plus注解）
   - [x] 创建UserMapper（替换UserRepository）
   - [x] 修改application.yml配置
   - [x] 创建数据库建表SQL脚本
   - [x] 更新AuthServiceImpl使用MyBatis-Plus
- [x] Task 9: 测试迁移是否成功
- **Status:** complete

### Phase 2: 雷达图核心功能开发（已完成）
<!-- WHAT: 实现雷达图相关的实体、服务和API -->
<!-- WHY: 这是项目核心功能 -->
- [x] 创建雷达图实体（RadarChart、Dimension、DataSeries、SeriesData）
- [x] 创建Mapper接口
- [x] 创建Param和Result DTO
- [x] 创建Service接口和骨架实现
- [x] 实现雷达图CRUD Service方法
- [x] 创建雷达图Controller
- [x] 执行数据库迁移
- [x] 测试雷达图功能（所有接口测试通过）
- [x] 修复时间字段自动填充问题（MyMetaObjectHandler字段名统一为createTime/updateTime）
- **实施计划文档：** docs/plans/2026-02-16-雷达图核心功能实施计划.md
- **Status:** complete

### Phase 3: 模板功能开发（待定）
<!-- WHAT: 实现模板分享和社区功能 -->
<!-- WHY: 这是项目的社交化特性，后续迭代 -->
- [ ] 创建模板实体
- [ ] 实现模板创建/分享功能
- [ ] 实现模板浏览/搜索功能
- [ ] 实现模板使用功能
- **Status:** pending

### Phase 4: 前端开发（已完成）
<!-- WHAT: 使用Vue3 + Element Plus + ECharts开发前端 -->
<!-- WHY: 用户需要Web界面来使用功能 -->
- [x] 初始化Vue3项目
- [x] 配置Element Plus
- [x] 配置ECharts
- [x] 实现用户注册/登录页面
- [x] 实现雷达图创建页面
- [x] 实现雷达图列表页面
- [x] 集成后端API
- [x] 测试前端功能
- [x] 修复API对接问题（端口、响应格式、字段名）
- **Status:** complete

### Phase 7: 前端联调测试与优化（已完成）
<!-- WHAT: 前后端联调测试，发现问题并优化用户体验 -->
<!-- WHY: 确保功能正常，优化用户体验 -->
**测试时间：** 2026-02-25

**完成的优化项目：**

**页面布局优化：**
- [x] 移除页面宽度限制（main.css）
- [x] 适配移动端和桌面端布局

**用户认证优化：**
- [x] 登录接口字段适配（username → account）
- [x] 登录表单添加长度验证
- [x] 注册成功后跳转到登录页

**数据模型优化：**
- [x] 数据库字段统一（create_time/update_time）
- [x] 前后端字段名统一（minValue、orderIndex）
- [x] ID精度问题修复（Long → String）
- [x] 添加 minValue 支持

**页面结构优化：**
- [x] 分离雷达图表单和数据组编辑为独立页面
- [x] 创建 MainLayout 顶部导航栏
- [x] 删除"数据系列"侧边栏菜单
- [x] 添加维度编辑器
- [x] 添加数据组编辑器

**交互优化：**
- [x] 删除操作添加二次确认
- [x] Vue Router 切换错误修复
- [x] 实时预览功能
- [x] 维度输入框对齐问题修复

**性能优化：**
- [x] 列表页只返回基本信息，不返回系列数据
- [x] 无系列时显示简易预览（各维度平均值）
- [x] 删除图表内标题（避免遮挡数据）

**数据系列功能优化：**
- [x] 创建雷达图不自动生成数据组
- [x] 允许删除所有数据组
- [x] 保存数据组颜色信息
- [x] 新增数据组在最前面显示

**UI/UX优化：**
- [x] 按钮文字优化（"添加数据"、"编辑雷达"、"数据组"）
- [x] 列表页搜索框改为点击触发
- [x] 搜索框样式优化（删除内图标、缩短宽度）
- [x] 三点菜单图标大小优化
- [x] 数据组名称框高度和字体优化
- [x] 颜色选择器按钮中文化（重置/保存）

**页面流程优化：**
- [x] 点击卡片 → 预览页面（只读）
- [x] 点击"编辑数据"按钮 → 编辑页面（可编辑）
- [x] 预览页面 Grid 布局（一行两组数据）

**技术优化：**
- [x] 后端添加 JacksonConfig（Long 序列化为字符串）
- [x] UpdateRadarChartParam 添加 series 字段
- [x] DataSeries 实体添加 color 字段
- [x] 路由顺序优化（具体路由在前）

**Files created/modified:**
- `radar-chart-builder-server/src/main/java/com/radarchart/config/JacksonConfig.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/entity/DataSeries.java` (modified - 添加color字段)
- `radar-chart-builder-server/src/main/java/com/radarchart/dto/param/UpdateRadarChartParam.java` (modified - 添加series字段)
- `radar-chart-builder-web/src/components/MainLayout.vue` (created - 顶部导航)
- `radar-chart-builder-web/src/views/radar/RadarFormView.vue` (modified - 表单页面)
- `radar-chart-builder-web/src/views/series/SeriesDetailView.vue` (created - 数据组编辑页)
- `radar-chart-builder-web/src/views/series/SeriesPreviewView.vue` (created - 预览页面)
- `radar-chart-builder-web/src/views/radar/RadarListView.vue` (modified - 搜索优化)
- `radar-chart-builder-web/src/assets/main.css` (modified - 颜色选择器按钮中文化)
- **Status:** complete

### Phase 8: Docker 部署准备（设计完成，待实施）
<!-- WHAT: 编写 Docker 容器化部署文档 -->
<!-- WHY: 为生产部署做准备，简化部署流程，确保环境一致性 -->
**文档时间：** 2026-02-25
**设计时间：** 2026-03-02

**完成的任务：**
- [x] 编写 Docker Desktop 安装指南（Windows/Mac）
- [x] 编写外部数据库准备指南
- [x] 设计部署方案（后端 + 前端，数据库外部部署）
- [x] 编写完整的配置文件模板：
  - [x] 后端 Dockerfile（多阶段构建）
  - [x] 前端 Dockerfile（多阶段构建）
  - [x] Nginx 配置文件
  - [x] docker-compose.yml（容器编排）
  - [x] 环境变量模板（.env.example）
- [x] 编写详细的部署步骤
- [x] 添加常见问题解答（数据库连接、Windows 特有配置等）
- [x] 添加生产环境优化建议
- [x] 编写设计文档（2026-03-02）
- [x] 编写详细实施计划（2026-03-02）

**技术方案：**
- **容器化方式：** Docker Compose（Docker Desktop 原生支持）
- **数据库部署：** 外部部署（不使用 Docker）
- **容器数量：** 2 个（后端 + 前端）
- **网络：** bridge 网络容器间通信
- **数据持久化：** 外部数据库独立管理
- **后端构建：** Maven 3.9.12 + Eclipse Temurin JRE 21
- **前端构建：** Node 24-alpine + Nginx Alpine

**文档位置：**
- `docs/Docker部署指南.md` - 用户部署手册
- `docs/plans/2026-03-02-docker-deployment-design.md` - 设计文档
- `docs/plans/2026-03-02-docker-deployment-implementation.md` - 实施计划

**特性：**
- 面向 Docker Desktop 初学者（包含安装指南）
- 支持 Windows 和 Mac
- 数据库外部部署（灵活管理）
- 完整的配置文件模板
- 详细的故障排除指南
- 云服务器迁移方案

**本地测试配置：**
- 数据库：host.docker.internal（本地 MariaDB）
- 用户名：root
- 密码：cgfhsc
- 数据库：radar_chart_builder
- 前端端口：3000

**Status:** planned（设计完成，待实施）

### Phase 5: 微信小程序开发（待定）
<!-- WHAT: 开发微信小程序版本 -->
<!-- WHY: 扩展平台覆盖，使用uni-app跨端开发 -->
- [ ] 使用uni-app初始化项目
- [ ] 复用Vue3组件逻辑
- [ ] 适配小程序API
- [ ] 测试小程序功能
- **Status:** pending

### Phase 6: JWT认证优化（待定）
<!-- WHAT: 优化JWT认证机制，提升性能和安全性 -->
<!-- WHY: 当用户规模达到一定水平时，现有JWT机制需要优化 -->
<!-- 触发条件：日活用户 > 1000 或 API QPS > 100 -->
- **优化目标：**
  - 减少数据库查询压力（用户信息验证）
  - 提升token安全性（防止token泄露）
  - 实现token刷新机制（避免频繁重新登录）
  - 支持token主动撤销（退出登录、修改密码）

- **技术方案选择（二选一）：**
  - **方案A：JWT + Redis存储**
    - Access Token：有效期短（如2小时）
    - Refresh Token：有效期长（如7天），存储在Redis
    - 用户信息缓存在Redis，减少数据库查询
    - 优点：性能最优，支持token撤销
    - 缺点：需要维护Redis，增加系统复杂度
  - **方案B：Refresh Token机制（数据库存储）**
    - Access Token：有效期短（如2小时）
    - Refresh Token：存储在数据库，关联用户ID
    - 定期清理过期的Refresh Token
    - 优点：不需要额外的Redis，实现相对简单
    - 缺点：性能不如Redis方案，数据库压力较大

- **实施步骤（待触发后执行）：**
  - [ ] 添加Redis依赖和配置
  - [ ] 创建RefreshToken实体和Mapper（如选择方案B）
  - [ ] 修改JwtUtil，支持Access Token和Refresh Token生成
  - [ ] 创建Redis工具类（如选择方案A）
  - [ ] 修改AuthServiceImpl，实现token刷新逻辑
  - [ ] 修改SecurityConfig，支持双token验证
  - [ ] 创建/api/auth/refresh接口
  - [ ] 实现退出登录时的token撤销
  - [ ] 添加定时任务清理过期token
  - [ ] 更新API文档
  - [ ] 测试token刷新流程
- **Status:** pending

## Key Questions
<!-- WHAT: 项目开发过程中需要回答的关键问题 -->
<!-- WHY: 指导技术选型和架构决策 -->
1. ~~后端API文档是否需要在线测试功能？~~ ✅ 已解决（使用Knife4j）
2. ~~雷达图数据如何存储？~~ ✅ 已解决（四表关联结构）
3. ~~前端是否需要状态管理？~~ ✅ 已解决（使用Pinia）
4. 是否需要部署CI/CD？（后续考虑）
5. ~~雷达图是否支持多数据系列？~~ ✅ 已解决（支持多系列对比）

## Decisions Made
<!-- WHAT: 技术和架构决策及其理由 -->
<!-- WHY: 记录决策过程，便于后续回顾和调整 -->
| Decision | Rationale |
|----------|-----------|
| SpringBoot 3.5 + Java 21 | 最新的稳定版本，长期支持，性能优秀 |
| MariaDB 11.4.7 | 开源、高性能、与MySQL兼容 |
| MyBatis-Plus | SQL可控性强，适合复杂查询，中文资料丰富，性能优化方便 |
| JWT认证 | 无状态，适合前后端分离架构 |
| Vue3 + Element Plus | 学习曲线平缓，文档完善，国内生态好 |
| Pinia | Vue3 官方推荐状态管理，TypeScript 支持好，API 简洁 |
| ECharts | 原生支持雷达图，功能强大，中文文档丰富 |
| Knife4j | Swagger增强版，界面美观，支持在线测试 |
| 四表关联结构 | 支持多数据系列，数据规范化，便于扩展和查询 |
| Param/Result命名 | 语义清晰，区分请求和响应 |
| 雪花ID（ASSIGN_ID） | 分布式友好，避免ID冲突 |
| 不使用Lombok | 项目禁用Lombok，手动编写getter/setter/constructor |
| API响应格式 | 后端直接返回数据，无包装（code/message/data） |

## Errors Encountered
<!-- WHAT: 开发过程中遇到的错误及解决方案 -->
<!-- WHY: 避免重复错误，积累经验 -->
| Error | Attempt | Resolution |
|-------|---------|------------|
| 前端 API 端口配置错误 (8080) | 1 | 修改为 8081 |
| 前端 API 响应格式不匹配 | 1 | 移除 ApiResponse 包装 |
| 前端字段名不一致 | 1 | createdAt/updatedAt → createTime/updateTime |
| 前端分页格式不匹配 | 1 | 移除分页，直接处理数组 |
| 前端注册跳转错误 | 1 | 注册成功后跳转到登录页 |
| 前端删除缺少确认 | 1 | 添加二次确认对话框 |
| 前端验证不足 | 1 | 添加用户名和密码长度验证 |
| 前端图标未导入 | 1 | 导入 Plus 和 Delete 图标 |
| 前端重复 initAuth | 1 | 移除 App.vue 中的调用 |
| 后端字段命名不统一 | 1 | users表字段改为 create_time/update_time，统一实体和DTO |
| 测试接口403错误 | 1 | SecurityConfig 添加 /api/health 和 /api/test/** 白名单 |
| TestController 过度简化 | 1 | 回滚 d→duration，>>20 → /1024/1024，移除 measureLatency |

## Notes
<!-- REMINDERS: -->
<!-- - Update phase status as you progress: pending → in_progress → complete -->
<!-- - Re-read this plan before major decisions (attention manipulation) -->
<!-- - Log ALL errors - they help avoid repetition -->
- Phase 1 已完成：用户认证功能 + MyBatis-Plus 迁移
- Phase 2 已完成：雷达图核心功能开发
- Phase 4 已完成：前端开发（Vue3 + Element Plus + ECharts）
- 设计文档：docs/plans/2026-02-16-雷达图核心功能设计.md
- 实施计划：docs/plans/2026-02-16-雷达图核心功能实施计划.md
- API测试文档：docs/接口文档/雷达图API测试指南.md
- 模板功能是待定功能，MVP阶段暂不实现

## 关键决策记录
- **数据系列**：支持多数据系列（可对比多组数据）
- **数据存储**：采用四表关联结构（RadarChart → Dimension, DataSeries → SeriesData）
- **维度范围**：支持自定义最小值/最大值（非0起点）
- **DTO命名**：使用 Param/Result 后缀
- **字段命名**：使用 createTime/updateTime（非 createdAt/updatedAt）
- **前端状态管理**：使用 Pinia（Vue3 官方推荐）
- **API响应格式**：后端直接返回数据，无包装（简化前端处理）
- **前端分页**：暂不支持分页（后端返回数组，前端全部显示）
