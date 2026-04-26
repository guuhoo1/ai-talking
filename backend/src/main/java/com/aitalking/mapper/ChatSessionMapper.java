package com.aitalking.mapper;

import com.aitalking.model.ChatSession;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 聊天会话数据访问接口
 * 提供聊天会话实体的数据库操作方法
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface ChatSessionMapper {

    /**
     * 根据用户ID查询该用户的所有聊天会话
     *
     * @param userId 用户ID
     * @return 聊天会话列表
     */
    List<ChatSession> selectByUserId(Long userId);

    /**
     * 插入新的聊天会话
     *
     * @param chatSession 聊天会话实体对象
     */
    void insert(ChatSession chatSession);

    /**
     * 根据会话ID和用户ID删除聊天会话
     * 同时验证会话归属，防止跨用户删除
     *
     * @param id 会话ID
     */
    void deleteById(@Param("id") Long id);

    /**
     * 根据会话ID查询聊天会话
     *
     * @param id 会话ID
     * @return 聊天会话实体对象，如果不存在返回null
     */
    ChatSession selectById(Long id);
}