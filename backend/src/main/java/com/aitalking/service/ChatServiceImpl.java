package com.aitalking.service;

import com.aitalking.mapper.ChatMessageMapper;
import com.aitalking.mapper.ChatSessionMapper;
import com.aitalking.model.ChatMessage;
import com.aitalking.model.ChatSession;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 聊天服务实现类
 * 提供聊天会话管理、消息存取等功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionMapper sessionMapper;

    @Autowired
    private ChatMessageMapper messageMapper;

    /**
     * 获取当前用户的所有会话列表
     *
     * @return 会话列表
     */
    @Override
    public List<ChatSession> getSessionList() {
        Long userId = StpUtil.getLoginIdAsLong();
        return sessionMapper.selectByUserId(userId);
    }

    /**
     * 创建新会话
     *
     * @param title 会话标题
     * @return 创建的会话对象
     */
    @Override
    public ChatSession createSession(String title) {
        ChatSession session = new ChatSession();
        session.setUserId(StpUtil.getLoginIdAsLong());
        session.setTitle(title);
        session.setCreateTime(new Date());
        sessionMapper.insert(session);
        return session;
    }

    /**
     * 删除指定会话
     *
     * @param sessionId 会话ID
     */
    @Override
    public void deleteSession(Long sessionId) {
        Long userId = StpUtil.getLoginIdAsLong();
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session != null && session.getUserId().equals(userId)) {
            sessionMapper.deleteById(sessionId);
        }
    }

    /**
     * 获取会话消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Override
    public List<ChatMessage> getMessages(Long sessionId) {
        Long userId = StpUtil.getLoginIdAsLong();
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session != null && session.getUserId().equals(userId)) {
            return messageMapper.selectBySessionId(sessionId);
        }
        return null;
    }

    /**
     * 保存消息
     *
     * @param sessionId 会话ID
     * @param role 消息角色
     * @param content 消息内容
     */
    @Override
    public void saveMessage(Long sessionId, String role, String content) {
        Long userId = StpUtil.getLoginIdAsLong();
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session != null && session.getUserId().equals(userId)) {
            saveMessageWithoutValidation(sessionId, role, content);
        }
    }

    /**
     * 异步保存消息（不验证归属）
     *
     * @param sessionId 会话ID
     * @param role 消息角色
     * @param content 消息内容
     */
    @Override
    public void saveMessageWithoutValidation(Long sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(new Date());
        messageMapper.insert(message);
    }
}
