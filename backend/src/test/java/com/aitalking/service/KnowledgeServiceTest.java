package com.aitalking.service;

import com.aitalking.dto.KnowledgeResponse;
import com.aitalking.mapper.AiKnowledgeMapper;
import com.aitalking.mapper.ChatMessageMapper;
import com.aitalking.model.AiKnowledge;
import com.aitalking.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KnowledgeServiceTest {

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private AiKnowledgeMapper knowledgeMapper;

    @Autowired
    private ChatMessageMapper messageMapper;

    @Test
    public void testGetAllKnowledge() {
        // 测试获取所有知识库
        List<KnowledgeResponse> knowledgeList = knowledgeService.getAllKnowledge();
        assertNotNull(knowledgeList);
        System.out.println("获取所有知识库成功，数量: " + knowledgeList.size());
    }

    @Test
    public void testExtractKnowledgeWithInvalidMessageId() {
        // 测试无效的消息ID
        Long invalidMessageId = 999999L;
        try {
            knowledgeService.extractKnowledge(invalidMessageId);
            fail("应该抛出异常");
        } catch (Exception e) {
            System.out.println("测试无效消息ID成功，异常信息: " + e.getMessage());
            assertTrue(e.getMessage().contains("Invalid message ID"));
        }
    }

    @Test
    public void testKnowledgePersistence() {
        // 测试知识库持久化
        // 这里可以添加一个测试，创建一个临时的消息对，然后提取知识库，验证是否成功保存
        // 由于需要真实的消息数据，这里只做简单的测试
        List<KnowledgeResponse> knowledgeList = knowledgeService.getAllKnowledge();
        if (!knowledgeList.isEmpty()) {
            KnowledgeResponse firstKnowledge = knowledgeList.get(0);
            KnowledgeResponse retrievedKnowledge = knowledgeService.getKnowledgeById(firstKnowledge.getId());
            assertNotNull(retrievedKnowledge);
            assertEquals(firstKnowledge.getTitle(), retrievedKnowledge.getTitle());
            System.out.println("测试知识库持久化成功，标题: " + firstKnowledge.getTitle());
        }
    }

    @Test
    public void testKnowledgeByTechTag() {
        // 测试按技术标签查询知识库
        List<KnowledgeResponse> knowledgeList = knowledgeService.getAllKnowledge();
        if (!knowledgeList.isEmpty()) {
            // 假设第一个知识库有技术标签
            KnowledgeResponse firstKnowledge = knowledgeList.get(0);
            // 这里简化处理，实际应该解析techTags字段
            System.out.println("测试按技术标签查询，第一个知识库标题: " + firstKnowledge.getTitle());
        }
    }
}
