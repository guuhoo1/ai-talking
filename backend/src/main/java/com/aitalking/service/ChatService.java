package com.aitalking.service;

import com.aitalking.mapper.ChatSessionMapper;
import com.aitalking.mapper.ChatMessageMapper;
import com.aitalking.model.ChatSession;
import com.aitalking.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天服务类
 * 提供聊天会话管理、消息存取等聊天相关功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class ChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户的会话列表
     * 查询当前登录用户的所有聊天会话
     *
     * @return 当前用户的会话列表
     */
    public List<ChatSession> getSessionList() {
        Long userId = userService.getCurrentUserId();
        return chatSessionMapper.selectByUserId(userId);
    }

    /**
     * 创建新的聊天会话
     * 为当前用户创建一个新的聊天会话
     *
     * @param title 会话标题
     * @return 创建成功的会话对象
     */
    public ChatSession createSession(String title) {
        Long userId = userService.getCurrentUserId();
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title);
        chatSessionMapper.insert(session);
        return session;
    }

    /**
     * 删除聊天会话
     * 删除指定ID的会话，仅允许删除属于当前用户的会话
     *
     * @param id 要删除的会话ID
     */
    public void deleteSession(Long id) {
        Long userId = userService.getCurrentUserId();
        chatSessionMapper.deleteById(id, userId);
    }

    /**
     * 获取会话的消息列表
     * 获取指定会话的所有消息，会验证会话归属
     *
     * @param sessionId 会话ID
     * @return 会话中的消息列表
     * @throws RuntimeException 如果会话不存在或不属于当前用户
     */
    public List<ChatMessage> getMessages(Long sessionId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getUserId() == null || !session.getUserId().equals(userService.getCurrentUserId())) {
            throw new RuntimeException("会话不存在或无权限");
        }
        return chatMessageMapper.selectBySessionId(sessionId);
    }

    /**
     * 保存聊天消息
     * 向指定会话添加新消息，会验证会话归属
     *
     * @param sessionId 会话ID
     * @param role 消息角色（如user、assistant）
     * @param content 消息内容
     * @throws RuntimeException 如果会话不存在或不属于当前用户
     */
    public void saveMessage(Long sessionId, String role, String content) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getUserId() == null || !session.getUserId().equals(userService.getCurrentUserId())) {
            throw new RuntimeException("会话不存在或无权限");
        }
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        chatMessageMapper.insert(message);
    }

    /**
     * 保存聊天消息（无需会话归属验证）
     * 用于异步线程中保存消息，跳过会话归属验证
     *
     * @param sessionId 会话ID
     * @param role 消息角色（如user、assistant）
     * @param content 消息内容
     */
    public void saveMessageWithoutValidation(Long sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        chatMessageMapper.insert(message);
    }
}