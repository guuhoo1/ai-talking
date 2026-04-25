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

    public KnowledgeResponse extractKnowledge(Long messageId) throws Exception {
        logger.info("开始提取知识库，消息ID: {}", messageId);
        try {
            // 获取当前assistant消息
            ChatMessage assistantMessage = messageMapper.selectById(messageId);
            if (assistantMessage == null || !"assistant".equals(assistantMessage.getRole())) {
                logger.warn("无效的消息ID或非assistant消息: {}", messageId);
                throw new Exception("Invalid message ID or not an assistant message");
            }

            // 获取上一条user消息
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("sessionId", assistantMessage.getSessionId());
            params.put("createTime", assistantMessage.getCreateTime());
            ChatMessage userMessage = messageMapper.selectPreviousUserMessage(params);
            if (userMessage == null) {
                logger.warn("未找到上一条用户消息，会话ID: {}", assistantMessage.getSessionId());
                throw new Exception("No previous user message found");
            }

            logger.info("获取到用户消息和助手消息，开始构建提示词");
            // 构建AI提示
            String prompt = buildPrompt(userMessage.getContent(), assistantMessage.getContent());

            // 调用AI进行总结
            logger.info("调用AI模型进行知识提炼");
            String aiResponse = ollamaService.generate(prompt);
            logger.info("AI响应获取成功，开始解析");

            // 解析AI响应
            JsonNode aiResponseNode = objectMapper.readTree(aiResponse);
            if (!aiResponseNode.has("valid") || !aiResponseNode.get("valid").asBoolean()) {
                logger.warn("AI提炼失败: {}", aiResponse);
                throw new Exception("AI failed to extract knowledge");
            }

            // 验证必要字段
            if (!aiResponseNode.has("title") || !aiResponseNode.has("summary") || !aiResponseNode.has("tech_tags") || !aiResponseNode.has("reusable")) {
                logger.warn("AI响应缺少必要字段: {}", aiResponse);
                throw new Exception("AI response missing required fields");
            }

            // 创建知识卡片
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

            // 保存到数据库
            int insertResult = knowledgeMapper.insert(knowledge);
            if (insertResult != 1) {
                logger.error("保存知识卡片失败，影响行数: {}", insertResult);
                throw new Exception("Failed to save knowledge to database");
            }

            logger.info("知识卡片保存成功，ID: {}", knowledge.getId());

            // 转换为响应对象
            KnowledgeResponse response = new KnowledgeResponse();
            BeanUtils.copyProperties(knowledge, response);

            logger.info("知识库提取完成，返回响应");
            return response;
        } catch (Exception e) {
            logger.error("提取知识库时发生错误: {}", e.getMessage(), e);
            // 重新抛出异常，确保上层能够捕获
            throw new Exception("Error extracting knowledge: " + e.getMessage(), e);
        }
    }

    public KnowledgeResponse getKnowledgeById(Long id) {
        AiKnowledge knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            return null;
        }

        KnowledgeResponse response = new KnowledgeResponse();
        BeanUtils.copyProperties(knowledge, response);
        return response;
    }

    public List<KnowledgeResponse> getAllKnowledge() {
        List<AiKnowledge> knowledgeList = knowledgeMapper.selectAll();
        return knowledgeList.stream().map(knowledge -> {
            KnowledgeResponse response = new KnowledgeResponse();
            BeanUtils.copyProperties(knowledge, response);
            return response;
        }).toList();
    }

    public List<KnowledgeResponse> getKnowledgeByTechTag(String tag) {
        List<AiKnowledge> knowledgeList = knowledgeMapper.selectByTechTag(tag);
        return knowledgeList.stream().map(knowledge -> {
            KnowledgeResponse response = new KnowledgeResponse();
            BeanUtils.copyProperties(knowledge, response);
            return response;
        }).toList();
    }

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

    private String buildPrompt(String userContent, String assistantContent) {
        return "请把下面的用户提问和AI回答，提炼为一条\"可复用的代码知识卡片\"。\n\n" +
                "【要求】\n\n" +
                "1. 标题：简洁明确，准确概括核心知识点，格式为：技术/框架 + 功能/实现（例如：Vue3实现防抖函数）\n" +
                "2. summary：详细说明该知识的核心作用、价值和应用场景，至少2-3句话，全面且准确\n" +
                "3. core_code：提取最关键、最核心的代码片段，去除冗余部分，但保持代码的完整性和可执行性。对于命令行命令，请提取完整的命令及其参数说明；对于代码示例，请提取包含核心逻辑的完整代码块。\n" +
                "4. tech_tags：技术标签数组，包含相关的技术栈、框架、库、工具等，确保标签全面且准确（例如：[\"Vue3\", \"JavaScript\", \"防抖\", \"性能优化\"]）\n" +
                "5. scene：详细描述使用场景，包括具体的应用场景、解决的问题、适用条件等，至少2-3句话\n" +
                "6. principle：原理说明，解释该知识的工作原理、核心概念或实现机制，至少2-3句话\n" +
                "7. notes：注意事项，包括使用时需要注意的问题、限制条件、潜在风险等，至少2-3点\n" +
                "8. reusable：是否可复用，根据代码的通用性和可移植性判断（true/false）\n\n" +
                "【质量要求】\n" +
                "- 提取的知识要具有通用性和可复用性\n" +
                "- 代码片段要精简但完整，能体现核心逻辑，包含必要的注释\n" +
                "- 技术标签要全面准确，反映涉及的所有相关技术\n" +
                "- 使用场景要具体明确，包含实际应用示例\n" +
                "- 原理说明要清晰易懂，解释核心概念和工作机制\n" +
                "- 注意事项要全面，涵盖使用过程中可能遇到的问题\n" +
                "- 对于命令行相关的知识，必须在core_code中包含完整的命令及其使用说明\n" +
                "- 要严格根据用户的原始问题提取相关知识，确保知识卡片的内容与用户问题直接相关\n" +
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