package com.aitalking.service;

import com.aitalking.dto.LoginRequest;
import com.aitalking.dto.RegisterRequest;
import com.aitalking.model.User;

/**
 * 认证服务接口
 * 提供用户注册、登录、登出等认证相关功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param request 注册请求，包含用户名和密码
     * @return 注册成功的用户对象
     */
    User register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的用户对象
     */
    User login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();

}