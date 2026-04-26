package com.aitalking.model;

import java.util.Date;

/**
 * 聊天会话实体类，用于表示用户与AI之间的一个聊天会话。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class ChatSession {
    /**
     * 会话唯一标识符
     */
    private Long id;

    /**
     * 所属用户的ID
     */
    private Long userId;

    /**
     * 会话标题，用于快速识别会话内容
     */
    private String title;

    /**
     * 会话创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}