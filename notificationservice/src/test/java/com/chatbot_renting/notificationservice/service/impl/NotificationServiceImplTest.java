package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import com.chatbot_renting.notificationservice.entity.NotificationDelivery;
import com.chatbot_renting.notificationservice.entity.NotificationPayload;
import com.chatbot_renting.notificationservice.entity.NotificationRecipient;
import com.chatbot_renting.notificationservice.entity.NotificationTemplate;
import com.chatbot_renting.notificationservice.repository.NotificationDeliveryRepository;
import com.chatbot_renting.notificationservice.repository.NotificationPayloadRepository;
import com.chatbot_renting.notificationservice.repository.NotificationRecipientRepository;
import com.chatbot_renting.notificationservice.repository.NotificationTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationTemplateRepository templateRepository;
    @Mock
    private NotificationPayloadRepository payloadRepository;
    @Mock
    private NotificationRecipientRepository recipientRepository;
    @Mock
    private NotificationDeliveryRepository deliveryRepository;
    @Mock
    private com.chatbot_renting.notificationservice.repository.UserNotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationTemplate template;

    @BeforeEach
    void setUp() {
        template = NotificationTemplate.builder()
                .id(UUID.randomUUID())
                .code("PAYMENT_FAILED")
                .name("Payment Failed")
                .build();
    }

    @Test
    void testSendNotification_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationSendRequest request = NotificationSendRequest.builder()
                .templateCode("PAYMENT_FAILED")
                .actorId(UUID.randomUUID())
                .sourceEntityId("INV-100")
                .sourceEntityType("INVOICE")
                .contextData(Map.of("amount", 99.99))
                .recipients(List.of(
                        NotificationSendRequest.RecipientInfo.builder()
                                .userId(userId)
                                .email("test@example.com")
                                .deviceToken("fcm-token-123")
                                .build()
                ))
                .build();

        when(templateRepository.findByCode("PAYMENT_FAILED")).thenReturn(Optional.of(template));
        
        NotificationPayload expectedPayload = NotificationPayload.builder()
                .id(UUID.randomUUID())
                .template(template)
                .build();
        when(payloadRepository.save(any(NotificationPayload.class))).thenReturn(expectedPayload);
        
        when(preferenceRepository.findByUserIdAndTemplateAndChannel(any(UUID.class), any(NotificationTemplate.class), anyString()))
                .thenReturn(Optional.empty());

        // Act
        UUID payloadId = notificationService.sendNotification(request);

        // Assert
        assertNotNull(payloadId);
        assertEquals(expectedPayload.getId(), payloadId);

        // Verify Payload Save
        ArgumentCaptor<NotificationPayload> payloadCaptor = ArgumentCaptor.forClass(NotificationPayload.class);
        verify(payloadRepository, times(1)).save(payloadCaptor.capture());
        NotificationPayload capturedPayload = payloadCaptor.getValue();
        assertEquals("INV-100", capturedPayload.getSourceEntityId());
        assertEquals("INVOICE", capturedPayload.getSourceEntityType());

        // Verify Recipient Save
        ArgumentCaptor<List<NotificationRecipient>> recipientCaptor = ArgumentCaptor.forClass(List.class);
        verify(recipientRepository, times(1)).saveAll(recipientCaptor.capture());
        List<NotificationRecipient> savedRecipients = recipientCaptor.getValue();
        assertEquals(1, savedRecipients.size());
        assertEquals(userId, savedRecipients.get(0).getRecipientId());

        // Verify Delivery Save (IN_APP + EMAIL + FCM_PUSH = 3 deliveries for this recipient)
        ArgumentCaptor<List<NotificationDelivery>> deliveryCaptor = ArgumentCaptor.forClass(List.class);
        verify(deliveryRepository, times(1)).saveAll(deliveryCaptor.capture());
        List<NotificationDelivery> savedDeliveries = deliveryCaptor.getValue();
        assertEquals(3, savedDeliveries.size());
        assertTrue(savedDeliveries.stream().anyMatch(d -> d.getChannel().equals("IN_APP")));
        assertTrue(savedDeliveries.stream().anyMatch(d -> d.getChannel().equals("EMAIL") && d.getDestination().equals("test@example.com")));
        assertTrue(savedDeliveries.stream().anyMatch(d -> d.getChannel().equals("FCM_PUSH") && d.getDestination().equals("fcm-token-123")));
    }

    @Test
    void testSendNotification_TemplateNotFound() {
        // Arrange
        NotificationSendRequest request = NotificationSendRequest.builder()
                .templateCode("UNKNOWN_CODE")
                .build();

        when(templateRepository.findByCode("UNKNOWN_CODE")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.sendNotification(request);
        });

        assertTrue(exception.getMessage().contains("Template code not found"));
        verify(payloadRepository, never()).save(any());
        verify(recipientRepository, never()).saveAll(any());
        verify(deliveryRepository, never()).saveAll(any());
    }

    @Test
    void testMarkAsRead_Success() {
        // Arrange
        UUID recordId = UUID.randomUUID();
        NotificationRecipient recipient = NotificationRecipient.builder()
                .id(recordId)
                .isRead(false)
                .build();
                
        when(recipientRepository.findById(recordId)).thenReturn(Optional.of(recipient));

        // Act
        notificationService.markAsRead(recordId);

        // Assert
        assertTrue(recipient.getIsRead());
        assertNotNull(recipient.getReadAt());
        verify(recipientRepository, times(1)).save(recipient);
    }
    
    @Test
    void testSendNotification_UserOptedOutEmailAndPush() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationSendRequest request = NotificationSendRequest.builder()
                .templateCode("PAYMENT_FAILED")
                .recipients(List.of(
                        NotificationSendRequest.RecipientInfo.builder()
                                .userId(userId)
                                .email("optout@example.com")
                                .deviceToken("fcm-optout-123")
                                .build()
                ))
                .build();

        when(templateRepository.findByCode("PAYMENT_FAILED")).thenReturn(Optional.of(template));
        NotificationPayload expectedPayload = NotificationPayload.builder().id(UUID.randomUUID()).template(template).build();
        when(payloadRepository.save(any(NotificationPayload.class))).thenReturn(expectedPayload);
        
        // Mock user opted out of EMAIL and FCM_PUSH
        com.chatbot_renting.notificationservice.entity.UserNotificationPreference optOutPref = 
            com.chatbot_renting.notificationservice.entity.UserNotificationPreference.builder()
                .isEnabled(false)
                .build();
                
        when(preferenceRepository.findByUserIdAndTemplateAndChannel(any(UUID.class), any(NotificationTemplate.class), anyString()))
                .thenReturn(Optional.of(optOutPref));

        // Act
        notificationService.sendNotification(request);

        // Assert
        ArgumentCaptor<List<NotificationDelivery>> deliveryCaptor = ArgumentCaptor.forClass(List.class);
        verify(deliveryRepository, times(1)).saveAll(deliveryCaptor.capture());
        List<NotificationDelivery> savedDeliveries = deliveryCaptor.getValue();
        
        assertEquals(1, savedDeliveries.size());
        assertEquals("IN_APP", savedDeliveries.get(0).getChannel()); // Only IN_APP should be created
    }

    @Test
    void testSendDirectEmail_Success() {
        // Act
        notificationService.sendDirectEmail("test@example.com", "TEMPLATE", Map.of("key", "val"));

        // Assert
        ArgumentCaptor<NotificationDelivery> deliveryCaptor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository, times(1)).save(deliveryCaptor.capture());
        
        NotificationDelivery savedDelivery = deliveryCaptor.getValue();
        assertEquals("EMAIL", savedDelivery.getChannel());
        assertEquals("test@example.com", savedDelivery.getDestination());
        assertEquals("PENDING", savedDelivery.getStatus());
    }

    @Test
    void testSendDirectZalo_Success() {
        // Act
        notificationService.sendDirectZalo("0987654321", "ZALO_TPL_1", Map.of("key", "val"));

        // Assert
        ArgumentCaptor<NotificationDelivery> deliveryCaptor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository, times(1)).save(deliveryCaptor.capture());
        
        NotificationDelivery savedDelivery = deliveryCaptor.getValue();
        assertEquals("ZALO", savedDelivery.getChannel());
        assertEquals("0987654321", savedDelivery.getDestination());
        assertEquals("PENDING", savedDelivery.getStatus());
    }

    @Test
    void testUpsertPreference_InsertNew() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(templateRepository.findByCode("PAYMENT_FAILED")).thenReturn(Optional.of(template));
        when(preferenceRepository.findByUserIdAndTemplateAndChannel(userId, template, "EMAIL"))
                .thenReturn(Optional.empty());

        // Act
        notificationService.upsertPreference(userId, "PAYMENT_FAILED", "EMAIL", false);

        // Assert
        ArgumentCaptor<com.chatbot_renting.notificationservice.entity.UserNotificationPreference> prefCaptor = 
            ArgumentCaptor.forClass(com.chatbot_renting.notificationservice.entity.UserNotificationPreference.class);
        verify(preferenceRepository, times(1)).save(prefCaptor.capture());
        
        com.chatbot_renting.notificationservice.entity.UserNotificationPreference savedPref = prefCaptor.getValue();
        assertEquals(userId, savedPref.getUserId());
        assertEquals("EMAIL", savedPref.getChannel());
        assertFalse(savedPref.getIsEnabled());
    }

    @Test
    void testUpsertPreference_UpdateExisting() {
        // Arrange
        UUID userId = UUID.randomUUID();
        com.chatbot_renting.notificationservice.entity.UserNotificationPreference existingPref = 
            com.chatbot_renting.notificationservice.entity.UserNotificationPreference.builder()
                .userId(userId)
                .template(template)
                .channel("FCM_PUSH")
                .isEnabled(true)
                .build();
                
        when(templateRepository.findByCode("PAYMENT_FAILED")).thenReturn(Optional.of(template));
        when(preferenceRepository.findByUserIdAndTemplateAndChannel(userId, template, "FCM_PUSH"))
                .thenReturn(Optional.of(existingPref));

        // Act
        notificationService.upsertPreference(userId, "PAYMENT_FAILED", "FCM_PUSH", false);

        // Assert
        verify(preferenceRepository, times(1)).save(existingPref);
        assertFalse(existingPref.getIsEnabled()); // Should be updated to false
    }

    @Test
    void testBroadcastNotification_DoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            notificationService.broadcastNotification("TEST_CODE", Map.of(), UUID.randomUUID());
        });
    }

}
