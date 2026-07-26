package com.chatbot_renting.coreservice.service.impl;

import com.chatbot_renting.coreservice.dto.SituationDto;
import com.chatbot_renting.coreservice.dto.request.ChatbotRequestDto;

import com.chatbot_renting.coreservice.dto.response.ChatbotResponseDto;
import com.chatbot_renting.coreservice.entity.Chatbot;
import com.chatbot_renting.coreservice.entity.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.entity.enums.ChatbotStatus;
import com.chatbot_renting.coreservice.repository.ChatbotRepository;
import com.lecturemind.commonservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceImplTest {

    @Mock
    private ChatbotRepository chatbotRepository;

    @InjectMocks
    private ChatbotServiceImpl chatbotService;

    private Chatbot chatbot;
    private ChatbotRequestDto requestDto;
    private final UUID USER_ID = UUID.randomUUID();
    private final UUID BOT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        chatbot = Chatbot.builder()
                .userId(USER_ID)
                .name("Test Bot")
                .description("Test Desc")
                .systemPrompt("You are a helpful assistant")
                .language(ChatbotLanguage.VIETNAMESE)
                .status(ChatbotStatus.ACTIVE)
                .situations(new ArrayList<>())
                .build();
        // Since id is inherited from BaseEntity and no setter, we simulate it if needed or test without relying on generic inherited getter where possible, but for DTO mapping it expects an ID ideally.

        requestDto = new ChatbotRequestDto(
                "New Bot",
                "New Desc",
                "http://avatar.url",
                "Prompt",
                ChatbotLanguage.ENGLISH,
                Collections.singletonList(new SituationDto(UUID.randomUUID(),"Sit 1", "Inst 1"))
        );
    }

    @Test
    void createChatbot_ShouldReturnDto_WhenValidRequest() {
        when(chatbotRepository.save(any(Chatbot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatbotResponseDto response = chatbotService.createChatbot(USER_ID, requestDto);

        assertNotNull(response);
        assertEquals("New Bot", response.name());
        assertEquals(ChatbotLanguage.ENGLISH, response.language());

        ArgumentCaptor<Chatbot> captor = ArgumentCaptor.forClass(Chatbot.class);
        verify(chatbotRepository).save(captor.capture());
        Chatbot savedBot = captor.getValue();
        assertEquals(USER_ID, savedBot.getUserId());
        assertEquals(1, savedBot.getSituations().size());
        assertEquals("Sit 1", savedBot.getSituations().get(0).getName());
    }

    @Test
    void getChatbot_ShouldReturnDto_WhenFound() {
        when(chatbotRepository.findByIdAndUserId(BOT_ID, USER_ID)).thenReturn(Optional.of(chatbot));

        ChatbotResponseDto response = chatbotService.getChatbot(BOT_ID, USER_ID);

        assertNotNull(response);
        assertEquals("Test Bot", response.name());
        verify(chatbotRepository).findByIdAndUserId(BOT_ID, USER_ID);
    }

    @Test
    void getChatbot_ShouldThrowNotFound_WhenMissing() {
        when(chatbotRepository.findByIdAndUserId(BOT_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chatbotService.getChatbot(BOT_ID, USER_ID));
        verify(chatbotRepository).findByIdAndUserId(BOT_ID, USER_ID);
    }

    @Test
    void getAllChatbots_ShouldReturnPage_WhenDataExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Chatbot> page = new PageImpl<>(Collections.singletonList(chatbot));
        when(chatbotRepository.findAllByUserId(USER_ID, pageable)).thenReturn(page);

        Page<ChatbotResponseDto> result = chatbotService.getAllChatbots(USER_ID, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(chatbotRepository).findAllByUserId(USER_ID, pageable);
    }

    @Test
    void updateChatbot_ShouldReturnDto_WhenFound() {
        when(chatbotRepository.findByIdAndUserId(BOT_ID, USER_ID)).thenReturn(Optional.of(chatbot));
        when(chatbotRepository.save(any(Chatbot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatbotResponseDto response = chatbotService.updateChatbot(BOT_ID, USER_ID, requestDto);

        assertNotNull(response);
        assertEquals("New Bot", response.name());

        ArgumentCaptor<Chatbot> captor = ArgumentCaptor.forClass(Chatbot.class);
        verify(chatbotRepository).save(captor.capture());
        Chatbot savedBot = captor.getValue();
        assertEquals("New Bot", savedBot.getName());
        assertEquals(1, savedBot.getSituations().size());
    }

    @Test
    void deleteChatbot_ShouldSoftDelete_WhenFound() {
        when(chatbotRepository.findByIdAndUserId(BOT_ID, USER_ID)).thenReturn(Optional.of(chatbot));

        chatbotService.deleteChatbot(BOT_ID, USER_ID);

        ArgumentCaptor<Chatbot> captor = ArgumentCaptor.forClass(Chatbot.class);
        verify(chatbotRepository).save(captor.capture());
        Chatbot deletedBot = captor.getValue();
        assertEquals(ChatbotStatus.DELETED, deletedBot.getStatus());
        assertNotNull(deletedBot.getDeletedAt());
    }
}
