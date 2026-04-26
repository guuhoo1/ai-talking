package com.aitalking.controller;

import com.aitalking.dto.Result;
import com.aitalking.model.User;
import com.aitalking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 * 处理用户信息查询等用户相关请求
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     * 获取当前登录用户的基本信息
     *
     * @return Result<User> 返回当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }
}