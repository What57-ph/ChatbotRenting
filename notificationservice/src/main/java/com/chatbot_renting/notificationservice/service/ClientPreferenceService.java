package com.chatbot_renting.notificationservice.service;

import java.util.List;
import java.util.UUID;
import com.chatbot_renting.notificationservice.dto.response.PreferenceResponse;

public interface ClientPreferenceService {
    List<PreferenceResponse> getUserPreferences(UUID userId);
    void upsertPreference(UUID userId, String templateCode, String channel, boolean isEnabled);
}
