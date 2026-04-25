package com.aitalking.service;

import com.aitalking.dto.LoginRequest;
import com.aitalking.dto.RegisterRequest;
import com.aitalking.mapper.UserMapper;
import com.aitalking.model.User;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 密码加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encryptedPassword);

        // 保存用户
        userMapper.insert(user);

        return user;
    }

    public User login(LoginRequest request) {
        // 查找用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 登录
        StpUtil.login(user.getId());

        return user;
    }

    public void logout() {
        StpUtil.logout();
    }
}