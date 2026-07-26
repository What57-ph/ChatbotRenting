package com.chatbot_renting.coreservice.rest.api;

import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.commonservice.domain.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RequestMapping("/api/v1/chatbots")
public interface ChatbotApi {

    @PostMapping
    ResponseEntity<ApiResponse<ChatbotResponseDto>> createChatbot(@RequestBody ChatbotRequestDto request);

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ChatbotResponseDto>> getChatbot(@PathVariable("id") UUID id);

    @GetMapping
    ResponseEntity<ApiResponse<Page<ChatbotResponseDto>>> getAllChatbots(Pageable pageable);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<ChatbotResponseDto>> updateChatbot(
            @PathVariable("id") UUID id,
            @RequestBody ChatbotRequestDto request);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteChatbot(@PathVariable("id") UUID id);
}
