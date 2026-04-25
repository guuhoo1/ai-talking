package com.aitalking.mapper;

import com.aitalking.model.ChatMessage;
import java.util.List;

public interface ChatMessageMapper {
    List<ChatMessage> selectBySessionId(Long sessionId);
    void insert(ChatMessage chatMessage);
}