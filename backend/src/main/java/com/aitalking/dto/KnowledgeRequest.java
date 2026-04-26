package com.aitalking.dto;

/**
 * 知识库请求类，用于封装从聊天消息提取知识库的请求参数。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class KnowledgeRequest {
    /**
     * 消息ID，标识需要提取为知识库的源消息
     */
    private Long messageId;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}