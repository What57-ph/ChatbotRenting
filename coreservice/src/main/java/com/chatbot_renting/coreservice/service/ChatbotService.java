package com.chatbot_renting.coreservice.service;


import com.chatbot_renting.commonservice.exception.ResourceNotFoundException;

import com.chatbot_renting.coreservice.domain.entity.Chatbot;
import com.chatbot_renting.coreservice.domain.entity.ChatbotSituation;
import com.chatbot_renting.coreservice.domain.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.domain.enums.ChatbotStatus;
import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;
import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.mapper.ChatbotMapper;
import com.chatbot_renting.coreservice.repository.ChatbotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotRepository chatbotRepository;

    @Transactional
    public ChatbotResponseDto createChatbot(Long userId, ChatbotRequestDto request) {
        Chatbot chatbot = Chatbot.builder()
                .userId(userId)
                .name(request.name())
                .description(request.description())
                .avatarUrl(request.avatarUrl())
                .systemPrompt(request.systemPrompt())
                .language(request.language() != null ? request.language() : ChatbotLanguage.VIETNAMESE)
                .status(ChatbotStatus.ACTIVE)
                .build();

        assignSituations(chatbot, request);
        return ChatbotMapper.toDto(chatbotRepository.save(chatbot));
    }

    @Transactional(readOnly = true)
    public ChatbotResponseDto getChatbot(Long botId, Long userId) {
        return ChatbotMapper.toDto(findBotOrThrow(botId, userId));
    }

    @Transactional
    public ChatbotResponseDto updateChatbot(Long botId, Long userId, ChatbotRequestDto request) {
        Chatbot chatbot = findBotOrThrow(botId, userId);
        
        chatbot.setName(request.name());
        chatbot.setDescription(request.description());
        chatbot.setAvatarUrl(request.avatarUrl());
        chatbot.setSystemPrompt(request.systemPrompt());
        if (request.language() != null) chatbot.setLanguage(request.language());
        
        assignSituations(chatbot, request);

        return ChatbotMapper.toDto(chatbotRepository.save(chatbot));
    }

    @Transactional
    public void deleteChatbot(Long botId, Long userId) {
        Chatbot chatbot = findBotOrThrow(botId, userId);
        chatbot.setStatus(ChatbotStatus.DELETED);
        chatbot.setDeletedAt(java.time.LocalDateTime.now());
        chatbotRepository.save(chatbot);
    }
    
    @Transactional(readOnly = true)
    public List<ChatbotResponseDto> getAllChatbots(Long userId) {
        return chatbotRepository.findAllByUserId(userId).stream()
                .map(ChatbotMapper::toDto)
                .collect(Collectors.toList());
    }

    private void assignSituations(Chatbot chatbot, ChatbotRequestDto request) {
        if (chatbot.getSituations() != null) {
            chatbot.getSituations().clear();
        }
        
        if (request.situations() != null) {
            List<ChatbotSituation> newSituations = request.situations().stream().map(dto ->
                ChatbotSituation.builder()
                        .chatbot(chatbot)
                        .name(dto.name())
                        .instruction(dto.instruction())
                        .build()
            ).toList();
            
            if (chatbot.getSituations() != null) {
                chatbot.getSituations().addAll(newSituations);
            } else {
                chatbot.setSituations(newSituations);
            }
        }
    }

    public Chatbot findBotOrThrow(Long botId, Long userId) {
        return chatbotRepository.findByIdAndUserId(botId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chatbot not found or unauthorized"));
    }
}
