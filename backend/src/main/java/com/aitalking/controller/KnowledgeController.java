package com.aitalking.controller;

import com.aitalking.dto.ApiResponse;
import com.aitalking.dto.KnowledgeRequest;
import com.aitalking.dto.KnowledgeResponse;
import com.aitalking.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @PostMapping("/extract")
    public ResponseEntity<?> extractKnowledge(@RequestBody KnowledgeRequest request) {
        try {
            KnowledgeResponse response = knowledgeService.extractKnowledge(request.getMessageId());
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error extracting knowledge: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getKnowledgeById(@PathVariable Long id) {
        KnowledgeResponse response = knowledgeService.getKnowledgeById(id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Knowledge not found");
        }
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<KnowledgeResponse>>> getAllKnowledge() {
        List<KnowledgeResponse> response = knowledgeService.getAllKnowledge();
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/by-tag/{tag}")
    public ResponseEntity<ApiResponse<List<KnowledgeResponse>>> getKnowledgeByTechTag(@PathVariable String tag) {
        List<KnowledgeResponse> response = knowledgeService.getKnowledgeByTechTag(tag);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKnowledge(@PathVariable Long id) {
        try {
            knowledgeService.deleteKnowledge(id);
            return ResponseEntity.ok(new ApiResponse<>("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting knowledge: " + e.getMessage());
        }
    }
}