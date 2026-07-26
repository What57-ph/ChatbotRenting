package com.chatbot_renting.coreservice.service.impl;

import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.request.TextSourceDto;
import com.chatbot_renting.coreservice.dto.request.UrlSourceDto;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import com.chatbot_renting.coreservice.entity.*;
import com.chatbot_renting.coreservice.entity.enums.ProcessingStatus;
import com.chatbot_renting.coreservice.mapper.KnowledgeSourceMapper;
import com.chatbot_renting.coreservice.repository.KnowledgeSourceRepository;
import com.chatbot_renting.coreservice.service.ChatbotService;
import com.chatbot_renting.coreservice.service.KnowledgeSourceService;
import com.lecturemind.commonservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeSourceServiceImpl implements KnowledgeSourceService {

    private final KnowledgeSourceRepository sourceRepository;
    private final ChatbotService chatbotService;

    @Override
    @Transactional
    public KnowledgeSourceResponseDto createSource(UUID botId, UUID userId, KnowledgeSourceRequestWrapper request) {
        log.info("Starting createSource - botId={}, userId={}, sourceType={}", botId, userId, request.sourceType());
        try {
            Chatbot chatbot = chatbotService.findBotOrThrow(botId, userId);

            KnowledgeSource source = KnowledgeSource.builder()
                    .chatbot(chatbot)
                    .name(request.name())
                    .sourceType(request.sourceType())
                    .status(ProcessingStatus.CREATED)
                    .build();

            KnowledgeSource updatedSource = attachSpecificSourceData(source, request);
            KnowledgeSource saved = sourceRepository.save(updatedSource);

            log.info("Completed createSource - resultId={}", saved.getId());
            return KnowledgeSourceMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Error in createSource - botId={}, userId={}", botId, userId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public KnowledgeSourceResponseDto getSource(UUID botId, UUID sourceId, UUID userId) {
        log.info("Starting getSource - botId={}, sourceId={}, userId={}", botId, sourceId, userId);
        try {
            chatbotService.findBotOrThrow(botId, userId);
            KnowledgeSource source = sourceRepository.findByIdAndChatbotId(sourceId, botId)
                    .orElseThrow(() -> new ResourceNotFoundException("Knowledge source not found"));
            log.info("Completed getSource - resultId={}", source.getId());
            return KnowledgeSourceMapper.toDto(source);
        } catch (Exception e) {
            log.error("Error in getSource - botId={}, sourceId={}", botId, sourceId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeSourceResponseDto> getAllSources(UUID botId, UUID userId, Pageable pageable) {
        log.info("Starting getAllSources - botId={}, userId={}", botId, userId);
        try {
            chatbotService.findBotOrThrow(botId, userId);
            Page<KnowledgeSource> sources = sourceRepository.findAllByChatbotId(botId, pageable);
            Page<KnowledgeSourceResponseDto> dtos = sources.map(KnowledgeSourceMapper::toDto);
            log.info("Completed getAllSources - resultSize={}", dtos.getContent().size());
            return dtos;
        } catch (Exception e) {
            log.error("Error in getAllSources - botId={}, userId={}", botId, userId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteSource(UUID botId, UUID sourceId, UUID userId) {
        log.info("Starting deleteSource - botId={}, sourceId={}, userId={}", botId, sourceId, userId);
        try {
            chatbotService.findBotOrThrow(botId, userId);
            KnowledgeSource source = sourceRepository.findByIdAndChatbotId(sourceId, botId)
                    .orElseThrow(() -> new ResourceNotFoundException("Knowledge source not found"));

            source.setStatus(ProcessingStatus.DELETED);
            source.setDeletedAt(LocalDateTime.now());

            sourceRepository.save(source);
            log.info("Completed deleteSource - resultId={}", source.getId());
        } catch (Exception e) {
            log.error("Error in deleteSource - botId={}, sourceId={}", botId, sourceId, e);
            throw e;
        }
    }

    private KnowledgeSource attachSpecificSourceData(
            KnowledgeSource source,
            KnowledgeSourceRequestWrapper request
    ) {
        switch (request.sourceType()) {
            case FILE -> {
                List<SourceFile> files = request.files().stream()
                        .map(dto -> SourceFile.builder()
                                .knowledgeSource(source)
                                .fileName(dto.fileName())
                                .fileUrl(dto.fileUrl())
                                .fileType(dto.fileType())
                                .fileSize(dto.fileSize())
                                .build()
                        )
                        .toList();
                source.setSourceFiles(files);
            }
            case URL -> {
                UrlSourceDto dto = request.url();
                SourceUrl url = SourceUrl.builder()
                        .knowledgeSource(source)
                        .url(dto.url())
                        .build();
                source.setSourceUrl(url);
            }
            case TEXT -> {
                TextSourceDto dto = request.text();
                SourceText text = SourceText.builder()
                        .knowledgeSource(source)
                        .content(dto.content())
                        .build();
                source.setSourceText(text);
            }
        }
        return source;
    }
}
