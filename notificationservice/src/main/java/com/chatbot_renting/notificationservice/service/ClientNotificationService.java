package com.chatbot_renting.notificationservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse;

public interface ClientNotificationService {
    Page<NotificationTimelineResponse> getTimeline(UUID userId, Pageable pageable);
    void markAsRead(UUID userId, UUID recipientRecordId);
    void markAllAsRead(UUID userId);
    void deleteNotification(UUID userId, UUID recipientRecordId);
    long getUnreadCount(UUID userId);
}
