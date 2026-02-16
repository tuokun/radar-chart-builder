package com.radarchart.service;

import com.radarchart.dto.param.LoginParam;
import com.radarchart.dto.param.RegisterParam;
import com.radarchart.dto.result.AuthResult;

/**
 * 用户认证服务接口
 */
public interface AuthService {
    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 认证结果（包含token和用户信息）
     */
    AuthResult register(RegisterParam request);

    /**
     * 用户登录
     *
     * @param request 登录参数
     * @return 认证结果（包含token和用户信息）
     */
    AuthResult login(LoginParam request);
}
