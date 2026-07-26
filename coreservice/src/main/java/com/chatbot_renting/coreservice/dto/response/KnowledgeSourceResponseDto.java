package com.chatbot_renting.coreservice.dto.response;

import com.chatbot_renting.coreservice.entity.enums.KnowledgeSourceType;
import com.chatbot_renting.coreservice.entity.enums.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record KnowledgeSourceResponseDto(
        UUID id,
        UUID chatbotId,
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
