package com.chatbot_renting.coreservice.controller;

import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestDto;
import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import com.chatbot_renting.coreservice.service.KnowledgeSourceService;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbots/{botId}/sources")
@RequiredArgsConstructor
public class KnowledgeSourceController {

    private final KnowledgeSourceService knowledgeSourceService;

    @PostMapping
    public ResponseEntity<ApiResponse<KnowledgeSourceResponseDto>> createSource(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long botId,
            @RequestBody KnowledgeSourceRequestWrapper request) {
        KnowledgeSourceResponseDto response = knowledgeSourceService.createSource(botId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.created(response, "Knowledge Source created successfully"));
    }

    @GetMapping("/{sourceId}")
    public ResponseEntity<ApiResponse<KnowledgeSourceResponseDto>> getSource(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long botId,
            @PathVariable Long sourceId) {
        KnowledgeSourceResponseDto response = knowledgeSourceService.getSource(botId, sourceId, userId);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KnowledgeSourceResponseDto>>> getAllSources(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long botId) {
        List<KnowledgeSourceResponseDto> response = knowledgeSourceService.getAllSources(botId, userId);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @DeleteMapping("/{sourceId}")
    public ResponseEntity<ApiResponse<Void>> deleteSource(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long botId,
            @PathVariable Long sourceId) {
        knowledgeSourceService.deleteSource(botId, sourceId, userId);
        return ResponseEntity.ok(ResponseBuilder.success(null));
    }
}
