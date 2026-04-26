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

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private OllamaService ollamaService;

    @GetMapping("/session/list")
    public Result<List<ChatSession>> getSessionList() {
        return Result.success(chatService.getSessionList());
    }

    @PostMapping("/session/create")
    public Result<ChatSession> createSession(@RequestBody java.util.Map<String, String> request) {
        ChatSession session = chatService.createSession(request.get("title"));
        return Result.success(session);
    }

    @DeleteMapping("/session/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        chatService.deleteSession(id);
        return Result.success(null);
    }

    @GetMapping("/message/{sessionId}")
    public Result<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return Result.success(chatService.getMessages(sessionId));
    }

    @GetMapping(value = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(@RequestParam Long sessionId, @RequestParam String content, @RequestParam String model, @RequestParam(required = false) String Authorization) {
        // 处理token
        if (Authorization != null && Authorization.startsWith("Bearer ")) {
            String token = Authorization.substring(7);
            // 手动验证token
            StpUtil.setTokenValue(token);
        }
        // 保存用户消息
        chatService.saveMessage(sessionId, "user", content);
        // 调用Ollama服务获取AI响应
        return ollamaService.chat(sessionId, content, model);
    }
}