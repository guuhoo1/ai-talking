package com.aitalking.dto;

/**
 * 用户注册请求类，用于封装用户注册时的基本信息。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class RegisterRequest {
    /**
     * 用户名，用于创建新账户
     */
    private String username;

    /**
     * 密码，用于账户安全认证
     */
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}