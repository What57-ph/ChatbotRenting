package com.chatbot_renting.coreservice.rest.controller;

import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import com.chatbot_renting.coreservice.rest.api.KnowledgeSourceApi;
import com.chatbot_renting.coreservice.service.KnowledgeSourceService;
import com.chatbot_renting.coreservice.utils.SecurityUtils;
import com.chatbot_renting.commonservice.domain.Response.ApiResponse;
import com.chatbot_renting.commonservice.domain.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KnowledgeSourceController implements KnowledgeSourceApi {

    private final KnowledgeSourceService knowledgeSourceService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<ApiResponse<KnowledgeSourceResponseDto>> createSource(UUID botId, KnowledgeSourceRequestWrapper request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to create KnowledgeSource for botId: {}, userId: {}", botId, userId);
        KnowledgeSourceResponseDto response = knowledgeSourceService.createSource(botId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.created(response, "Knowledge Source created successfully"));
    }

    @Override
    public ResponseEntity<ApiResponse<KnowledgeSourceResponseDto>> getSource(UUID botId, UUID sourceId) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to get KnowledgeSource sourceId: {} for botId: {}, userId: {}", sourceId, botId, userId);
        KnowledgeSourceResponseDto response = knowledgeSourceService.getSource(botId, sourceId, userId);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @Override
    public ResponseEntity<ApiResponse<Page<KnowledgeSourceResponseDto>>> getAllSources(UUID botId, Pageable pageable) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to get all KnowledgeSources for botId: {}, userId: {}, page: {}", botId, userId, pageable.getPageNumber());
        Page<KnowledgeSourceResponseDto> response = knowledgeSourceService.getAllSources(botId, userId, pageable);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSource(UUID botId, UUID sourceId) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to delete KnowledgeSource sourceId: {} for botId: {}, userId: {}", sourceId, botId, userId);
        knowledgeSourceService.deleteSource(botId, sourceId, userId);
        return ResponseEntity.ok(ResponseBuilder.success(null));
    }
}
