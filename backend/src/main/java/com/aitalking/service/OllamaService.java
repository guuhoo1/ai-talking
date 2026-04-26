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

/**
 * Ollama AI服务类
 * 提供与Ollama AI模型的交互功能，包括聊天和文本生成
 * 支持SSE实时流式响应和同步阻塞式响应两种模式
 *
 * @author AI Talking
 * @date 2026-04-26
 */
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

    /**
     * 聊天功能（流式响应）
     * 通过SSE实时流式返回AI响应，适用于前端需要实时展示打字效果的场景
     * 获取会话历史消息作为上下文，异步处理AI响应并实时推送
     *
     * @param sessionId 会话ID，用于获取历史消息上下文
     * @param content 用户输入的消息内容
     * @param model 使用的AI模型名称，如果为null或空则使用默认模型
     * @return SseEmitter对象，用于SSE实时通信，超时时间5分钟
     */
    public SseEmitter chat(Long sessionId, String content, String model) {
        SseEmitter emitter = new SseEmitter(300000L);

        final String finalModel = (model == null || model.isEmpty()) ? defaultModel : model;

        final List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));

        chatService.getMessages(sessionId).forEach(msg -> {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        });

        messages.add(Map.of("role", "user", "content", content));

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
                                            emitter.send(SseEmitter.event().data(chunk));
                                        }
                                        if (node.has("done") && node.get("done").asBoolean()) {
                                            chatService.saveMessageWithoutValidation(sessionId, "assistant", assistantResponse.toString());
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
                                }
                        );
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    /**
     * 文本生成功能（同步阻塞式响应）
     * 同步调用AI模型生成文本响应，适用于不需要实时流式输出的场景
     * 使用默认模型，等待AI完整响应后返回
     *
     * @param prompt 输入的提示词或问题
     * @return AI生成的完整文本响应
     * @throws RuntimeException 如果AI响应超时、发生错误或返回空响应
     */
    public String generate(String prompt) {
        logger.info("开始生成AI响应，使用模型: {}", defaultModel);
        final String finalModel = defaultModel;

        final List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
        messages.add(Map.of("role", "user", "content", prompt));

        StringBuilder responseBuilder = new StringBuilder();
        try {
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.atomic.AtomicReference<Exception> errorRef = new java.util.concurrent.atomic.AtomicReference<>();


            logger.info("发送请求到Ollama API");
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
                            }
                    );

            logger.info("等待AI响应，最多120秒");
            boolean completed = latch.await(120, java.util.concurrent.TimeUnit.SECONDS);
            if (!completed) {
                logger.error("AI响应超时");
                throw new Exception("AI response timed out");
            }

            if (errorRef.get() != null) {
                logger.error("AI响应发生错误: {}", errorRef.get().getMessage());
                throw errorRef.get();
            }

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