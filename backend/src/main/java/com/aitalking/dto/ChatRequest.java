package com.aitalking.dto;

/**
 * 聊天请求类，用于封装用户发送聊天消息时的请求参数。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class ChatRequest {
    /**
     * 会话ID，用于标识当前聊天会话
     */
    private Long sessionId;

    /**
     * 聊天消息内容，用户输入的文本
     */
    private String content;

    /**
     * 使用的AI模型名称，如gpt-4、claude等
     */
    private String model;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}