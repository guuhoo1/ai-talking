package com.aitalking.controller;

import com.aitalking.dto.KnowledgeRequest;
import com.aitalking.dto.KnowledgeResponse;
import com.aitalking.dto.Result;
import com.aitalking.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库控制器
 * 处理知识提取、查询、删除等知识库相关请求
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * 从聊天消息中提取知识
     * 根据消息ID提取并保存知识条目
     *
     * @param request 包含消息ID的知识请求体
     * @return Result<KnowledgeResponse> 返回提取的知识内容
     */
    @PostMapping("/extract")
    public Result<KnowledgeResponse> extractKnowledge(@RequestBody KnowledgeRequest request) {
        try {
            KnowledgeResponse response = knowledgeService.extractKnowledge(request.getMessageId());
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据ID获取知识详情
     * 根据知识条目ID获取完整的知识信息
     *
     * @param id 知识条目ID
     * @return Result<KnowledgeResponse> 返回知识详情，失败时返回404错误
     */
    @GetMapping("/{id}")
    public Result<KnowledgeResponse> getKnowledgeById(@PathVariable Long id) {
        KnowledgeResponse response = knowledgeService.getKnowledgeById(id);
        if (response == null) {
            return Result.fail(404, "Knowledge not found");
        }
        return Result.success(response);
    }

    /**
     * 获取所有知识列表
     * 获取知识库中的所有知识条目
     *
     * @return Result<List<KnowledgeResponse>> 返回所有知识条目列表
     */
    @GetMapping("/all")
    public Result<List<KnowledgeResponse>> getAllKnowledge() {
        List<KnowledgeResponse> response = knowledgeService.getAllKnowledge();
        return Result.success(response);
    }

    /**
     * 根据技术标签获取知识列表
     * 筛选指定技术标签的所有知识条目
     *
     * @param tag 技术标签名称
     * @return Result<List<KnowledgeResponse>> 返回匹配标签的知识列表
     */
    @GetMapping("/by-tag/{tag}")
    public Result<List<KnowledgeResponse>> getKnowledgeByTechTag(@PathVariable String tag) {
        List<KnowledgeResponse> response = knowledgeService.getKnowledgeByTechTag(tag);
        return Result.success(response);
    }

    /**
     * 删除知识条目
     * 根据ID删除指定的知识条目
     *
     * @param id 要删除的知识条目ID
     * @return Result<Void> 返回删除操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledge(@PathVariable Long id) {
        try {
            knowledgeService.deleteKnowledge(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}