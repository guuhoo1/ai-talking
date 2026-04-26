package com.aitalking.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Ollama API 客户端工具类
 * 提供与 Ollama 大语言模型服务进行交互的方法，支持流式聊天和响应解析
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class OllamaClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * 构造方法，初始化 WebClient 和 ObjectMapper
     *
     * @param baseUrl Ollama 服务的 base URL 地址，例如：http://localhost:11434
     */
    public OllamaClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 向 Ollama 服务发送聊天请求（流式响应）
     *
     * @param model   使用的模型名称，例如：llama3、qwen 等
     * @param messages 消息列表，每个消息为 Map 结构，包含 role 和 content 字段
     * @return Flux<String> 流式响应数据，每个元素为 JSON 格式的响应片段
     */
    public Flux<String> chat(String model, List<Map<String, String>> messages) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", messages,
                "stream", true
        );

        return webClient.post()
                .uri("/chat")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);
    }

    /**
     * 解析 JSON 格式的响应字符串
     *
     * @param response JSON 格式的响应字符串
     * @return JsonNode 解析后的 JSON 节点对象
     * @throws Exception 如果 JSON 解析失败则抛出异常
     */
    public JsonNode parseResponse(String response) throws Exception {
        return objectMapper.readTree(response);
    }
}