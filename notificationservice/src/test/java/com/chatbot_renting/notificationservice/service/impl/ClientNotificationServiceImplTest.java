package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse;
import com.chatbot_renting.notificationservice.entity.NotificationPayload;
import com.chatbot_renting.notificationservice.entity.NotificationRecipient;
import com.chatbot_renting.notificationservice.entity.NotificationTemplate;
import com.chatbot_renting.notificationservice.repository.NotificationRecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientNotificationServiceImplTest {

    @Mock
    private NotificationRecipientRepository recipientRepository;

    @InjectMocks
    private ClientNotificationServiceImpl clientNotificationService;

    private UUID userId;
    private UUID recipientRecordId;
    private NotificationRecipient recipient;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        recipientRecordId = UUID.randomUUID();

        NotificationTemplate template = NotificationTemplate.builder()
                .code("TEST_TEMPLATE")
                .build();

        NotificationPayload payload = NotificationPayload.builder()
                .template(template)
                .sourceEntityId(UUID.randomUUID().toString())
                .sourceEntityType("SYSTEM")
                .createdAt(OffsetDateTime.now())
                .build();

        recipient = NotificationRecipient.builder()
                .id(recipientRecordId)
                .recipientId(userId)
                .isRead(false)
                .isDeleted(false)
                .payload(payload)
                .build();
    }

    @Test
    void getTimeline_ShouldReturnPagedResponse() {
        when(recipientRepository.findTimelineByRecipientId(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(recipient)));

        Page<NotificationTimelineResponse> timeline = clientNotificationService.getTimeline(userId, Pageable.unpaged());

        assertNotNull(timeline);
        assertEquals(1, timeline.getTotalElements());
        NotificationTimelineResponse response = timeline.getContent().get(0);
        assertEquals("TEST_TEMPLATE", response.getTemplateCode());
        assertEquals("SYSTEM", response.getSourceEntityType());
        assertFalse(response.getIsRead());
    }

    @Test
    void markAsRead_WhenUnread_ShouldUpdateReadStatus() {
        when(recipientRepository.findByIdAndRecipientId(recipientRecordId, userId))
                .thenReturn(Optional.of(recipient));

        clientNotificationService.markAsRead(userId, recipientRecordId);

        assertTrue(recipient.getIsRead());
        assertNotNull(recipient.getReadAt());
        verify(recipientRepository, times(1)).save(recipient);
    }

    @Test
    void markAsRead_WhenAlreadyRead_ShouldNotUpdate() {
        recipient.setIsRead(true);
        when(recipientRepository.findByIdAndRecipientId(recipientRecordId, userId))
                .thenReturn(Optional.of(recipient));

        clientNotificationService.markAsRead(userId, recipientRecordId);

        verify(recipientRepository, never()).save(recipient);
    }

    @Test
    void markAsRead_NotFound_ShouldThrowException() {
        when(recipientRepository.findByIdAndRecipientId(recipientRecordId, userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> clientNotificationService.markAsRead(userId, recipientRecordId));
    }

    @Test
    void markAllAsRead_ShouldInvokeRepositoryMethod() {
        clientNotificationService.markAllAsRead(userId);

        verify(recipientRepository, times(1)).markAllAsRead(userId);
    }

    @Test
    void deleteNotification_ShouldSetDeletedFlags() {
        when(recipientRepository.findByIdAndRecipientId(recipientRecordId, userId))
                .thenReturn(Optional.of(recipient));

        clientNotificationService.deleteNotification(userId, recipientRecordId);

        assertTrue(recipient.getIsDeleted());
        assertNotNull(recipient.getDeletedAt());
        verify(recipientRepository, times(1)).save(recipient);
    }

    @Test
    void getUnreadCount_ShouldReturnCorrectCount() {
        when(recipientRepository.countByRecipientIdAndIsReadFalseAndIsDeletedFalse(userId))
                .thenReturn(5L);

        long count = clientNotificationService.getUnreadCount(userId);

        assertEquals(5L, count);
        verify(recipientRepository, times(1))
                .countByRecipientIdAndIsReadFalseAndIsDeletedFalse(userId);
    }
}
