package com.chatbot_renting.coreservice.dto.response;

import com.chatbot_renting.coreservice.domain.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.domain.enums.ChatbotStatus;
import com.chatbot_renting.coreservice.dto.SituationDto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatbotResponseDto(
        Long id,
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
