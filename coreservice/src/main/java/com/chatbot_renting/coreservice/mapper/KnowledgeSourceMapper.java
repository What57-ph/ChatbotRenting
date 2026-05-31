package com.chatbot_renting.coreservice.mapper;

import com.chatbot_renting.coreservice.domain.entity.KnowledgeSource;
import com.chatbot_renting.coreservice.domain.entity.SourceFile;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import java.util.List;
public class KnowledgeSourceMapper {
    public static KnowledgeSourceResponseDto toDto(KnowledgeSource source) {
        List<String> fileUrls = source.getSourceFiles() != null
                ? source.getSourceFiles()
                .stream()
                .map(SourceFile::getFileUrl)
                .toList()
                : List.of();
        String url = source.getSourceUrl() != null ? source.getSourceUrl().getUrl() : null;
        String content = source.getSourceText() != null ? source.getSourceText().getContent() : null;
        
        return new KnowledgeSourceResponseDto(
                source.getId(),
                source.getChatbot().getId(),
                source.getName(),
                source.getSourceType(),
                source.getStatus(),
                fileUrls,
                url,
                content,
                source.getCreatedAt(),
                source.getUpdatedAt()
        );
    }
}
