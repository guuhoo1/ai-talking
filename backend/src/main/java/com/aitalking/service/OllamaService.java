package com.aitalking.service;

import com.aitalking.utils.OllamaClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

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

    public String generate(String prompt) {
        logger.info("开始生成AI响应，使用模型: {}", defaultModel);
        // 使用默认模型
        final String finalModel = defaultModel;

        // 构建消息列表
        final List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
        messages.add(Map.of("role", "user", "content", prompt));

        // 同步处理AI响应
        StringBuilder responseBuilder = new StringBuilder();
        try {
            // 使用CountDownLatch等待响应完成
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.atomic.AtomicReference<Exception> errorRef = new java.util.concurrent.atomic.AtomicReference<>();


            logger.info("发送请求到Ollama API");
//            打印内容
            logger.info("请求内容-> {}",messages);
            ollamaClient.chat(finalModel, messages)
                    .subscribe(
                            response -> {
                                try {
                                    JsonNode node = ollamaClient.parseResponse(response);
                                    if (node.has("message") && node.get("message").has("content")) {
                                        String chunk = node.get("message").get("content").asText();
                                        responseBuilder.append(chunk);
                                    }
                                    if (node.has("done") && node.get("done").asBoolean()) {
                                        logger.info("AI响应完成");
                                        latch.countDown();
                                    }
                                } catch (Exception e) {
                                    logger.error("解析响应时发生错误: {}", e.getMessage());
                                    errorRef.set(e);
                                    latch.countDown();
                                }
                            },
                            error -> {
                                logger.error("AI响应流发生错误: {}", error.getMessage());
                                errorRef.set(new Exception(error));
                                latch.countDown();
                            },
                            () -> {
                                // 流结束
                            }
                    );

            // 等待响应完成，最多120秒（根据实际情况调整）
            logger.info("等待AI响应，最多120秒");
            boolean completed = latch.await(120, java.util.concurrent.TimeUnit.SECONDS);
            if (!completed) {
                logger.error("AI响应超时");
                throw new Exception("AI response timed out");
            }

            // 检查是否有错误
            if (errorRef.get() != null) {
                logger.error("AI响应发生错误: {}", errorRef.get().getMessage());
                throw errorRef.get();
            }

            // 检查响应是否为空
            if (responseBuilder.length() == 0) {
                logger.error("AI返回空响应");
                throw new Exception("AI returned empty response");
            }

            logger.info("AI响应生成成功，长度: {}", responseBuilder.length());
        } catch (Exception e) {
            logger.error("生成AI响应时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate AI response: " + e.getMessage(), e);
        }

        return responseBuilder.toString();
    }
}