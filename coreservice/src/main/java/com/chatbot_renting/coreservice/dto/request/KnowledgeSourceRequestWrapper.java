package com.chatbot_renting.coreservice.dto.request;

import com.chatbot_renting.coreservice.domain.enums.KnowledgeSourceType;
import java.util.List;
public record KnowledgeSourceRequestWrapper(
        String name,
        KnowledgeSourceType sourceType,
        List<FileSourceDto> files,
        UrlSourceDto url,
        TextSourceDto text
) {}
