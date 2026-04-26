package com.aitalking.controller;

import com.aitalking.dto.LoginRequest;
import com.aitalking.dto.RegisterRequest;
import com.aitalking.dto.Result;
import com.aitalking.model.User;
import com.aitalking.service.AuthService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户注册、登录、登出等认证相关请求
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     * 根据注册请求信息创建新用户账户
     *
     * @param request 包含用户名、密码等注册信息的请求体
     * @return Result<User> 返回注册成功后的用户信息
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return Result.success(user);
    }

    /**
     * 用户登录
     * 验证用户凭据并返回认证令牌
     *
     * @param request 包含用户名和密码的登录请求体
     * @return Result<Map<String, Object>> 返回用户信息和登录令牌
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", StpUtil.getTokenValue());
        return Result.success(data);
    }

    /**
     * 用户登出
     * 清除当前用户的登录状态
     *
     * @return Result<Void> 返回登出操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success(null);
    }
}