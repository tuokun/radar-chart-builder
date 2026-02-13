package com.radarchart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.radarchart.dto.AuthResponse;
import com.radarchart.dto.LoginRequest;
import com.radarchart.dto.RegisterRequest;
import com.radarchart.dto.UserResponse;
import com.radarchart.entity.User;
import com.radarchart.exception.BadRequestException;
import com.radarchart.exception.ResourceNotFoundException;
import com.radarchart.mapper.UserMapper;
import com.radarchart.service.AuthService;
import com.radarchart.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BadRequestException("用户名已存在");
        }

        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BadRequestException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());

        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserResponse userResponse = UserResponse.fromEntity(user);

        return new AuthResponse(token, userResponse);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getAccount())
               .or()
               .eq(User::getEmail, request.getAccount());

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new ResourceNotFoundException("账号不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserResponse userResponse = UserResponse.fromEntity(user);

        return new AuthResponse(token, userResponse);
    }
}
