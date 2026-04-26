package com.aitalking.controller;

import com.aitalking.dto.Result;
import com.aitalking.model.ChatSession;
import com.aitalking.model.ChatMessage;
import com.aitalking.service.ChatService;
import com.aitalking.service.OllamaService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 聊天控制器
 * 处理聊天会话管理、消息收发等聊天相关请求
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private OllamaService ollamaService;

    /**
     * 获取聊天会话列表
     * 获取当前用户的所有聊天会话
     *
     * @return Result<List<ChatSession>> 返回聊天会话列表
     */
    @GetMapping("/session/list")
    public Result<List<ChatSession>> getSessionList() {
        return Result.success(chatService.getSessionList());
    }

    /**
     * 创建新聊天会话
     * 根据指定标题创建新的聊天会话
     *
     * @param request 包含会话标题的请求体
     * @return Result<ChatSession> 返回创建成功的新会话信息
     */
    @PostMapping("/session/create")
    public Result<ChatSession> createSession(@RequestBody java.util.Map<String, String> request) {
        ChatSession session = chatService.createSession(request.get("title"));
        return Result.success(session);
    }

    /**
     * 删除聊天会话
     * 根据会话ID删除指定的聊天会话
     *
     * @param id 要删除的会话ID
     * @return Result<Void> 返回删除操作结果
     */
    @DeleteMapping("/session/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        chatService.deleteSession(id);
        return Result.success(null);
    }

    /**
     * 获取会话消息列表
     * 根据会话ID获取该会话下的所有聊天消息
     *
     * @param sessionId 会话ID
     * @return Result<List<ChatMessage>> 返回该会话的消息列表
     */
    @GetMapping("/message/{sessionId}")
    public Result<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return Result.success(chatService.getMessages(sessionId));
    }

    /**
     * 发送消息并获取AI响应
     * 发送用户消息并通过SSE流式返回AI响应
     *
     * @param sessionId 会话ID
     * @param content 用户发送的消息内容
     * @param model 使用的AI模型名称
     * @param Authorization 可选的Bearer令牌，用于手动验证
     * @return SseEmitter 返回SSE事件发射器用于流式响应
     */
    @GetMapping(value = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(@RequestParam Long sessionId, @RequestParam String content, @RequestParam String model, @RequestParam(required = false) String Authorization) {
        if (Authorization != null && Authorization.startsWith("Bearer ")) {
            String token = Authorization.substring(7);
            StpUtil.setTokenValue(token);
        }
        chatService.saveMessage(sessionId, "user", content);
        return ollamaService.chat(sessionId, content, model);
    }
}