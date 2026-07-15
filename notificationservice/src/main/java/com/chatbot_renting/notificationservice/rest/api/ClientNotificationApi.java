package com.chatbot_renting.notificationservice.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/client-api/v1/notifications")
public interface ClientNotificationApi {

    @GetMapping
    ResponseEntity<Object> getTimeline(); // Placeholder for paginated DTO

    @GetMapping("/unread-count")
    ResponseEntity<Long> getUnreadCount();

    @PutMapping("/{id}/read")
    ResponseEntity<Void> markAsRead(@PathVariable("id") UUID id);

    @PutMapping("/read-all")
    ResponseEntity<Void> markAllAsRead();
}
