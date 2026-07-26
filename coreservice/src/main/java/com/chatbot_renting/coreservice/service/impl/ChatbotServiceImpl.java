package com.chatbot_renting.coreservice.service.impl;

import com.chatbot_renting.coreservice.entity.Chatbot;
import com.chatbot_renting.coreservice.entity.ChatbotSituation;
import com.chatbot_renting.coreservice.entity.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.entity.enums.ChatbotStatus;
import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.mapper.ChatbotMapper;
import com.chatbot_renting.coreservice.repository.ChatbotRepository;
import com.chatbot_renting.coreservice.service.ChatbotService;
import com.lecturemind.commonservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatbotRepository chatbotRepository;

    @Override
    @Transactional
    public ChatbotResponseDto createChatbot(UUID userId, ChatbotRequestDto request) {
        log.info("Starting createChatbot - userId={}, name={}", userId, request.name());
        try {
            Chatbot chatbot = Chatbot.builder()
                    .userId(userId)
                    .name(request.name())
                    .description(request.description())
                    .avatarUrl(request.avatarUrl())
                    .systemPrompt(request.systemPrompt())
                    .language(request.language() != null ? request.language() : ChatbotLanguage.VIETNAMESE)
                    .status(ChatbotStatus.ACTIVE)
                    .situations(new ArrayList<>())
                    .build();

            chatbot = assignSituations(chatbot, request);
            Chatbot saved = chatbotRepository.save(chatbot);
            log.info("Completed createChatbot - resultId={}", saved.getId());
            return ChatbotMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Error in createChatbot - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChatbotResponseDto getChatbot(UUID botId, UUID userId) {
        log.info("Starting getChatbot - botId={}, userId={}", botId, userId);
        try {
            Chatbot chatbot = findBotOrThrow(botId, userId);
            log.info("Completed getChatbot - resultId={}", chatbot.getId());
            return ChatbotMapper.toDto(chatbot);
        } catch (Exception e) {
            log.error("Error in getChatbot - botId={}, userId={}", botId, userId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatbotResponseDto> getAllChatbots(UUID userId, Pageable pageable) {
        log.info("Starting getAllChatbots - userId={}", userId);
        try {
            Page<Chatbot> chatbots = chatbotRepository.findAllByUserId(userId, pageable);
            Page<ChatbotResponseDto> dtos = chatbots.map(ChatbotMapper::toDto);
            log.info("Completed getAllChatbots - resultSize={}", dtos.getContent().size());
            return dtos;
        } catch (Exception e) {
            log.error("Error in getAllChatbots - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ChatbotResponseDto updateChatbot(UUID botId, UUID userId, ChatbotRequestDto request) {
        log.info("Starting updateChatbot - botId={}, userId={}", botId, userId);
        try {
            Chatbot chatbot = findBotOrThrow(botId, userId);

            chatbot.setName(request.name());
            chatbot.setDescription(request.description());
            chatbot.setAvatarUrl(request.avatarUrl());
            chatbot.setSystemPrompt(request.systemPrompt());
            if (request.language() != null) {
                chatbot.setLanguage(request.language());
            }

            chatbot = assignSituations(chatbot, request);
            Chatbot saved = chatbotRepository.save(chatbot);
            log.info("Completed updateChatbot - resultId={}", saved.getId());
            return ChatbotMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Error in updateChatbot - botId={}, userId={}", botId, userId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteChatbot(UUID botId, UUID userId) {
        log.info("Starting deleteChatbot - botId={}, userId={}", botId, userId);
        try {
            Chatbot chatbot = findBotOrThrow(botId, userId);

            chatbot.setStatus(ChatbotStatus.DELETED);
            chatbot.setDeletedAt(LocalDateTime.now());

            chatbotRepository.save(chatbot);
            log.info("Completed deleteChatbot - resultId={}", chatbot.getId());
        } catch (Exception e) {
            log.error("Error in deleteChatbot - botId={}, userId={}", botId, userId, e);
            throw e;
        }
    }

    @Override
    public Chatbot findBotOrThrow(UUID botId, UUID userId) {
        return chatbotRepository.findByIdAndUserId(botId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chatbot not found or unauthorized"));
    }

    private Chatbot assignSituations(Chatbot chatbot, ChatbotRequestDto request) {
        if (chatbot.getSituations() != null) {
            chatbot.getSituations().clear();
        } else {
            chatbot.setSituations(new ArrayList<>());
        }

        if (request.situations() != null) {
            final Chatbot finalChatbot = chatbot;
            List<ChatbotSituation> newSituations = request.situations().stream().map(dto ->
                    ChatbotSituation.builder()
                            .chatbot(finalChatbot)
                            .name(dto.name())
                            .instruction(dto.instruction())
                            .build()
            ).toList();

            chatbot.getSituations().addAll(newSituations);
        }
        return chatbot;
    }
}
