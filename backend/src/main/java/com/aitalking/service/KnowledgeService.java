package com.aitalking.service;

import com.aitalking.dto.KnowledgeResponse;

import java.util.List;

/**
 * 知识库服务接口
 * 提供AI对话知识的提取、存储、查询和管理功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface KnowledgeService {

    /**
     * 从AI对话中提取知识卡片
     *
     * @param messageId 消息ID
     * @return 知识卡片响应对象
     * @throws Exception 提取过程中可能出现的异常
     */
    KnowledgeResponse extractKnowledge(Long messageId) throws Exception;

    /**
     * 根据ID获取知识卡片
     *
     * @param id 知识卡片ID
     * @return 知识卡片响应对象
     */
    KnowledgeResponse getKnowledgeById(Long id);

    /**
     * 获取所有知识卡片
     *
     * @return 知识卡片列表
     */
    List<KnowledgeResponse> getAllKnowledge();

    /**
     * 根据技术标签查询知识卡片
     *
     * @param tag 技术标签
     * @return 知识卡片列表
     */
    List<KnowledgeResponse> getKnowledgeByTechTag(String tag);

    /**
     * 删除知识卡片
     *
     * @param id 知识卡片ID
     * @throws Exception 删除过程中可能出现的异常
     */
    void deleteKnowledge(Long id) throws Exception;

}