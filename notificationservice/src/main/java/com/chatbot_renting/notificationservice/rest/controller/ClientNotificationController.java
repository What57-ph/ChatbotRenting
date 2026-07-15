package com.chatbot_renting.notificationservice.rest.controller;

import com.chatbot_renting.notificationservice.rest.api.ClientNotificationApi;
import com.chatbot_renting.notificationservice.service.NotificationService;
import com.chatbot_renting.notificationservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse;

@RestController
@RequiredArgsConstructor
public class ClientNotificationController implements ClientNotificationApi {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<Page<NotificationTimelineResponse>> getTimeline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = securityUtils.getCurrentUserId();
        Page<NotificationTimelineResponse> timeline = notificationService.getTimeline(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(timeline);
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
