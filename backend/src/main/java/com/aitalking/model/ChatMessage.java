package com.aitalking.model;

import java.util.Date;

/**
 * 聊天消息实体类，用于表示聊天会话中的单条消息。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class ChatMessage {
    /**
     * 消息唯一标识符
     */
    private Long id;

    /**
     * 所属会话的ID
     */
    private Long sessionId;

    /**
     * 消息角色，如"user"表示用户消息，"assistant"表示AI回复
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}