package com.chatbot_renting.coreservice.controller;

import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.service.ChatbotService;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbots")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatbotResponseDto>> createChatbot(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ChatbotRequestDto request) {
        ChatbotResponseDto response = chatbotService.createChatbot(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.created(response, "Chatbot created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatbotResponseDto>> getChatbot(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        ChatbotResponseDto response = chatbotService.getChatbot(id, userId);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatbotResponseDto>>> getAllChatbots(
            @RequestHeader("X-User-Id") Long userId) {
        List<ChatbotResponseDto> response = chatbotService.getAllChatbots(userId);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatbotResponseDto>> updateChatbot(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestBody ChatbotRequestDto request) {
        ChatbotResponseDto response = chatbotService.updateChatbot(id, userId, request);
        return ResponseEntity.ok(ResponseBuilder.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChatbot(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        chatbotService.deleteChatbot(id, userId);
        return ResponseEntity.ok(ResponseBuilder.success(null));
    }
}
