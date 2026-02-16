package com.radarchart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.radarchart.dto.param.LoginParam;
import com.radarchart.dto.param.RegisterParam;
import com.radarchart.dto.result.AuthResult;
import com.radarchart.dto.result.UserResult;
import com.radarchart.entity.User;
import com.radarchart.exception.BadRequestException;
import com.radarchart.exception.ResourceNotFoundException;
import com.radarchart.mapper.UserMapper;
import com.radarchart.service.AuthService;
import com.radarchart.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResult register(RegisterParam request) {
        LOGGER.info("开始用户注册，用户名: {}, 邮箱: {}", request.getUsername(), request.getEmail());

        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            LOGGER.warn("用户注册失败，用户名已存在: {}", request.getUsername());
            throw new BadRequestException("用户名已存在");
        }

        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            LOGGER.warn("用户注册失败，邮箱已被注册: {}", request.getEmail());
            throw new BadRequestException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());

        userMapper.insert(user);
        LOGGER.info("用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserResult userResult = UserResult.fromEntity(user);

        return new AuthResult(token, userResult);
    }

    @Override
    public AuthResult login(LoginParam request) {
        LOGGER.info("开始用户登录，账号: {}", request.getAccount());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getAccount())
               .or()
               .eq(User::getEmail, request.getAccount());

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            LOGGER.warn("用户登录失败，账号不存在: {}", request.getAccount());
            throw new ResourceNotFoundException("账号不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            LOGGER.warn("用户登录失败，密码错误，账号: {}", request.getAccount());
            throw new BadRequestException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserResult userResult = UserResult.fromEntity(user);

        LOGGER.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
        return new AuthResult(token, userResult);
    }
}
