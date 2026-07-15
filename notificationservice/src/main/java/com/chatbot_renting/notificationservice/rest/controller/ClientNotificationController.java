package com.chatbot_renting.notificationservice.rest.controller;

import com.chatbot_renting.notificationservice.rest.api.ClientNotificationApi;
import com.chatbot_renting.notificationservice.service.NotificationService;
import com.chatbot_renting.notificationservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientNotificationController implements ClientNotificationApi {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<Object> getTimeline() {
        UUID userId = securityUtils.getCurrentUserId();
        // Return dummy implementation list
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Long> getUnreadCount() {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @Override
    public ResponseEntity<Void> markAsRead(UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> markAllAsRead() {
        UUID userId = securityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
