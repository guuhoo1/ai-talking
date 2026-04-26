package com.aitalking.service;

import com.aitalking.model.ChatMessage;
import com.aitalking.model.ChatSession;

import java.util.List;

/**
 * 聊天服务接口
 * 提供聊天会话管理、消息存取等功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface ChatService {

    /**
     * 获取当前用户的所有会话列表
     *
     * @return 会话列表
     */
    List<ChatSession> getSessionList();

    /**
     * 创建新会话
     *
     * @param title 会话标题
     * @return 创建的会话对象
     */
    ChatSession createSession(String title);

    /**
     * 删除指定会话
     *
     * @param sessionId 会话ID
     */
    void deleteSession(Long sessionId);

    /**
     * 获取会话消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<ChatMessage> getMessages(Long sessionId);

    /**
     * 保存消息
     *
     * @param sessionId 会话ID
     * @param role 消息角色
     * @param content 消息内容
     */
    void saveMessage(Long sessionId, String role, String content);

    /**
     * 异步保存消息（不验证归属）
     *
     * @param sessionId 会话ID
     * @param role 消息角色
     * @param content 消息内容
     */
    void saveMessageWithoutValidation(Long sessionId, String role, String content);

}