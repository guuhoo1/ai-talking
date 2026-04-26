package com.aitalking.service;

import com.aitalking.dto.LoginRequest;
import com.aitalking.dto.RegisterRequest;
import com.aitalking.mapper.UserMapper;
import com.aitalking.model.User;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 认证服务类
 * 提供用户注册、登录、登出等认证相关功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     * 检查用户名是否已存在，若不存在则创建新用户并返回
     *
     * @param request 注册请求对象，包含用户名和密码
     * @return 注册成功的用户对象
     * @throws RuntimeException 如果用户名已存在，抛出运行时异常
     */
    public User register(RegisterRequest request) {
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encryptedPassword);

        userMapper.insert(user);

        return user;
    }

    /**
     * 用户登录
     * 验证用户凭据，若验证成功则创建会话并返回用户信息
     *
     * @param request 登录请求对象，包含用户名和密码
     * @return 登录成功的用户对象
     * @throws RuntimeException 如果用户名或密码错误，抛出运行时异常
     */
    public User login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("用户名或密码错误");
        }

        StpUtil.login(user.getId());

        return user;
    }

    /**
     * 用户登出
     * 销毁当前用户的会话
     */
    public void logout() {
        StpUtil.logout();
    }
}