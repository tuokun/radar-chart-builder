package com.radarchart.controller;

import com.radarchart.dto.AuthResponse;
import com.radarchart.dto.LoginRequest;
import com.radarchart.dto.RegisterRequest;
import com.radarchart.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户认证", description = "用户注册、登录相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户注册", description = "新用户注册接口，注册成功后返回JWT Token和用户信息")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        LOGGER.info("收到用户注册请求，用户名: {}, 邮箱: {}", request.getUsername(), request.getEmail());
        AuthResponse response = authService.register(request);
        LOGGER.info("用户注册请求处理完成");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "用户登录", description = "使用用户名或邮箱登录，成功后返回JWT Token和用户信息")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LOGGER.info("收到用户登录请求，账号: {}", request.getAccount());
        AuthResponse response = authService.login(request);
        LOGGER.info("用户登录请求处理完成");
        return ResponseEntity.ok(response);
    }
}
