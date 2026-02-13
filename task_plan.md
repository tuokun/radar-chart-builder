# Task Plan: 雷达图生成器项目开发

<!--
  WHAT: 雷达图生成器项目开发路线图
  WHY: 记录项目整体规划、技术选型、开发进度
  WHEN: 创建于项目启动时，随开发进度更新
-->

## Goal
构建一个基于SpringBoot 3.5 + Java 21的后端服务和Vue3 + ECharts的前端应用，实现雷达图生成功能，用户可自定义维度并生成可视化图表。

## Current Phase
Phase 1: 后端基础架构搭建（进行中）

## Phases

### Phase 1: 后端基础架构搭建（进行中）
<!-- WHAT: 搭建SpringBoot项目基础结构，实现用户认证功能 -->
<!-- WHY: 后端是项目核心，需要先完成基础架构 -->

**详细任务分解（参考 docs/plans/2025-02-12-backend-initial-setup-and-auth.md）：**

- [x] Task 1: 创建SpringBoot项目基础结构
  - [x] 创建pom.xml（包含SpringBoot 3.5、JPA、Security、JWT、MariaDB依赖）
  - [x] 创建RadarChartApplication.java主类
  - [x] 创建application.yml配置文件
  - [x] 创建.gitignore
  - [x] 验证项目能正常构建
  - [x] 提交初始代码
- [x] Task 2: 创建User实体和Repository
- [x] Task 3: 创建DTO类
- [x] Task 4: 实现JWT工具类
- [x] Task 5: 实现Spring Security配置
- [x] Task 6: 实现用户认证Service
- [x] Task 7: 实现认证Controller
- [x] Task 8: JPA迁移到MyBatis-Plus（新增）
   - [x] 修改pom.xml，替换依赖
   - [x] 修改User实体（JPA注解 → MyBatis-Plus注解）
   - [x] 创建UserMapper（替换UserRepository）
   - [x] 修改application.yml配置
   - [x] 创建数据库建表SQL脚本
   - [x] 更新AuthServiceImpl使用MyBatis-Plus
   - [ ] Task 9: 测试迁移是否成功
   - **Status:** in_progress

### Phase 2: 雷达图核心功能开发
<!-- WHAT: 实现雷达图相关的实体、服务和API -->
<!-- WHY: 这是项目核心功能 -->
- [ ] 创建雷达图实体（RadarChart）
- [ ] 创建维度配置实体（DimensionConfig）
- [ ] 创建Repository
- [ ] 实现雷达图CRUD Service
- [ ] 创建雷达图Controller
- [ ] 实现图表数据生成接口
- [ ] 测试雷达图功能
- **Status:** pending

### Phase 3: 模板功能开发（待定）
<!-- WHAT: 实现模板分享和社区功能 -->
<!-- WHY: 这是项目的社交化特性，后续迭代 -->
- [ ] 创建模板实体
- [ ] 实现模板创建/分享功能
- [ ] 实现模板浏览/搜索功能
- [ ] 实现模板使用功能
- **Status:** pending

### Phase 4: 前端开发
<!-- WHAT: 使用Vue3 + Element Plus + ECharts开发前端 -->
<!-- WHY: 用户需要Web界面来使用功能 -->
- [ ] 初始化Vue3项目
- [ ] 配置Element Plus
- [ ] 配置ECharts
- [ ] 实现用户注册/登录页面
- [ ] 实现雷达图创建页面
- [ ] 实现雷达图列表页面
- [ ] 集成后端API
- [ ] 测试前端功能
- **Status:** pending

### Phase 5: 微信小程序开发（待定）
<!-- WHAT: 开发微信小程序版本 -->
<!-- WHY: 扩展平台覆盖，使用uni-app跨端开发 -->
- [ ] 使用uni-app初始化项目
- [ ] 复用Vue3组件逻辑
- [ ] 适配小程序API
- [ ] 测试小程序功能
- **Status:** pending

## Key Questions
<!-- WHAT: 项目开发过程中需要回答的关键问题 -->
<!-- WHY: 指导技术选型和架构决策 -->
1. 后端API文档是否需要在线测试功能？（是 - 使用Knife4j）
2. 雷达图数据如何存储？（JSON格式存储在数据库）
3. 前端是否需要状态管理？（Pinia，如果需要复杂状态）
4. 是否需要部署CI/CD？（后续考虑）

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
| ECharts | 原生支持雷达图，功能强大，中文文档丰富 |
| Knife4j | Swagger增强版，界面美观，支持在线测试 |

## Errors Encountered
<!-- WHAT: 开发过程中遇到的错误及解决方案 -->
<!-- WHY: 避免重复错误，积累经验 -->
| Error | Attempt | Resolution |
|-------|---------|------------|
| | 1 | |

## Notes
<!-- REMINDERS: -->
<!-- - Update phase status as you progress: pending → in_progress → complete -->
<!-- - Re-read this plan before major decisions (attention manipulation) -->
<!-- - Log ALL errors - they help avoid repetition -->
- 后端基础架构已完成Task 1-7（JPA版本）
- 决定将JPA迁移到MyBatis-Plus，已创建详细迁移计划
- 迁移计划文档：docs/plans/2026-02-13-migrate-jpa-to-mybatis-plus.md
- 前端开发需要等后端API基本完成后再开始
- 模板功能是待定功能，MVP阶段暂不实现
