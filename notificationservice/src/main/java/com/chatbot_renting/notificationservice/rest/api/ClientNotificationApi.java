package com.chatbot_renting.notificationservice.rest.api;

import com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/client-api/v1/notifications")
public interface ClientNotificationApi {

    @GetMapping
    ResponseEntity<Page<NotificationTimelineResponse>> getTimeline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping("/unread-count")
    ResponseEntity<Long> getUnreadCount();

    @PutMapping("/{id}/read")
    ResponseEntity<Void> markAsRead(@PathVariable("id") UUID id);

    @PutMapping("/read-all")
    ResponseEntity<Void> markAllAsRead();
}
