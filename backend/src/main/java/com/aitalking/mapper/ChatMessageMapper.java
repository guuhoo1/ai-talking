package com.aitalking.mapper;

import com.aitalking.model.ChatMessage;
import java.util.Date;
import java.util.List;

public interface ChatMessageMapper {
    List<ChatMessage> selectBySessionId(Long sessionId);
    void insert(ChatMessage chatMessage);
    ChatMessage selectById(Long id);
    ChatMessage selectPreviousUserMessage(java.util.Map<String, Object> params);
}