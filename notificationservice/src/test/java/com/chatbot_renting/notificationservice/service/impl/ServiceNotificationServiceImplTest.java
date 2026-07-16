package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.client.UserAccountClient;
import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import com.chatbot_renting.notificationservice.dto.response.client.AllUsersResponse;
import com.chatbot_renting.notificationservice.entity.NotificationPayload;
import com.chatbot_renting.notificationservice.entity.NotificationRecipient;
import com.chatbot_renting.notificationservice.entity.NotificationTemplate;
import com.chatbot_renting.notificationservice.entity.NotificationDelivery;
import com.chatbot_renting.notificationservice.event.DeliveriesCreatedEvent;
import com.chatbot_renting.notificationservice.repository.NotificationPayloadRepository;
import com.chatbot_renting.notificationservice.repository.NotificationRecipientRepository;
import com.chatbot_renting.notificationservice.repository.NotificationTemplateRepository;
import com.chatbot_renting.notificationservice.repository.NotificationDeliveryRepository;
import com.chatbot_renting.notificationservice.repository.UserNotificationPreferenceRepository;
import com.chatbot_renting.notificationservice.dispatch.DeliveryDispatchPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceNotificationServiceImplTest {

    @Mock
    private NotificationPayloadRepository payloadRepository;

    @Mock
    private NotificationRecipientRepository recipientRepository;

    @Mock
    private NotificationTemplateRepository templateRepository;

    @Mock
    private NotificationDeliveryRepository deliveryRepository;

    @Mock
    private UserNotificationPreferenceRepository preferenceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DeliveryDispatchPublisher dispatchPublisher;

    @Mock
    private UserAccountClient userAccountClient;

    @InjectMocks
    private ServiceNotificationServiceImpl serviceNotificationService;

    @Captor
    private ArgumentCaptor<List<NotificationRecipient>> recipientsCaptor;

    private NotificationTemplate template;
    private NotificationSendRequest.RecipientInfo recipientInfo;

    @BeforeEach
    void setUp() {
        template = NotificationTemplate.builder()
                .code("ANNOUNCEMENT")
                .build();

        recipientInfo = NotificationSendRequest.RecipientInfo.builder()
                .userId(UUID.randomUUID())
                .build();
    }

    @Test
    void sendDirectEmail_ShouldCreatePayloadAndRecipientAndPublishEvent() {
        Map<String, Object> params = new HashMap<>();
        params.put("subject", "Subject");

        NotificationPayload payloadParam = NotificationPayload.builder()
                .id(UUID.randomUUID())
                .build();

        when(templateRepository.findByCode("EMAIL_TPL")).thenReturn(Optional.of(template));
        when(payloadRepository.save(any(NotificationPayload.class))).thenReturn(payloadParam);
        when(deliveryRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        serviceNotificationService.sendDirectEmail("test@example.com", "EMAIL_TPL", params);

        verify(payloadRepository, times(1)).save(any(NotificationPayload.class));
        verify(recipientRepository, times(1)).saveAll(any());
        verify(eventPublisher, times(1)).publishEvent(any(DeliveriesCreatedEvent.class));
    }

    @Test
    void sendDirectZalo_ShouldCreatePayloadAndRecipientAndPublishEvent() {
        String templateId = "ZALO_TPL";
        Map<String, Object> params = new HashMap<>();
        params.put("KEY", "VALUE");

        NotificationPayload payloadParam = NotificationPayload.builder()
                .id(UUID.randomUUID())
                .build();

        when(templateRepository.findByCode("ZALO_TPL")).thenReturn(Optional.of(template));
        when(payloadRepository.save(any(NotificationPayload.class))).thenReturn(payloadParam);
        when(recipientRepository.save(any(NotificationRecipient.class))).thenReturn(NotificationRecipient.builder().id(UUID.randomUUID()).build());
        when(deliveryRepository.save(any(NotificationDelivery.class))).thenAnswer(i -> i.getArgument(0));

        serviceNotificationService.sendDirectZalo("0123456789", templateId, params);

        verify(payloadRepository, times(1)).save(any(NotificationPayload.class));
        verify(recipientRepository, times(1)).save(any(NotificationRecipient.class));
        verify(eventPublisher, times(1)).publishEvent(any(DeliveriesCreatedEvent.class));
    }

    @Test
    void broadcastNotification_ShouldChunkAndSaveUsers() {
        when(templateRepository.findByCode("ANNOUNCEMENT")).thenReturn(Optional.of(template));
        
        NotificationPayload mockPayload = NotificationPayload.builder().id(UUID.randomUUID()).build();
        when(payloadRepository.save(any())).thenReturn(mockPayload);
        
        // Mock user account client
        List<AllUsersResponse.UserResponse> activeUsers = new ArrayList<>();
        for (int i = 0; i < 2050; i++) { // Test chunking with batch size 1000
            AllUsersResponse.UserResponse user = new AllUsersResponse.UserResponse();
            user.setId(UUID.randomUUID());
            user.setActive(true);
            activeUsers.add(user);
        }
        
        // Add one inactive user to test filtering
        AllUsersResponse.UserResponse inactiveUser = new AllUsersResponse.UserResponse();
        inactiveUser.setId(UUID.randomUUID());
        inactiveUser.setActive(false);
        activeUsers.add(inactiveUser);
        
        AllUsersResponse usersResponse = new AllUsersResponse();
        usersResponse.setAllUsers(activeUsers);
        when(userAccountClient.getAllUsers()).thenReturn(usersResponse);

        serviceNotificationService.broadcastNotification("ANNOUNCEMENT", new HashMap<>(), UUID.randomUUID());

        // Verify chunk saving
        verify(recipientRepository, times(3)).saveAll(recipientsCaptor.capture());
        
        List<List<NotificationRecipient>> capturedLists = recipientsCaptor.getAllValues();
        assertEquals(3, capturedLists.size());
        assertEquals(1000, capturedLists.get(0).size()); // First chunk
        assertEquals(1000, capturedLists.get(1).size()); // Second chunk
        assertEquals(50, capturedLists.get(2).size());   // Remainder chunk
    }
}
