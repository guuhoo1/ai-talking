package com.aitalking.service;

import com.aitalking.dto.KnowledgeResponse;
import com.aitalking.mapper.AiKnowledgeMapper;
import com.aitalking.mapper.ChatMessageMapper;
import com.aitalking.model.AiKnowledge;
import com.aitalking.model.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 知识库服务类
 * 提供AI对话知识的提取、存储、查询和管理功能
 * 通过AI模型从用户与助手的对话中提炼关键知识点
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class KnowledgeService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeService.class);

    @Autowired
    private AiKnowledgeMapper knowledgeMapper;

    @Autowired
    private ChatMessageMapper messageMapper;

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 从AI对话消息中提取知识
     * 根据指定的消息ID，获取对应的助手消息和用户消息，
     * 调用AI模型提炼关键知识点并存储到知识库
     *
     * @param messageId 助手消息ID，用于定位对话上下文
     * @return 提取的知识卡片响应对象
     * @throws Exception 如果消息无效、未找到用户消息、AI处理失败或数据库保存失败
     */
    public KnowledgeResponse extractKnowledge(Long messageId) throws Exception {
        logger.info("开始提取知识库，消息ID: {}", messageId);
        try {
            ChatMessage assistantMessage = messageMapper.selectById(messageId);
            if (assistantMessage == null || !"assistant".equals(assistantMessage.getRole())) {
                logger.warn("无效的消息ID或非assistant消息: {}", messageId);
                throw new Exception("Invalid message ID or not an assistant message");
            }

            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("sessionId", assistantMessage.getSessionId());
            params.put("createTime", assistantMessage.getCreateTime());
            ChatMessage userMessage = messageMapper.selectPreviousUserMessage(params);
            if (userMessage == null) {
                logger.warn("未找到上一条用户消息，会话ID: {}", assistantMessage.getSessionId());
                throw new Exception("No previous user message found");
            }

            logger.info("获取到用户消息和助手消息，开始构建提示词");
            String prompt = buildPrompt(userMessage.getContent(), assistantMessage.getContent());

            logger.info("调用AI模型进行知识提炼");
            String aiResponse = ollamaService.generate(prompt);
            logger.info("AI响应获取成功，开始解析");

            JsonNode aiResponseNode = objectMapper.readTree(aiResponse);
            if (!aiResponseNode.has("valid") || !aiResponseNode.get("valid").asBoolean()) {
                logger.warn("AI提炼失败: {}", aiResponse);
                throw new Exception("AI failed to extract knowledge");
            }

            if (!aiResponseNode.has("title") || !aiResponseNode.has("summary") || !aiResponseNode.has("tech_tags") || !aiResponseNode.has("reusable")) {
                logger.warn("AI响应缺少必要字段: {}", aiResponse);
                throw new Exception("AI response missing required fields");
            }

            AiKnowledge knowledge = new AiKnowledge();
            knowledge.setTitle(aiResponseNode.get("title").asText());
            knowledge.setSummary(aiResponseNode.get("summary").asText());
            knowledge.setCoreCode(aiResponseNode.has("core_code") ? aiResponseNode.get("core_code").asText() : null);
            knowledge.setTechTags(aiResponseNode.get("tech_tags").toString());
            knowledge.setScene(aiResponseNode.has("scene") ? aiResponseNode.get("scene").asText() : null);
            knowledge.setPrinciple(aiResponseNode.has("principle") ? aiResponseNode.get("principle").asText() : null);
            knowledge.setNotes(aiResponseNode.has("notes") ? aiResponseNode.get("notes").asText() : null);
            knowledge.setReusable(aiResponseNode.get("reusable").asBoolean());
            knowledge.setSourceMessageId(messageId);
            knowledge.setCreateTime(new Date());

            logger.info("知识卡片创建成功，标题: {}", knowledge.getTitle());

            int insertResult = knowledgeMapper.insert(knowledge);
            if (insertResult != 1) {
                logger.error("保存知识卡片失败，影响行数: {}", insertResult);
                throw new Exception("Failed to save knowledge to database");
            }

            logger.info("知识卡片保存成功，ID: {}", knowledge.getId());

            KnowledgeResponse response = new KnowledgeResponse();
            BeanUtils.copyProperties(knowledge, response);

            logger.info("知识库提取完成，返回响应");
            return response;
        } catch (Exception e) {
            logger.error("提取知识库时发生错误: {}", e.getMessage(), e);
            throw new Exception("Error extracting knowledge: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取知识卡片
     * 查询指定ID的知识库条目
     *
     * @param id 知识卡片ID
     * @return 知识卡片响应对象，如果不存在返回null
     */
    public KnowledgeResponse getKnowledgeById(Long id) {
        AiKnowledge knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            return null;
        }

        KnowledgeResponse response = new KnowledgeResponse();
        BeanUtils.copyProperties(knowledge, response);
        return response;
    }

    /**
     * 获取所有知识卡片
     * 查询知识库中的所有条目
     *
     * @return 所有知识卡片的响应对象列表
     */
    public List<KnowledgeResponse> getAllKnowledge() {
        List<AiKnowledge> knowledgeList = knowledgeMapper.selectAll();
        return knowledgeList.stream().map(knowledge -> {
            KnowledgeResponse response = new KnowledgeResponse();
            BeanUtils.copyProperties(knowledge, response);
            return response;
        }).toList();
    }

    /**
     * 根据技术标签获取知识卡片
     * 查询包含指定技术标签的所有知识库条目
     *
     * @param tag 技术标签名称
     * @return 匹配该标签的知识卡片响应对象列表
     */
    public List<KnowledgeResponse> getKnowledgeByTechTag(String tag) {
        List<AiKnowledge> knowledgeList = knowledgeMapper.selectByTechTag(tag);
        return knowledgeList.stream().map(knowledge -> {
            KnowledgeResponse response = new KnowledgeResponse();
            BeanUtils.copyProperties(knowledge, response);
            return response;
        }).toList();
    }

    /**
     * 删除知识卡片
     * 根据ID删除指定的知识库条目
     *
     * @param id 要删除的知识卡片ID
     * @throws Exception 如果删除操作失败
     */
    public void deleteKnowledge(Long id) throws Exception {
        logger.info("删除知识库，ID: {}", id);
        try {
            int deleteResult = knowledgeMapper.deleteById(id);
            if (deleteResult != 1) {
                throw new Exception("Failed to delete knowledge");
            }
            logger.info("知识库删除成功，ID: {}", id);
        } catch (Exception e) {
            logger.error("删除知识库时发生错误: {}", e.getMessage(), e);
            throw new Exception("Error deleting knowledge: " + e.getMessage(), e);
        }
    }

    /**
     * 构建知识提炼提示词
     * 根据用户消息和助手回复构建AI知识提炼的prompt
     *
     * @param userContent 用户消息内容
     * @param assistantContent 助手回复内容
     * @return 构建好的提示词字符串
     */
    private String buildPrompt(String userContent, String assistantContent) {
        return "请把下面的用户提问和AI回答，提炼为关键语句和代码片段，并添加适当的标记。\n\n" +
                "【要求】\n\n" +
                "1. 标题：简洁明确，准确概括核心知识点，格式为：技术/框架 + 功能/实现（例如：Vue3实现防抖函数）\n" +
                "2. summary：提取最关键的语句，直接回答用户的问题，每个关键语句前添加[KEY]标记，多个关键语句用换行分隔\n" +
                "3. core_code：提取最核心的代码片段，去除冗余部分，保持代码的完整性和可执行性，代码片段前后添加[CODE]标记\n" +
                "4. tech_tags：技术标签数组，包含相关的技术栈、框架、库、工具等，确保标签全面且准确（例如：[\"Vue3\", \"JavaScript\", \"防抖\"]）\n" +
                "5. scene：简要描述使用场景，1-2句话即可\n" +
                "6. principle：简要说明原理，1-2句话即可\n" +
                "7. notes：简要说明注意事项，1-2点即可\n" +
                "8. reusable：是否可复用，根据代码的通用性和可移植性判断（true/false）\n\n" +
                "【质量要求】\n" +
                "- 聚焦于提取关键信息，避免冗余内容\n" +
                "- 关键语句要直接回答用户的问题，包含核心信息\n" +
                "- 代码片段要精简但完整，能体现核心逻辑\n" +
                "- 标记要清晰明了，便于识别和分类\n" +
                "- 要严格根据用户的原始问题提取相关知识，确保提炼结果与用户问题直接相关\n" +
                "- 如果用户问题涉及端口操作，请优先提取针对端口的命令，而不是进程操作命令\n\n" +
                "【必须返回JSON，不要任何解释】\n\n" +
                "【输入】\n" +
                "User:\n" + userContent + "\n\n" +
                "Assistant:\n" + assistantContent + "\n" +
                "你必须严格返回JSON格式，不允许输出任何额外文字。\n\n" +
                "如果无法提炼，请返回：\n" +
                "{\"valid\": false}" + "\n\n" +
                "否则返回：\n" +
                "{\"valid\": true, \"title\": \"\", \"summary\": \"\", \"core_code\": \"\", \"tech_tags\": [], \"scene\": \"\", \"principle\": \"\", \"notes\": \"\", \"reusable\": true}";
    }
}