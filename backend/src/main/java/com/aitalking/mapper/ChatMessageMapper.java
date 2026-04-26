package com.aitalking.mapper;

import com.aitalking.model.ChatMessage;
import java.util.Date;
import java.util.List;

/**
 * 聊天消息数据访问接口
 * 提供聊天消息实体的数据库操作方法
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface ChatMessageMapper {

    /**
     * 根据会话ID查询该会话的所有消息
     *
     * @param sessionId 会话ID
     * @return 聊天消息列表，按创建时间升序排列
     */
    List<ChatMessage> selectBySessionId(Long sessionId);

    /**
     * 插入新的聊天消息
     *
     * @param chatMessage 聊天消息实体对象
     */
    void insert(ChatMessage chatMessage);

    /**
     * 根据消息ID查询聊天消息
     *
     * @param id 消息ID
     * @return 聊天消息实体对象，如果不存在返回null
     */
    ChatMessage selectById(Long id);

    /**
     * 查询指定会话中用户发送的上一条消息
     * 用于确定AI回复的上下文
     *
     * @param params 参数字段，包含sessionId和可选的当前消息ID
     * @return 用户发送的消息对象，如果不存在返回null
     */
    ChatMessage selectPreviousUserMessage(java.util.Map<String, Object> params);
}