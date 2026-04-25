package com.aitalking.mapper;

import com.aitalking.model.ChatSession;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ChatSessionMapper {
    List<ChatSession> selectByUserId(Long userId);
    void insert(ChatSession chatSession);
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);
    ChatSession selectById(Long id);
}