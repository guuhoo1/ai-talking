package com.aitalking.service;

import com.aitalking.mapper.UserMapper;
import com.aitalking.model.User;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 * 提供当前用户信息获取、用户ID获取等用户相关功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前登录用户
     * 通过当前会话获取登录用户ID，然后查询用户完整信息
     *
     * @return 当前登录的用户对象，如果未登录返回null
     */
    public User getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        return userMapper.selectById(userId);
    }

    /**
     * 获取当前登录用户ID
     * 通过当前会话直接获取登录用户的ID
     *
     * @return 当前登录用户的ID
     */
    public Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 通过Token获取当前用户ID
     * 验证token有效性后获取用户ID
     *
     * @param token 用户认证令牌，如果为null或空则返回null
     * @return 用户ID，如果token无效或为空则返回null
     */
    public Long getCurrentUserIdByToken(String token) {
        if (token != null && !token.isEmpty()) {
            StpUtil.checkLogin();
            return StpUtil.getLoginIdAsLong();
        }
        return null;
    }
}