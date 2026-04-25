package com.aitalking.service;

import com.aitalking.mapper.ChatSessionMapper;
import com.aitalking.mapper.ChatMessageMapper;
import com.aitalking.model.ChatSession;
import com.aitalking.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private UserService userService;

    public List<ChatSession> getSessionList() {
        Long userId = userService.getCurrentUserId();
        return chatSessionMapper.selectByUserId(userId);
    }

    public ChatSession createSession(String title) {
        Long userId = userService.getCurrentUserId();
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title);
        chatSessionMapper.insert(session);
        return session;
    }

    public void deleteSession(Long id) {
        Long userId = userService.getCurrentUserId();
        chatSessionMapper.deleteById(id, userId);
    }

    public List<ChatMessage> getMessages(Long sessionId) {
        // 验证会话归属
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getUserId() == null || !session.getUserId().equals(userService.getCurrentUserId())) {
            throw new RuntimeException("会话不存在或无权限");
        }
        return chatMessageMapper.selectBySessionId(sessionId);
    }

    public void saveMessage(Long sessionId, String role, String content) {
        // 验证会话归属
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
    
    // 用于异步线程中保存消息，不需要验证会话归属
    public void saveMessageWithoutValidation(Long sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        chatMessageMapper.insert(message);
    }
}