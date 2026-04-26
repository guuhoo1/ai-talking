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
 * 认证服务实现类
 * 实现用户注册、登录、登出等认证相关功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param request 注册请求，包含用户名和密码
     * @return 注册成功的用户对象
     */
    @Override
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

    /**
     * 用户登录
     *
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的用户对象
     */
    @Override
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

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }
}
