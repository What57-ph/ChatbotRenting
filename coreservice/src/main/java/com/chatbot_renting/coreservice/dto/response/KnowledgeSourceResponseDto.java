package com.chatbot_renting.coreservice.dto.response;

import com.chatbot_renting.coreservice.domain.enums.KnowledgeSourceType;
import com.chatbot_renting.coreservice.domain.enums.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.List;
public record KnowledgeSourceResponseDto(
        Long id,
        Long chatbotId,
        String name,
        KnowledgeSourceType sourceType,
        ProcessingStatus status,
        List<String> fileUrl,
        String url,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
