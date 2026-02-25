# 雷达图生成器项目文档索引

<!--
  WHAT: 项目文档与任务的映射关系索引
  WHY: 方便在完成特定任务后快速定位需要更新的文档
  WHEN: 完成任何开发任务后，参考此索引更新相关文档
-->

## 文档索引总览

| 文档路径 | 文档类型 | 说明 |
|---------|---------|------|
| `progress.md` | 进度记录 | 项目开发进度日志、测试结果、错误记录 |
| `task_plan.md` | 任务计划 | 项目整体规划、技术选型、阶段划分 |
| `产品需求文档.md` | 需求文档 | 产品功能需求说明 |
| `IDEA_SETUP.md` | 配置说明 | IntelliJ IDEA 配置指南 |
| `docs/plans/` | 计划文档 | 各阶段详细设计和实施计划 |
| `docs/接口文档/` | API文档 | 接口测试指南 |

---

## 任务完成后的文档更新清单

### Phase 1: 后端基础架构搭建

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `progress.md` | 添加 Phase 1 开始时间、完成时间、Actions taken、Files created/modified |
| P0 | `task_plan.md` | 更新 Current Phase、Phase 1 状态标记为 complete |
| P1 | `progress.md` | 添加相关测试结果到 Test Results |
| P1 | `progress.md` | 添加遇到的问题到 Error Log |
| P2 | `task_plan.md` | 更新 Decisions Made（如有新决策） |
| P2 | `task_plan.md` | 更新 Notes |

### Phase 2: 雷达图核心功能开发

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `progress.md` | 添加 Phase 2 开始时间、完成时间、Actions taken、Files created/modified |
| P0 | `docs/plans/2026-02-16-雷达图核心功能设计.md` | 标记设计完成状态 |
| P0 | `docs/plans/2026-02-16-雷达图核心功能实施计划.md` | 标记所有Task完成状态 |
| P1 | `task_plan.md` | 更新 Current Phase、Phase 2 状态标记为 complete |
| P1 | `docs/接口文档/雷达图API测试指南.md` | 添加测试结果 |
| P1 | `progress.md` | 添加相关测试结果到 Test Results |
| P1 | `progress.md` | 添加遇到的问题到 Error Log |

### Phase 4: 前端开发

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `radar-chart-builder-web/README.md` | 创建/更新项目说明文档 |
| P0 | `progress.md` | 添加 Phase 4 开始时间、完成时间、Actions taken、Files created/modified |
| P0 | `progress.md` | 添加前端测试结果（type-check、lint） |
| P0 | `progress.md` | 添加前端修复的问题到 Error Log |
| P1 | `task_plan.md` | 更新 Current Phase、Phase 4 状态标记为 complete |
| P1 | `task_plan.md` | 更新 Key Questions（前端相关） |
| P1 | `task_plan.md` | 更新 Decisions Made（前端技术栈） |
| P2 | `task_plan.md` | 更新 关键决策记录 |

### Phase 7: 前端联调测试与优化

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `progress.md` | 添加 Phase 7 开始时间、完成时间、Actions taken、Files created/modified |
| P0 | `progress.md` | 添加所有优化项目到 Actions taken |
| P0 | `progress.md` | 添加测试结果到 Test Results |
| P0 | `task_plan.md` | 更新 Current Phase、Phase 7 状态标记为 complete |
| P1 | `task_plan.md` | 更新 Decisions Made（如有新决策） |

### 通用任务

#### 修复 Bug / 问题

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `progress.md` | 在 Error Log 中添加问题描述、尝试、解决方案 |
| P1 | `progress.md` | 在 Test Results 中添加修复验证结果 |
| P2 | `task_plan.md` | 如果涉及技术选型变更，更新 Decisions Made |

#### 更新技术栈 / 依赖

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `task_plan.md` | 更新 Decisions Made |
| P0 | `task_plan.md` | 更新 关键决策记录 |
| P1 | `progress.md` | 在 Actions taken 中记录变更原因 |
| P2 | `progress.md` | 更新 5-Question Reboot Check（如技术方向变化） |

#### 创建/更新 API

**完成后需要更新的文档：**

| 优先级 | 文档 | 更新内容 |
|-------|------|---------|
| P0 | `docs/接口文档/雷达图API测试指南.md` | 添加新接口说明和测试用例 |
| P1 | `progress.md` | 添加测试结果到 Test Results |

---

## 快速查找指南

### 按文档类型查找

**更新进度：**
- `progress.md` - 完成任何阶段、遇到任何问题时

**更新计划：**
- `task_plan.md` - 完成任何阶段、更改技术选型时

**更新API文档：**
- `docs/接口文档/雷达图API测试指南.md` - 新增或修改API时

**更新项目说明：**
- `radar-chart-builder-web/README.md` - 前端项目有重大变更时
- 项目根目录 `README.md`（如存在）- 整体项目说明更新

### 按触发场景查找

**场景1: 完成一个开发阶段（Phase）**
1. 更新 `progress.md` - 添加完整的阶段记录
2. 更新 `task_plan.md` - 标记阶段完成状态
3. 更新 `progress.md` - 更新 5-Question Reboot Check

**场景2: 遇到错误并解决**
1. 更新 `progress.md` - 在 Error Log 添加记录
2. 更新 `progress.md` - 在 Test Results 添加验证结果

**场景3: 前端开发完成**
1. 更新 `radar-chart-builder-web/README.md`
2. 更新 `progress.md` - Phase 4 完成记录
3. 更新 `task_plan.md` - 标记完成、更新决策

**场景4: 新增或修改API**
1. 更新 `docs/接口文档/雷达图API测试指南.md`
2. 更新 `progress.md` - 添加测试结果

---

## 当前文档状态跟踪

| 文档 | 最后更新时间 | 内容阶段 |
|------|-------------|---------|
| `progress.md` | 2026-02-25 | Phase 1, 2, 4, 7 完成 |
| `task_plan.md` | 2026-02-25 | Phase 1, 2, 4, 7 完成 |
| `产品需求文档.md` | 2026-02-12 | 初始版本 |
| `IDEA_SETUP.md` | 2026-02-15 | IDEA配置说明 |
| `docs/plans/2026-02-16-雷达图核心功能设计.md` | 2026-02-16 | 设计完成 |
| `docs/plans/2026-02-16-雷达图核心功能实施计划.md` | 2026-02-16 | 实施完成 |
| `docs/接口文档/雷达图API测试指南.md` | 2026-02-17 | API测试完成 |
| `docs/前端测试说明.md` | 2026-02-23 | 前端测试准备 |
| `radar-chart-builder-web/README.md` | 2026-02-17 | 前端项目说明 |

---

## 文档维护规范

1. **及时更新** - 完成任务后立即更新相关文档
2. **详细记录** - Actions taken 中记录具体做了什么
3. **错误记录** - Error Log 中记录完整的错误和解决过程
4. **状态同步** - task_plan.md 中的状态要与实际进度保持一致
5. **提交规范** - 文档更新后单独提交，与代码提交分开

---

## 提交建议

**文档提交格式：**
```
docs: [简短描述]

- 更新 xxx.md
- 添加 xxx 记录
```

**示例：**
```
docs: 更新进度记录（Phase 4 前端开发完成）

- 更新 progress.md（添加 Phase 4 完成记录）
- 更新 task_plan.md（标记 Phase 4 完成）
- 更新 radar-chart-builder-web/README.md（项目说明）
```
