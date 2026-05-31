package com.chatbot_renting.coreservice.mapper;


import com.chatbot_renting.coreservice.domain.entity.Chatbot;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.dto.SituationDto;

import java.util.stream.Collectors;

public class ChatbotMapper {
    public static ChatbotResponseDto toDto(Chatbot chatbot) {
        var situations = chatbot.getSituations() != null ? 
            chatbot.getSituations().stream()
                .map(s -> new SituationDto(s.getId(), s.getName(), s.getInstruction()))
                .collect(Collectors.toList()) : null;
                
        return new ChatbotResponseDto(
                chatbot.getId(),
                chatbot.getName(),
                chatbot.getDescription(),
                chatbot.getAvatarUrl(),
                chatbot.getSystemPrompt(),
                chatbot.getStatus(),
                chatbot.getLanguage(),
                situations,
                chatbot.getCreatedAt(),
                chatbot.getUpdatedAt()
        );
    }
}
