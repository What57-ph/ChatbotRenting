package com.chatbot_renting.coreservice.rest.controller;

import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.rest.api.ChatbotApi;
import com.chatbot_renting.coreservice.service.ChatbotService;
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
public class ChatbotController implements ChatbotApi {

    private final ChatbotService chatbotService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<ApiResponse<ChatbotResponseDto>> createChatbot(ChatbotRequestDto request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to create Chatbot for user: {}", userId);
        ChatbotResponseDto response = chatbotService.createChatbot(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.created(response, "Chatbot created successfully"));
    }

    @Override
    public ResponseEntity<ApiResponse<ChatbotResponseDto>> getChatbot(UUID id) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to get Chatbot id: {} for user: {}", id, userId);
        ChatbotResponseDto response = chatbotService.getChatbot(id, userId);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @Override
    public ResponseEntity<ApiResponse<Page<ChatbotResponseDto>>> getAllChatbots(Pageable pageable) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to get all Chatbots for user: {}, page: {}", userId, pageable.getPageNumber());
        Page<ChatbotResponseDto> response = chatbotService.getAllChatbots(userId, pageable);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @Override
    public ResponseEntity<ApiResponse<ChatbotResponseDto>> updateChatbot(UUID id, ChatbotRequestDto request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to update Chatbot id: {} for user: {}", id, userId);
        ChatbotResponseDto response = chatbotService.updateChatbot(id, userId, request);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteChatbot(UUID id) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("REST request to delete Chatbot id: {} for user: {}", id, userId);
        chatbotService.deleteChatbot(id, userId);
        return ResponseEntity.ok(ResponseBuilder.success(null));
    }
}
