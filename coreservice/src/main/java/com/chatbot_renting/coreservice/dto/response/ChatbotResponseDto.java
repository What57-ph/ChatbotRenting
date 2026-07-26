package com.chatbot_renting.coreservice.dto.response;

import com.chatbot_renting.coreservice.entity.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.entity.enums.ChatbotStatus;
import com.chatbot_renting.coreservice.dto.SituationDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChatbotResponseDto(
        UUID id,
        String name,
        String description,
        String avatarUrl,
        String systemPrompt,
        ChatbotStatus status,
        ChatbotLanguage language,
        List<SituationDto> situations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
