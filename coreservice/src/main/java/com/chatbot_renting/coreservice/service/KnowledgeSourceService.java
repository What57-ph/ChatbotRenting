package com.chatbot_renting.coreservice.service;

import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface KnowledgeSourceService {

    KnowledgeSourceResponseDto createSource(UUID botId, UUID userId, KnowledgeSourceRequestWrapper request);

    KnowledgeSourceResponseDto getSource(UUID botId, UUID sourceId, UUID userId);

    Page<KnowledgeSourceResponseDto> getAllSources(UUID botId, UUID userId, Pageable pageable);

    void deleteSource(UUID botId, UUID sourceId, UUID userId);
}
