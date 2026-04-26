package com.aitalking.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * Ollama AI服务接口
 * 提供与Ollama AI模型的交互功能
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public interface OllamaService {

    /**
     * 流式聊天
     *
     * @param sessionId 会话ID
     * @param content 聊天内容
     * @param model 模型名称
     * @return SSE事件发射器，用于实时推送AI响应
     */
    SseEmitter chat(Long sessionId, String content, String model);

    /**
     * 生成文本
     *
     * @param prompt 提示词
     * @return 生成的文本内容
     */
    Flux<String> generate(String prompt);

}