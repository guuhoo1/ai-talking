package com.aitalking.dto;

/**
 * 聊天响应类，用于封装AI返回的聊天结果。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class ChatResponse {
    /**
     * AI返回的聊天内容文本
     */
    private String content;

    /**
     * 标识聊天是否已完成，true表示完成，false表示未完成
     */
    private boolean done;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}