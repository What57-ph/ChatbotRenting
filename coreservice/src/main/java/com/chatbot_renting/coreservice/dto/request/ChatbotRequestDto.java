package com.chatbot_renting.coreservice.dto.request;

import com.chatbot_renting.coreservice.domain.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.dto.SituationDto;

import java.util.List;

public record ChatbotRequestDto(
        String name,
        String description,
        String avatarUrl,
        String systemPrompt,
        ChatbotLanguage language,
        List<SituationDto> situations
) {
}
