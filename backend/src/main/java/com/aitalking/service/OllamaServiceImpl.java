package com.aitalking.service;

import com.aitalking.utils.OllamaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ollama AI服务实现类
 * 提供与Ollama AI模型的交互功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Service
public class OllamaServiceImpl implements OllamaService {

    @Autowired
    private ChatService chatService;

    private OllamaClient ollamaClient = new OllamaClient("http://localhost:11434");

    /**
     * 流式聊天
     *
     * @param sessionId 会话ID
     * @param content 聊天内容
     * @param model 模型名称
     * @return SSE事件发射器，用于实时推送AI响应
     */
    @Override
    public SseEmitter chat(Long sessionId, String content, String model) {
        SseEmitter emitter = new SseEmitter(120000L); // 2分钟超时

        Thread thread = new Thread(() -> {
            try {
                // 构建聊天消息
                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> userMessage = new HashMap<>();
                userMessage.put("role", "user");
                userMessage.put("content", content);
                messages.add(userMessage);

                // 调用Ollama API
                String response = String.valueOf(ollamaClient.chat(model, messages));

                // 解析响应
                String contentPart = response;
                if (contentPart != null) {
                    // 发送消息
                    emitter.send(contentPart);
                    // 保存AI响应
                    chatService.saveMessageWithoutValidation(sessionId, "assistant", contentPart);
                }

                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        thread.start();
        return emitter;
    }

    /**
     * 生成文本
     *
     * @param prompt 提示词
     * @return 生成的文本内容
     */
    @Override
    public Flux<String> generate(String prompt) {
        try {
            // 构建消息
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            // 调用Ollama API
            return ollamaClient.chat("qwen2.5:3b", messages);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AI response: " + e.getMessage());
        }
    }

}