package com.chatbot_renting.coreservice.dto.request;


import com.chatbot_renting.coreservice.domain.enums.KnowledgeSourceType;
import java.util.List;
public record KnowledgeSourceRequestDto(
        String name,
        KnowledgeSourceType sourceType,
        List<String> fileUrl,
        String fileName,
        String fileType,
        Long fileSize,
        String url,
        String content
) {
}
