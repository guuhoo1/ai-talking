package com.aitalking.service;

import com.aitalking.model.User;

/**
 * 用户服务接口
 * 提供当前用户信息获取、用户ID获取等功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface UserService {

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户对象，如未登录返回null
     */
    User getCurrentUser();

    /**
     * 获取当前登录用户ID
     *
     * @return 当前登录用户ID，如未登录返回null
     */
    Long getCurrentUserId();

    /**
     * 通过token获取当前用户ID
     *
     * @param token 用户认证令牌
     * @return 用户ID，如果token无效或为空返回null
     */
    Long getCurrentUserIdByToken(String token);

}