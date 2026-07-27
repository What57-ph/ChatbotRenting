package com.chatbot_renting.coreservice.service.impl;

import com.chatbot_renting.coreservice.dto.request.KnowledgeSourceRequestWrapper;
import com.chatbot_renting.coreservice.dto.request.TextSourceDto;
import com.chatbot_renting.coreservice.dto.response.KnowledgeSourceResponseDto;
import com.chatbot_renting.coreservice.entity.Chatbot;
import com.chatbot_renting.coreservice.entity.KnowledgeSource;
import com.chatbot_renting.coreservice.entity.enums.KnowledgeSourceType;
import com.chatbot_renting.coreservice.entity.enums.ProcessingStatus;
import com.chatbot_renting.coreservice.repository.KnowledgeSourceRepository;
import com.chatbot_renting.coreservice.service.ChatbotService;
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
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeSourceServiceImplTest {

    @Mock
    private KnowledgeSourceRepository sourceRepository;

    @Mock
    private ChatbotService chatbotService;

    @InjectMocks
    private KnowledgeSourceServiceImpl sourceService;

    private Chatbot chatbot;
    private KnowledgeSource knowledgeSource;
    private KnowledgeSourceRequestWrapper textRequest;

    private final UUID USER_ID = UUID.randomUUID();
    private final UUID BOT_ID = UUID.randomUUID();
    private final UUID SOURCE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        chatbot = Chatbot.builder()
                .userId(USER_ID)
                .name("Test Bot")
                .build();

        knowledgeSource = KnowledgeSource.builder()
                .chatbot(chatbot)
                .name("My Text Source")
                .sourceType(KnowledgeSourceType.TEXT)
                .status(ProcessingStatus.CREATED)
                .build();

        textRequest = new KnowledgeSourceRequestWrapper(
                "New Text Source",
                KnowledgeSourceType.TEXT,
                null,
                null,
                new TextSourceDto("Some knowledge content here")
        );
    }

    @Test
    void createSource_ShouldReturnDto_WhenValidTextRequest() {
        when(chatbotService.findBotOrThrow(BOT_ID, USER_ID)).thenReturn(chatbot);
        when(sourceRepository.save(any(KnowledgeSource.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KnowledgeSourceResponseDto response = sourceService.createSource(BOT_ID, USER_ID, textRequest);

        assertNotNull(response);
        assertEquals("New Text Source", response.name());
        assertEquals(KnowledgeSourceType.TEXT, response.sourceType());
        
        ArgumentCaptor<KnowledgeSource> captor = ArgumentCaptor.forClass(KnowledgeSource.class);
        verify(sourceRepository).save(captor.capture());
        KnowledgeSource savedSource = captor.getValue();
        assertEquals(ProcessingStatus.CREATED, savedSource.getStatus());
        assertNotNull(savedSource.getSourceText());
        assertEquals("Some knowledge content here", savedSource.getSourceText().getContent());
    }

    @Test
    void getSource_ShouldReturnDto_WhenFound() {
        when(chatbotService.findBotOrThrow(BOT_ID, USER_ID)).thenReturn(chatbot);
        when(sourceRepository.findByIdAndChatbotId(SOURCE_ID, BOT_ID)).thenReturn(Optional.of(knowledgeSource));

        KnowledgeSourceResponseDto response = sourceService.getSource(BOT_ID, SOURCE_ID, USER_ID);

        assertNotNull(response);
        assertEquals("My Text Source", response.name());
    }

    @Test
    void getSource_ShouldThrowNotFound_WhenDataMissing() {
        when(chatbotService.findBotOrThrow(BOT_ID, USER_ID)).thenReturn(chatbot);
        when(sourceRepository.findByIdAndChatbotId(SOURCE_ID, BOT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sourceService.getSource(BOT_ID, SOURCE_ID, USER_ID));
    }

    @Test
    void getAllSources_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<KnowledgeSource> page = new PageImpl<>(Collections.singletonList(knowledgeSource));
        
        when(chatbotService.findBotOrThrow(BOT_ID, USER_ID)).thenReturn(chatbot);
        when(sourceRepository.findAllByChatbotId(BOT_ID, pageable)).thenReturn(page);

        Page<KnowledgeSourceResponseDto> result = sourceService.getAllSources(BOT_ID, USER_ID, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(sourceRepository).findAllByChatbotId(BOT_ID, pageable);
    }

    @Test
    void deleteSource_ShouldSoftDelete_WhenFound() {
        when(chatbotService.findBotOrThrow(BOT_ID, USER_ID)).thenReturn(chatbot);
        when(sourceRepository.findByIdAndChatbotId(SOURCE_ID, BOT_ID)).thenReturn(Optional.of(knowledgeSource));

        sourceService.deleteSource(BOT_ID, SOURCE_ID, USER_ID);

        ArgumentCaptor<KnowledgeSource> captor = ArgumentCaptor.forClass(KnowledgeSource.class);
        verify(sourceRepository).save(captor.capture());
        KnowledgeSource deletedSource = captor.getValue();
        
        assertEquals(ProcessingStatus.DELETED, deletedSource.getStatus());
        assertNotNull(deletedSource.getDeletedAt());
    }
}
