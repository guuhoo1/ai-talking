package com.aitalking.service;

import com.aitalking.mapper.UserMapper;
import com.aitalking.model.User;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        return userMapper.selectById(userId);
    }

    public Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    public Long getCurrentUserIdByToken(String token) {
        if (token != null && !token.isEmpty()) {
            // 使用token获取用户ID
            StpUtil.checkLogin();
            return StpUtil.getLoginIdAsLong();
        }
        return null;
    }
}