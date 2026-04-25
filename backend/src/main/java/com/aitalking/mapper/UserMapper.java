package com.aitalking.mapper;

import com.aitalking.model.User;

public interface UserMapper {
    User selectByUsername(String username);
    void insert(User user);
    User selectById(Long id);
}