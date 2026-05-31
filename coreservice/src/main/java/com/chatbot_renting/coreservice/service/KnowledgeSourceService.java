package com.chatbot_renting.coreservice.service;

import com.chatbot_renting.commonservice.exception.ResourceNotFoundException;
import com.chatbot_renting.coreservice.domain.entity.*;
import com.chatbot_renting.coreservice.domain.enums.ProcessingStatus;
import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.request.TextSourceDto;
import com.chatbot_renting.coreservice.dto.request.UrlSourceDto;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import com.chatbot_renting.coreservice.mapper.KnowledgeSourceMapper;
import com.chatbot_renting.coreservice.repository.KnowledgeSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeSourceService {

    private final KnowledgeSourceRepository sourceRepository;
    private final ChatbotService chatbotService;

    @Transactional
    public KnowledgeSourceResponseDto createSource(Long botId, Long userId, KnowledgeSourceRequestWrapper request) {
        Chatbot chatbot = chatbotService.findBotOrThrow(botId, userId);

        KnowledgeSource source = KnowledgeSource.builder()
                .chatbot(chatbot)
                .name(request.name())
                .sourceType(request.sourceType())
                .status(ProcessingStatus.CREATED)
                .build();

        attachSpecificSourceData(source, request);
        return KnowledgeSourceMapper.toDto(sourceRepository.save(source));
    }

    @Transactional(readOnly = true)
    public KnowledgeSourceResponseDto getSource(Long botId, Long sourceId, Long userId) {
        chatbotService.findBotOrThrow(botId, userId);
        KnowledgeSource source = sourceRepository.findByIdAndChatbotId(sourceId, botId)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge source not found"));
        return KnowledgeSourceMapper.toDto(source);
    }
    
    @Transactional(readOnly = true)
    public List<KnowledgeSourceResponseDto> getAllSources(Long botId, Long userId) {
        chatbotService.findBotOrThrow(botId, userId);
        return sourceRepository.findAllByChatbotId(botId).stream()
                .map(KnowledgeSourceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSource(Long botId, Long sourceId, Long userId) {
        chatbotService.findBotOrThrow(botId, userId);
        KnowledgeSource source = sourceRepository.findByIdAndChatbotId(sourceId, botId)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge source not found"));
                
        source.setStatus(ProcessingStatus.FAILED);
        source.setDeletedAt(java.time.LocalDateTime.now());
        sourceRepository.save(source);
    }

    private void attachSpecificSourceData(
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
    }
}
