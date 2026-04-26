package com.aitalking.mapper;

import com.aitalking.model.AiKnowledge;

import java.util.List;

/**
 * AI知识库数据访问接口
 * 提供AI知识实体的数据库操作方法
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface AiKnowledgeMapper {

    /**
     * 插入AI知识条目
     *
     * @param knowledge AI知识实体对象
     * @return 影响的行数
     */
    int insert(AiKnowledge knowledge);

    /**
     * 根据知识ID查询AI知识
     *
     * @param id 知识ID
     * @return AI知识实体对象，如果不存在返回null
     */
    AiKnowledge selectById(Long id);

    /**
     * 查询所有AI知识条目
     *
     * @return AI知识列表
     */
    List<AiKnowledge> selectAll();

    /**
     * 根据源消息ID查询AI知识
     * 用于查找由特定用户消息生成的AI知识
     *
     * @param sourceMessageId 源消息ID
     * @return AI知识实体对象，如果不存在返回null
     */
    AiKnowledge selectBySourceMessageId(Long sourceMessageId);

    /**
     * 根据技术标签查询AI知识列表
     *
     * @param tag 技术标签
     * @return 匹配该标签的AI知识列表
     */
    List<AiKnowledge> selectByTechTag(String tag);

    /**
     * 根据知识ID删除AI知识
     *
     * @param id 知识ID
     * @return 影响的行数
     */
    int deleteById(Long id);
}