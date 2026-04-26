package com.aitalking.model;

import java.util.Date;

/**
 * 用户实体类，用于表示系统中的用户信息。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class User {
    /**
     * 用户唯一标识符
     */
    private Long id;

    /**
     * 用户名，用于登录和显示
     */
    private String username;

    /**
     * 用户密码（加密存储）
     */
    private String password;

    /**
     * 用户创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}