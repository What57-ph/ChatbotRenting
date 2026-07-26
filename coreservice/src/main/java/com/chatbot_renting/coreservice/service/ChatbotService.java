package com.chatbot_renting.coreservice.service;

import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.entity.Chatbot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChatbotService {

    ChatbotResponseDto createChatbot(UUID userId, ChatbotRequestDto request);

    ChatbotResponseDto getChatbot(UUID botId, UUID userId);

    Page<ChatbotResponseDto> getAllChatbots(UUID userId, Pageable pageable);

    ChatbotResponseDto updateChatbot(UUID botId, UUID userId, ChatbotRequestDto request);

    void deleteChatbot(UUID botId, UUID userId);

    Chatbot findBotOrThrow(UUID botId, UUID userId);
}
