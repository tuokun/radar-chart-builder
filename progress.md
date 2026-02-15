# Progress Log

<!--
  WHAT: 雷达图生成器项目开发进度记录
  WHY: 记录开发过程中的进展、测试结果、错误日志
  WHEN: 每个阶段完成或遇到错误时更新
-->

## Session: 2026-02-13

### Phase 1: 后端基础架构搭建
<!-- WHAT: 创建SpringBoot项目，实现用户认证功能 -->
<!-- WHY: 后端是项目核心，需要先完成基础架构 -->
- **Status:** in_progress
- **Started:** 2026-02-12 21:00
- **Updated:** 2026-02-13 08:30

<!-- Actions taken: -->
- 创建项目整体规划文档（项目规划文档.md）
- 创建产品需求文档（产品需求文档.md）
- 使用writing-plans技能创建详细实现计划
- 创建task_plan.md项目执行计划
- 创建findings.md技术选型记录
- **2026-02-12 22:45-23:00 后端开发进展**：
  - 初始化Git仓库
  - 创建radar-chart-builder-server项目目录结构
  - 创建pom.xml，配置SpringBoot 3.5及相关依赖
  - 创建RadarChartApplication.java主应用类
  - 创建application.yml配置文件（数据库连接、JWT配置）
  - 创建.gitignore文件
- **2026-02-13 08:13-08:30 后端开发完整实现**：
  - 解决Maven依赖问题（MariaDB版本调整为3.4.1）
  - 成功构建项目
  - 完成Task 1-7的所有代码实现
  - 每个Task完成后都进行了Git提交
- **2026-02-13 09:15 技术选型调整决策**：
  - 分析JPA vs MyBatis-Plus的优劣
  - 决定将JPA迁移到MyBatis-Plus
  - 更新task_plan.md，添加迁移任务（Task 8）
  - 更新findings.md，记录决策过程和对比分析
  - 创建迁移计划文档准备提交
- **2026-02-13 09:45 创建详细迁移计划**：
  - 创建docs/plans/2026-02-13-migrate-jpa-to-mybatis-plus.md
  - 制定9个详细迁移任务（修改pom.xml、实体、Mapper、配置、建表SQL等）
  - 更新task_plan.md，记录技术栈变更
  - 准备交给后端会话执行迁移
- **2026-02-15 22:30 清理Eclipse文件并配置IDEA专用设置**：
  - 删除所有Eclipse相关文件（bin/、.project、.classpath、.settings/）
  - 更新.gitignore，明确添加Eclipse文件忽略规则
  - 更新pom.xml，添加maven-clean-plugin自动清理Eclipse文件
  - 创建IDEA_SETUP.md配置说明文档
  - 验证所有Eclipse文件已删除
- **2026-02-15 22:35 彻底清理所有Eclipse文件（包括.factorypath）**：
  - 删除.factorypath文件（Eclipse注解处理器配置）
  - 更新.gitignore，添加.factorypath忽略规则
  - 更新pom.xml，在maven-clean-plugin中添加.factorypath清理
  - 更新IDEA_SETUP.md，补充.factorypath说明
  - 最终验证：所有Eclipse文件（.project、.classpath、.factorypath、.settings/、bin/）已完全删除

<!-- Files created/modified: -->
- `项目规划文档.md` (created)
- `产品需求文档.md` (created)
- `docs/plans/2025-02-12-backend-initial-setup-and-auth.md` (created)
- `task_plan.md` (created)
- `findings.md` (created)
- `progress.md` (created)
- `radar-chart-builder-server/pom.xml` (created & modified)
- `radar-chart-builder-server/src/main/java/com/radarchart/RadarChartApplication.java` (created)
- `radar-chart-builder-server/src/main/resources/application.yml` (created)
- `radar-chart-builder-server/.gitignore` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/entity/User.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/repository/UserRepository.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/dto/` (4 files created)
- `radar-chart-builder-server/src/main/java/com/radarchart/config/JwtConfig.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/util/JwtUtil.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/config/SecurityConfig.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/config/JwtAuthenticationFilter.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/exception/` (3 files created)
- `radar-chart-builder-server/src/main/java/com/radarchart/service/AuthService.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/service/impl/AuthServiceImpl.java` (created)
- `radar-chart-builder-server/src/main/java/com/radarchart/controller/AuthController.java` (created)

### Phase 2: 雷达图核心功能开发
- **Status:** pending
- Actions taken:
- Files created/modified:

### Phase 3: 模板功能开发
- **Status:** pending
- Actions taken:
- Files created/modified:

### Phase 4: 前端开发
- **Status:** pending
- Actions taken:
- Files created/modified:

### Phase 5: 微信小程序开发（待定）
- **Status:** pending
- Actions taken:
- Files created/modified:

## Test Results
<!-- WHAT: 测试记录 -->
<!-- WHY: 验证功能正确性 -->

| Test | Input | Expected | Actual | Status |
|------|-------|----------|--------|--------|
| 后端API测试 | 迁移到MyBatis-Plus后 | API端点正常工作 | 待迁移后测试 | ⏳ |

## Error Log
<!-- WHAT: 错误日志 -->
<!-- WHY: 记录错误及解决方法 -->

| Timestamp | Error | Attempt | Resolution |
|-----------|-------|---------|------------|
| 2026-02-13 08:05 | MariaDB 11.4.7下载失败（artifactory不可用） | 1 | 改用MariaDB 3.4.1版本 |
| 2026-02-13 08:05 | Maven本地仓库路径错误（/User vs /Users） | 1 | 使用-Dmaven.repo.local指定正确路径 |
| 2026-02-13 09:00 | LSP报错：MyBatis-Plus注解和Mapper导入无法解析 | 1 | 这是IDE的LSP错误，代码正确 |

## 5-Question Reboot Check
<!-- WHAT: 五问重启测试 -->
<!-- WHY: 验证上下文是否完整 -->

| Question | Answer |
|----------|--------|
| Where am I? | Phase 1 - 后端基础架构搭建（JPA迁移到MyBatis-Plus前） |
| Where am I going? | 执行JPA到MyBatis-Plus的迁移，然后继续Phase 2雷达图核心功能 |
| What's the goal? | 构建雷达图生成器，后端SpringBoot + MyBatis-Plus，前端Vue3 + ECharts |
| What have I learned? | 技术选型已确定：MyBatis-Plus、Vue3、Element Plus、ECharts（见findings.md） |
| What have I done? | 创建项目文档、实现计划、技术选型、迁移计划（见progress.md） |

---
<!--
  REMINDER:
  - Update after completing each phase or encountering errors
  - Be detailed - this is your "what happened" log
  - Include timestamps for errors to track when issues occurred
-->
*明天继续时，先检查后端独立会话的执行进度*

## Session: 2026-02-15 22:45

### JWT认证优化计划
<!-- WHAT: 规划JWT认证优化方案 -->
<!-- WHY: 为未来的性能和安全优化做准备 -->
- **Status:** pending
- **Actions taken:**
  - 分析当前JWT机制的局限性
  - 研究JWT + Redis方案
  - 研究Refresh Token方案（数据库存储）
  - 制定优化触发条件和方案选择建议
  - 添加Phase 6到task_plan.md（JWT认证优化待定）
  - 在findings.md中记录详细的方案对比
  - 定义实施优先级（Phase 1、2、3）
- **Files created/modified:**
  - `task_plan.md` (modified - 添加Phase 6)
  - `findings.md` (modified - 添加JWT优化方案研究)
