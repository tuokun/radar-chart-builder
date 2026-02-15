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
- **MyBatis-Plus**: 增强版MyBatis，内置CRUD，SQL可控，适合复杂查询
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
| ORM：MyBatis-Plus | SQL可控性强，适合复杂查询，中文资料丰富，性能优化方便（替代JPA） |
| 认证：JWT + Spring Security | 无状态，适合前后端分离，Spring Security成熟可靠 |
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

## JPA vs MyBatis-Plus 决策过程
<!-- WHAT: 持久层框架选型对比 -->
<!-- WHY: 记录技术选型的思考过程 -->

### 决策背景
初始方案使用JPA/Hibernate作为ORM框架，但考虑到项目特点和长远发展，决定切换到MyBatis-Plus。

### JPA/Hibernate 优势与劣势
**优势：**
- 开发速度快，基本不需要写SQL
- 类型安全，编译期发现问题
- 自动建表，维护简单
- 一级/二级缓存提升性能

**劣势：**
- SQL控制弱，复杂查询性能优化困难
- N+1查询问题常见
- 黑盒操作，生成的SQL不可控
- 性能调优复杂，需要理解JPA概念
- 不适合复杂查询（多表关联、动态SQL、存储过程）

### MyBatis-Plus 优势与劣势
**优势：**
- SQL控制强，可以写SQL，也可以用内置方法
- 性能可优化，SQL可见
- 学习简单，对SQL熟悉就很快上手
- 中文文档丰富，国内使用广泛
- 动态SQL，支持复杂条件查询
- 代码生成器，自动生成Entity、Mapper、Service、Controller
- Lambda表达式，类型安全
- 内置分页插件
- 批量操作性能优于JPA

**劣势：**
- 需要手动建表，没有自动建表
- 需要写Mapper（虽然简化了）
- 没有缓存，需要自己集成Redis
- 换数据库需要修改SQL

### 决策理由
1. **项目特点匹配**：雷达图数据以JSON格式存储，未来有模板搜索、用户图表统计等复杂查询需求
2. **性能可控**：SQL可见，可以针对复杂查询进行优化
3. **学习成本低**：对SQL熟悉就很快上手，不需要理解JPA抽象概念
4. **中文生态好**：国内使用广泛，遇到问题容易解决
5. **迁移成本低**：当前只有User一个实体，迁移成本很低

### 迁移时机
选择在项目早期进行迁移，只有User一个实体，代码量小，迁移成本最低。

## JWT认证优化方案研究
<!-- WHAT: JWT认证优化的技术方案分析 -->
<!-- WHY: 为未来的性能和安全优化做准备 -->

### 当前JWT机制
**现状：**
- 使用JWT进行无状态认证
- 单一token，有效期24小时
- token包含用户ID和用户名
- 无token刷新机制

**局限性：**
- token有效期长（24小时），泄露风险高
- 无法主动撤销token（退出登录、修改密码）
- 每次请求都需要验证token，无法缓存用户信息
- 高并发时token解析和验证会成为性能瓶颈

### 优化方案对比

#### 方案A：JWT + Redis存储

**架构：**
```
┌─────────┐
│  登录   │
└────┬────┘
     │
     ├─> 生成Access Token（2小时） → 返回客户端
     ├─> 生成Refresh Token（7天） → 存储Redis
     └─> 用户信息缓存 → Redis（key: user:{userId}）
```

**优点：**
- ✅ 性能最优：用户信息从Redis读取，不查数据库
- ✅ 安全性高：Access Token短期，Refresh Token可撤销
- ✅ 支持主动撤销：退出登录时删除Redis中的token
- ✅ 缓存命中率高：热点用户信息常驻内存
- ✅ 可扩展性强：Redis支持集群

**缺点：**
- ❌ 系统复杂度增加：需要维护Redis集群
- ❌ 成本增加：Redis服务器和运维成本
- ❌ 一致性风险：Redis缓存和数据库需要同步

**技术栈：**
- Spring Data Redis
- Lettuce连接池
- Redisson（分布式锁）

---

#### 方案B：Refresh Token机制（数据库存储）

**架构：**
```
┌─────────┐
│  登录   │
└────┬────┘
     │
     ├─> 生成Access Token（2小时） → 返回客户端
     └─> 生成Refresh Token（7天） → 存储数据库
         └─> refresh_tokens表（id, user_id, token, expires_at）
```

**优点：**
- ✅ 实现相对简单：不需要额外的Redis
- ✅ 成本低：利用现有数据库
- ✅ 维护简单：无需管理新的基础设施
- ✅ 数据一致性：Refresh Token在数据库，与用户数据同源

**缺点：**
- ❌ 性能不如Redis：每次刷新token都需要查询数据库
- ❌ 数据库压力大：高并发时数据库成为瓶颈
- ❌ 撤销性能：批量删除token时需要更新数据库

**数据表设计：**
```sql
CREATE TABLE refresh_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token VARCHAR(512) NOT NULL UNIQUE,
  expires_at DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id),
  INDEX idx_expires_at (expires_at)
);
```

**技术栈：**
- MyBatis-Plus（已有）
- 定时任务（Spring @Scheduled）
- 数据库索引优化

---

### 优化建议

#### 触发条件
建议在以下条件之一满足时启动优化：
- 日活用户（DAU）> 1000
- API QPS > 100
- 登录响应时间 > 500ms
- 数据库CPU使用率 > 60%

#### 推荐方案选择
| 用户规模 | 推荐方案 | 理由 |
|---------|----------|------|
| 小型（< 1000 DAU） | 方案B（数据库Refresh Token） | 实现简单，成本低，满足需求 |
| 中型（1000-10000 DAU） | 方案B（数据库Refresh Token） | 可通过数据库索引优化解决性能问题 |
| 大型（> 10000 DAU） | 方案A（JWT + Redis） | 性能要求高，需要Redis缓存 |

#### 实施优先级
1. **Phase 1（立即）**：优化现有JWT配置
   - 缩短token有效期至2小时
   - 增强token验证逻辑
   - 添加token使用日志

2. **Phase 2（触发条件满足）**：实施Refresh Token
   - 创建refresh_tokens表
   - 实现token刷新接口
   - 实现退出登录时撤销token

3. **Phase 3（大规模应用）**：引入Redis
   - 迁移到JWT + Redis方案
   - 实现用户信息缓存
   - 实现分布式token管理

---

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
