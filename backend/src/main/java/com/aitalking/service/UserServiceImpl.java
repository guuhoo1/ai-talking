package com.aitalking.service;

import com.aitalking.mapper.UserMapper;
import com.aitalking.model.User;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 提供当前用户信息获取、用户ID获取等功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户对象，如未登录返回null
     */
    @Override
    public User getCurrentUser() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            return userMapper.selectById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 当前登录用户ID，如未登录返回null
     */
    @Override
    public Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过token获取当前用户ID
     *
     * @param token 用户认证令牌
     * @return 用户ID，如果token无效或为空返回null
     */
    @Override
    public Long getCurrentUserIdByToken(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                StpUtil.setTokenValue(token);
                return StpUtil.getLoginIdAsLong();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
