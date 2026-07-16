package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dto.response.PreferenceResponse;
import com.chatbot_renting.notificationservice.entity.NotificationTemplate;
import com.chatbot_renting.notificationservice.entity.UserNotificationPreference;
import com.chatbot_renting.notificationservice.repository.NotificationTemplateRepository;
import com.chatbot_renting.notificationservice.repository.UserNotificationPreferenceRepository;
import com.chatbot_renting.notificationservice.service.ClientPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientPreferenceServiceImpl implements ClientPreferenceService {

    private final UserNotificationPreferenceRepository preferenceRepository;
    private final NotificationTemplateRepository templateRepository;

    @Override
    public List<PreferenceResponse> getUserPreferences(UUID userId) {
        return preferenceRepository.findByUserId(userId).stream()
                .map(p -> PreferenceResponse.builder()
                        .templateCode(p.getTemplate() != null ? p.getTemplate().getCode() : null)
                        .channel(p.getChannel())
                        .isEnabled(p.getIsEnabled())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void upsertPreference(UUID userId, String templateCode, String channel, boolean isEnabled) {
        log.info("Upserting preference for user {}: {} -> {} = {}", userId, templateCode, channel, isEnabled);
        NotificationTemplate template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template code not found: " + templateCode));

        UserNotificationPreference preference = preferenceRepository
                .findByUserIdAndTemplateAndChannel(userId, template, channel)
                .orElse(UserNotificationPreference.builder()
                        .userId(userId)
                        .template(template)
                        .channel(channel)
                        .build());

        preference.setIsEnabled(isEnabled);
        preferenceRepository.save(preference);
    }
}
