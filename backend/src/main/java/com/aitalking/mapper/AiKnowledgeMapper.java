package com.aitalking.mapper;

import com.aitalking.model.AiKnowledge;

import java.util.List;

public interface AiKnowledgeMapper {
    int insert(AiKnowledge knowledge);
    AiKnowledge selectById(Long id);
    List<AiKnowledge> selectAll();
    AiKnowledge selectBySourceMessageId(Long sourceMessageId);
    List<AiKnowledge> selectByTechTag(String tag);
    int deleteById(Long id);
}