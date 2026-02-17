# 后端基础搭建与用户认证模块实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 搭建SpringBoot 3.5项目基础架构，实现用户注册/登录功能和JWT认证

**Architecture:** 采用前后端分离架构，后端提供RESTful API，使用Spring Security进行认证授权，JWT作为token机制，MariaDB作为数据存储

**Tech Stack:** SpringBoot 3.5, Java 21, MariaDB 11.4.7, Spring Security, JWT, JPA/Hibernate

---

## Task 1: 创建SpringBoot项目基础结构

**Files:**
- Create: `radar-chart-builder-server/pom.xml`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/RadarChartApplication.java`
- Create: `radar-chart-builder-server/src/main/resources/application.yml`
- Create: `radar-chart-builder-server/.gitignore`

**Step 1: 创建pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
        <relativePath/>
    </parent>
    <groupId>com.radarchart</groupId>
    <artifactId>radar-chart-builder</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>radar-chart-builder</name>
    <description>Radar Chart Builder Backend</description>
    <properties>
        <java.version>21</java.version>
        <mariadb.version>11.4.7</mariadb.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Step 2: 创建主应用类**

```java
package com.radarchart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RadarChartApplication {
    public static void main(String[] args) {
        SpringApplication.run(RadarChartApplication.class, args);
    }
}
```

**Step 3: 创建application.yml配置**

```yaml
spring:
  application:
    name: radar-chart-builder
  datasource:
    url: jdbc:mariadb://localhost:3306/radar_chart_db?createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MariaDBDialect
        format_sql: true

server:
  port: 8080

jwt:
  secret: your-256-bit-secret-key-change-this-in-production
  expiration: 86400000
```

**Step 4: 创建.gitignore**

```
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/
.mvn/
mvnw
mvnw.cmd
*.iml
.idea/
.DS_Store
```

**Step 5: 验证项目能正常启动**

```bash
cd radar-chart-builder-server
mvn clean install
mvn spring-boot:run
```

**Expected:** 应用启动成功，日志显示Tomcat运行在8080端口

**Step 6: 提交**

```bash
git add .
git commit -m "feat: initialize SpringBoot project structure"
```

---

## Task 2: 创建User实体和Repository

**Files:**
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/entity/User.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/repository/UserRepository.java`

**Step 1: 创建User实体**

```java
package com.radarchart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String nickname;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Step 2: 创建UserRepository**

```java
package com.radarchart.repository;

import com.radarchart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

**Step 3: 提交**

```bash
git add src/main/java/com/radarchart/entity/User.java src/main/java/com/radarchart/repository/UserRepository.java
git commit -m "feat: add User entity and UserRepository"
```

---

## Task 3: 创建DTO类

**Files:**
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/dto/RegisterRequest.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/dto/LoginRequest.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/dto/AuthResponse.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/dto/UserResponse.java`

**Step 1: 创建RegisterRequest**

```java
package com.radarchart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
}
```

**Step 2: 创建LoginRequest**

```java
package com.radarchart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

**Step 3: 创建AuthResponse**

```java
package com.radarchart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;
}
```

**Step 4: 创建UserResponse**

```java
package com.radarchart.dto;

import com.radarchart.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
```

**Step 5: 提交**

```bash
git add src/main/java/com/radarchart/dto/
git commit -m "feat: add request and response DTOs"
```

---

## Task 4: 实现JWT工具类

**Files:**
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/config/JwtConfig.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/util/JwtUtil.java`

**Step 1: 创建JwtConfig配置类**

```java
package com.radarchart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getSecret() {
        return secret;
    }

    public Long getExpiration() {
        return expiration;
    }
}
```

**Step 2: 创建JwtUtil工具类**

```java
package com.radarchart.util;

