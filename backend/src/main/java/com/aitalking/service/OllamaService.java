package com.aitalking.service;

import com.aitalking.utils.OllamaClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final OllamaClient ollamaClient;
    private final ChatService chatService;

    @Value("${ollama.default-model}")
    private String defaultModel;

    @Autowired
    public OllamaService(ChatService chatService, @Value("${ollama.base-url}") String baseUrl) {
        this.chatService = chatService;
        this.ollamaClient = new OllamaClient(baseUrl);
    }

    public SseEmitter chat(Long sessionId, String content, String model) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // 使用默认模型如果未指定
        final String finalModel = (model == null || model.isEmpty()) ? defaultModel : model;

        // 获取会话历史
        final List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));

        // 添加历史消息
        chatService.getMessages(sessionId).forEach(msg -> {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        });

        // 添加当前用户消息
        messages.add(Map.of("role", "user", "content", content));

        // 异步处理AI响应
        new Thread(() -> {
            try {
                StringBuilder assistantResponse = new StringBuilder();
                ollamaClient.chat(finalModel, messages)
                        .subscribe(
                                response -> {
                                    try {
                                        JsonNode node = ollamaClient.parseResponse(response);
                                        if (node.has("message") && node.get("message").has("content")) {
                                            String chunk = node.get("message").get("content").asText();
                                            assistantResponse.append(chunk);
                                            // 发送SSE事件
                                            emitter.send(SseEmitter.event().data(chunk));
                                        }
                                        if (node.has("done") && node.get("done").asBoolean()) {
                                            // 保存AI响应
                                            chatService.saveMessageWithoutValidation(sessionId, "assistant", assistantResponse.toString());
                                            // 发送结束事件
                                            emitter.send(SseEmitter.event().data("[DONE]"));
                                            emitter.complete();
                                        }
                                    } catch (Exception e) {
                                        emitter.completeWithError(e);
                                    }
                                },
                                error -> {
                                    emitter.completeWithError(error);
                                },
                                () -> {
                                    // 流结束
                                }
                        );
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}