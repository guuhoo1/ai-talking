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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", StpUtil.getTokenValue());
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success(null);
    }
}