package com.aitalking.mapper;

import com.aitalking.model.User;

/**
 * 用户数据访问接口
 * 提供用户实体的数据库操作方法
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface UserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体对象，如果不存在返回null
     */
    User selectByUsername(String username);

    /**
     * 插入用户信息
     *
     * @param user 用户实体对象
     */
    void insert(User user);

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体对象，如果不存在返回null
     */
    User selectById(Long id);
}