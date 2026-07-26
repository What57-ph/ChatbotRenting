package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dto.response.PreferenceResponse;
import com.chatbot_renting.notificationservice.entity.NotificationTemplate;
import com.chatbot_renting.notificationservice.entity.UserNotificationPreference;
import com.chatbot_renting.notificationservice.repository.NotificationTemplateRepository;
import com.chatbot_renting.notificationservice.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientPreferenceServiceImplTest {

    @Mock
    private UserNotificationPreferenceRepository preferenceRepository;

    @Mock
    private NotificationTemplateRepository templateRepository;

    @InjectMocks
    private ClientPreferenceServiceImpl clientPreferenceService;

    private UUID userId;
    private UserNotificationPreference preference;
    private NotificationTemplate template;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        template = NotificationTemplate.builder()
                .code("NEW_MESSAGE")
                .build();

        preference = UserNotificationPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .template(template)
                .channel("EMAIL")
                .isEnabled(true)
                .build();
    }

    @Test
    void getUserPreferences_ShouldMapCorrectly() {
        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Collections.singletonList(preference));

        List<PreferenceResponse> responses = clientPreferenceService.getUserPreferences(userId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("NEW_MESSAGE", responses.get(0).getTemplateCode());
        assertEquals("EMAIL", responses.get(0).getChannel());
        assertTrue(responses.get(0).getIsEnabled());
    }

    @Test
    void upsertPreference_WithExistingPreference_ShouldUpdate() {
        when(templateRepository.findByCode("NEW_MESSAGE"))
                .thenReturn(Optional.of(template));
        when(preferenceRepository.findByUserIdAndTemplateAndChannel(userId, template, "EMAIL"))
                .thenReturn(Optional.of(preference));

        clientPreferenceService.upsertPreference(userId, "NEW_MESSAGE", "EMAIL", false);

        assertFalse(preference.getIsEnabled());
        verify(preferenceRepository, times(1)).save(preference);
    }

    @Test
    void upsertPreference_WithNewPreference_ShouldCreate() {
        when(templateRepository.findByCode("NEW_MESSAGE"))
                .thenReturn(Optional.of(template));
        when(preferenceRepository.findByUserIdAndTemplateAndChannel(userId, template, "EMAIL"))
                .thenReturn(Optional.empty());

        clientPreferenceService.upsertPreference(userId, "NEW_MESSAGE", "EMAIL", true);

        verify(preferenceRepository, times(1)).save(any(UserNotificationPreference.class));
    }

    @Test
    void upsertPreference_TemplateNotFound_ShouldThrowException() {
        when(templateRepository.findByCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> clientPreferenceService.upsertPreference(userId, "UNKNOWN", "EMAIL", true));
        verify(preferenceRepository, never()).save(any());
    }
}
