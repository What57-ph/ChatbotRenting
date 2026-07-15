package com.chatbot_renting.notificationservice.rest.controller;

import com.chatbot_renting.notificationservice.dto.request.PreferenceUpdateRequest;
import com.chatbot_renting.notificationservice.rest.api.ClientPreferenceApi;
import com.chatbot_renting.notificationservice.service.NotificationService;
import com.chatbot_renting.notificationservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientPreferenceController implements ClientPreferenceApi {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<Object> getPreferences() {
        UUID userId = securityUtils.getCurrentUserId();
        // Return dummy preferences summary
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> upsertPreference(String templateCode, String channel, PreferenceUpdateRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        notificationService.upsertPreference(userId, templateCode, channel, request.getIsEnabled());
        return ResponseEntity.ok().build();
    }
}
