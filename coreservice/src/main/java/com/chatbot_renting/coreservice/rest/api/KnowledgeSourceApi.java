package com.chatbot_renting.coreservice.rest.api;

import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import com.chatbot_renting.commonservice.domain.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RequestMapping("/api/v1/chatbots/{botId}/sources")
public interface KnowledgeSourceApi {

    @PostMapping
    ResponseEntity<ApiResponse<KnowledgeSourceResponseDto>> createSource(
            @PathVariable("botId") UUID botId,
            @RequestBody KnowledgeSourceRequestWrapper request);

    @GetMapping("/{sourceId}")
    ResponseEntity<ApiResponse<KnowledgeSourceResponseDto>> getSource(
            @PathVariable("botId") UUID botId,
            @PathVariable("sourceId") UUID sourceId);

    @GetMapping
    ResponseEntity<ApiResponse<Page<KnowledgeSourceResponseDto>>> getAllSources(
            @PathVariable("botId") UUID botId,
            Pageable pageable);

    @DeleteMapping("/{sourceId}")
    ResponseEntity<ApiResponse<Void>> deleteSource(
            @PathVariable("botId") UUID botId,
            @PathVariable("sourceId") UUID sourceId);
}
