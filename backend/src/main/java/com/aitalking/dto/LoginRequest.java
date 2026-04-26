package com.aitalking.dto;

/**
 * 用户登录请求类，用于封装用户登录时的凭证信息。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class LoginRequest {
    /**
     * 用户名，用于登录凭证
     */
    private String username;

    /**
     * 密码，用于登录凭证
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