# Findings & Decisions

<!--
  WHAT: 雷达图生成器项目技术选型和发现记录
  WHY: 记录技术调研结果、架构决策、参考资料
  WHEN: 项目初期创建，随发现更新
-->

## Requirements
<!-- WHAT: 用户需求分解 -->
<!-- WHY: 明确需要实现的功能 -->
- 用户注册/登录（账号密码方式）
- 创建雷达图（自定义维度和参数）
- 雷达图可视化展示
- 查看和管理我的雷达图
- 保存和删除雷达图
- 模板分享和使用（待定功能）

## Research Findings
<!-- WHAT: 技术调研发现 -->
<!-- WHY: 支持技术选型决策 -->

### 后端技术栈
- **SpringBoot 3.5**: 最新稳定版，支持Java 21，性能优化，长期支持
- **Spring Security**: 安全框架成熟生态完善，支持JWT无状态认证
- **MariaDB 11.4.7**: MySQL分支，性能优秀，完全兼容MySQL
- **JPA/Hibernate**: ORM框架，简化数据库操作
- **JWT**: 无状态认证，适合前后端分离架构

### 前端技术栈
- **Vue 3**:
  - Composition API更灵活
  - 学习曲线平缓
  - 文档完善（中文文档质量高）
  - 生态丰富（Element Plus、Vant等）
  - 国内社区活跃，资料多

- **Element Plus**:
  - Vue 3官方推荐UI库
  - 组件丰富，开箱即用
  - 设计规范统一
  - 中文文档完整

- **ECharts**:
  - 百度开源，功能最全面
  - 原生支持雷达图，配置简单
  - 文档完善，中文资料丰富
  - 性能好，交互性强
  - 示例代码多，上手快

- **Chart.js**:
  - 轻量级，简单易用
  - 原生支持雷达图
  - 配置直观，API简单
  - 功能相对基础

- **D3.js**:
  - 功能最强大，灵活性最高
  - 可以创建任何类型的图表
  - 学习曲线陡峭，需要更多代码
  - 适合需要高度自定义的场景

### API文档工具
- **Knife4j**: Swagger增强版，界面美观，支持在线测试，中文支持好
- **Swagger**: 标准的API文档工具，生态完善
- **Postman**: 手动测试API，不适合自动生成文档

## Technical Decisions
<!-- WHAT: 技术架构决策 -->
<!-- WHY: 记录选型理由，便于回顾 -->

| Decision | Rationale |
|----------|-----------|
| 后端：SpringBoot 3.5 + Java 21 | 最新稳定版，长期支持，性能优秀，社区活跃 |
| 数据库：MariaDB 11.4.7 | 开源、高性能、与MySQL兼容，文档丰富 |
| 认证：JWT + Spring Security | 无状态，适合前后端分离，Spring Security成熟可靠 |
| ORM：JPA/Hibernate | 减少SQL编写，提高开发效率，类型安全 |
| 前端框架：Vue 3 | 学习曲线平缓，中文文档完善，生态丰富，国内社区活跃 |
| UI库：Element Plus | Vue 3官方推荐，组件丰富，设计规范统一 |
| 图表库：ECharts | 原生支持雷达图，功能强大，中文文档丰富，示例多 |
| API文档：Knife4j | Swagger增强版，界面美观，支持在线测试，中文支持好 |

## Issues Encountered
<!-- WHAT: 遇到的问题及解决方案 -->
<!-- WHY: 积累问题解决经验 -->

| Issue | Resolution |
|-------|------------|
| session-catchup.py脚本路径错误 | 忽略，直接创建规划文件 |
| 权限限制阻止编辑项目根目录和.opencode/plans/文件 | 使用全局规划目录（后已修正） |
| 规划文件命名不符合plan-with-files规范 | 应使用task_plan.md、findings.md、progress.md三个固定文件名 |

## Resources
<!-- WHAT: 有用的资源链接 -->
<!-- WHY: 方便后续查阅 -->

### 官方文档
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Vue 3: https://cn.vuejs.org/
- Element Plus: https://element-plus.org/
- ECharts: https://echarts.apache.org/zh/
- Knife4j: https://doc.xiaominfo.com/

### 参考资料
- Spring Security JWT: https://spring.io/guides/gs/securing-web/
- ECharts雷达图示例: https://echarts.apache.org/examples/zh/index.html
- Vue 3官方教程: https://cn.vuejs.org/guide/introduction.html

## Backend Implementation Details
<!-- WHAT: 后端实现细节 -->
<!-- WHY: 记录后端开发的技术细节 -->

### 项目结构
```
radar-chart-builder/
├── radar-chart-builder-server/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/radarchart/
│   │   │   │   └── RadarChartApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   └── .gitignore
```

### 核心依赖说明
- **spring-boot-starter-web**: Web应用基础，内嵌Tomcat
- **spring-boot-starter-data-jpa**: JPA持久化，简化数据库操作
- **spring-boot-starter-security**: 安全框架，提供认证授权
- **spring-boot-starter-validation**: 参数验证（@NotBlank、@Email等）
- **jjwt-api/jjwt-impl/jjwt-jackson**: JWT token生成和验证（版本0.12.3）
- **mariadb-java-client**: MariaDB数据库驱动
- **lombok**: 简化Java代码（@Data、@NoArgsConstructor等）
- **spring-boot-starter-test**: 测试框架
- **spring-security-test**: Spring Security测试支持

### 配置说明
- **数据库**: MariaDB 11.4.7，连接地址jdbc:mariadb://localhost:3306/radar_chart_db
- **JPA**: ddl-auto=update（自动更新表结构），show-sql=true（显示SQL）
- **JWT**:
  - secret: your-256-bit-secret-key-change-this-in-production（需在生产环境修改）
  - expiration: 86400000（24小时）
- **Server端口**: 8080

### 待实现模块（按计划文档）
- User实体和Repository
- DTO类（RegisterRequest、LoginRequest、AuthResponse、UserResponse）
- JWT工具类（JwtConfig、JwtUtil）
- Spring Security配置（SecurityConfig、JwtAuthenticationFilter）
- 认证Service（AuthService接口、AuthServiceImpl实现）
- 认证Controller（AuthController）
- 异常处理（BadRequestException、ResourceNotFoundException、GlobalExceptionHandler）

### 待验证项
- [ ] MariaDB是否已安装并运行在localhost:3306
- [ ] 数据库用户名密码是否为root/root
- [ ] Java 21是否已安装
- [ ] Maven是否已安装

## Visual/Browser Findings
<!-- WHAT: 视觉内容发现 -->
<!-- WHY: 重要发现需要记录 -->

---
<!--
  REMINDER: The 2-Action Rule
  After every 2 view/browser/search operations, you MUST update this file.
  This prevents visual information from being lost when context resets.
-->
*技术选型已确定，无进一步视觉内容*
