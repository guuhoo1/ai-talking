package com.aitalking.controller;

import com.aitalking.dto.KnowledgeRequest;
import com.aitalking.dto.KnowledgeResponse;
import com.aitalking.dto.Result;
import com.aitalking.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @PostMapping("/extract")
    public Result<KnowledgeResponse> extractKnowledge(@RequestBody KnowledgeRequest request) {
        try {
            KnowledgeResponse response = knowledgeService.extractKnowledge(request.getMessageId());
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<KnowledgeResponse> getKnowledgeById(@PathVariable Long id) {
        KnowledgeResponse response = knowledgeService.getKnowledgeById(id);
        if (response == null) {
            return Result.fail(404, "Knowledge not found");
        }
        return Result.success(response);
    }

    @GetMapping("/all")
    public Result<List<KnowledgeResponse>> getAllKnowledge() {
        List<KnowledgeResponse> response = knowledgeService.getAllKnowledge();
        return Result.success(response);
    }

    @GetMapping("/by-tag/{tag}")
    public Result<List<KnowledgeResponse>> getKnowledgeByTechTag(@PathVariable String tag) {
        List<KnowledgeResponse> response = knowledgeService.getKnowledgeByTechTag(tag);
        return Result.success(response);
    }

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