import com.radarchart.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("username", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

**Step 3: 提交**

```bash
git add src/main/java/com/radarchart/config/JwtConfig.java src/main/java/com/radarchart/util/JwtUtil.java
git commit -m "feat: add JWT utility for token generation and validation"
```

---

## Task 5: 实现Spring Security配置

**Files:**
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/config/SecurityConfig.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/config/JwtAuthenticationFilter.java`

**Step 1: 创建SecurityConfig配置类**

```java
package com.radarchart.config;

import com.radarchart.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

**Step 2: 创建JwtAuthenticationFilter过滤器**

```java
package com.radarchart.config;

import com.radarchart.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

**Step 3: 提交**

```bash
git add src/main/java/com/radarchart/config/
git commit -m "feat: configure Spring Security with JWT authentication"
```

---

## Task 6: 实现用户认证Service

**Files:**
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/service/AuthService.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/service/impl/AuthServiceImpl.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/exception/BadRequestException.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/exception/ResourceNotFoundException.java`
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/exception/GlobalExceptionHandler.java`

**Step 1: 创建异常类**

```java
package com.radarchart.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
```

```java
package com.radarchart.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

```java
package com.radarchart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

**Step 2: 创建AuthService接口**

```java
package com.radarchart.service;

import com.radarchart.dto.AuthResponse;
import com.radarchart.dto.LoginRequest;
import com.radarchart.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
```

**Step 3: 创建AuthServiceImpl实现类**

```java
package com.radarchart.service.impl;

import com.radarchart.dto.AuthResponse;
import com.radarchart.dto.LoginRequest;
import com.radarchart.dto.RegisterRequest;
import com.radarchart.dto.UserResponse;
import com.radarchart.entity.User;
import com.radarchart.exception.BadRequestException;
import com.radarchart.exception.ResourceNotFoundException;
import com.radarchart.repository.UserRepository;
import com.radarchart.service.AuthService;
import com.radarchart.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("用户名已存在");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserResponse userResponse = UserResponse.fromEntity(user);

        return new AuthResponse(token, userResponse);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getAccount())
                .orElseGet(() -> userRepository.findByEmail(request.getAccount())
                        .orElseThrow(() -> new ResourceNotFoundException("账号不存在")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserResponse userResponse = UserResponse.fromEntity(user);

        return new AuthResponse(token, userResponse);
    }
}
```

**Step 4: 提交**

```bash
git add src/main/java/com/radarchart/service/ src/main/java/com/radarchart/exception/
git commit -m "feat: implement authentication service with exception handling"
```

---

## Task 7: 实现认证Controller

**Files:**
- Create: `radar-chart-builder-server/src/main/java/com/radarchart/controller/AuthController.java`

**Step 1: 创建AuthController**

```java
package com.radarchart.controller;

import com.radarchart.dto.AuthResponse;
import com.radarchart.dto.LoginRequest;
import com.radarchart.dto.RegisterRequest;
import com.radarchart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

**Step 2: 提交**

```bash
git add src/main/java/com/radarchart/controller/AuthController.java
git commit -m "feat: add authentication controller"
```

---

## Task 8: 测试API端点

**Files:**
- Modify: `radar-chart-builder-server/src/main/resources/application.yml`

**Step 1: 启动应用**

```bash
cd radar-chart-builder-server
mvn spring-boot:run
```

**Expected:** 应用启动成功，数据库表自动创建

**Step 2: 测试注册接口**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456",
    "nickname": "测试用户"
  }'
```

**Expected:** 返回包含token和用户信息的JSON

**Step 3: 测试登录接口**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "account": "testuser",
    "password": "123456"
  }'
```

**Expected:** 返回token和用户信息

**Step 4: 测试受保护的接口**

```bash
curl http://localhost:8080/api/some-protected-endpoint \
  -H "Authorization: Bearer <your-token>"
```

**Expected:** 404 Not Found (接口不存在) 或正常返回（如果有该接口）

**Step 5: 提交**

```bash
git add .
git commit -m "test: verify authentication API endpoints"
```

---

## 任务总结

完成以上所有任务后，后端基础架构已经搭建完成，包含：
- SpringBoot 3.5 + Java 21 项目结构
- MariaDB数据库连接配置
- Spring Security + JWT认证
- 用户注册/登录功能
- RESTful API端点

下一步可以开始实现雷达图相关功能。
