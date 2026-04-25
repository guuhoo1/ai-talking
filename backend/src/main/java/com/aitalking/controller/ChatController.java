package com.aitalking.controller;

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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private OllamaService ollamaService;

    @GetMapping("/session/list")
    public Map<String, Object> getSessionList() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取会话列表成功");
        result.put("data", chatService.getSessionList());
        return result;
    }

    @PostMapping("/session/create")
    public Map<String, Object> createSession(@RequestBody Map<String, String> request) {
        ChatSession session = chatService.createSession(request.get("title"));
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建会话成功");
        result.put("data", session);
        return result;
    }

    @DeleteMapping("/session/{id}")
    public Map<String, Object> deleteSession(@PathVariable Long id) {
        chatService.deleteSession(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除会话成功");
        return result;
    }

    @GetMapping("/message/{sessionId}")
    public Map<String, Object> getMessages(@PathVariable Long sessionId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取消息列表成功");
        result.put("data", chatService.getMessages(sessionId));
        return result;
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