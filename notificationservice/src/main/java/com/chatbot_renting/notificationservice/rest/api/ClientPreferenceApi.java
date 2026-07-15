package com.chatbot_renting.notificationservice.rest.api;

import com.chatbot_renting.notificationservice.dto.request.PreferenceUpdateRequest;
import com.chatbot_renting.notificationservice.dto.response.PreferenceResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/client-api/v1/preferences")
public interface ClientPreferenceApi {

    @GetMapping
    ResponseEntity<List<PreferenceResponse>> getPreferences();

    @PutMapping
    ResponseEntity<Void> upsertPreference(
            @RequestParam("templateCode") String templateCode,
            @RequestParam("channel") String channel,
            @Valid @RequestBody PreferenceUpdateRequest request);
}
