package com.chatbot_renting.notificationservice.service;

import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;

import java.util.UUID;

public interface NotificationService {

    /**
     * Creates a notification payload, assigns it to recipients, and schedules deliveries.
     * @param request The request containing template code, payload data, and recipient list.
     * @return The UUID of the created NotificationPayload.
     */
    UUID sendNotification(NotificationSendRequest request);

    void broadcastNotification(String templateCode, java.util.Map<String, Object> contextData, UUID actorId);

    void sendDirectEmail(String email, String templateCode, java.util.Map<String, Object> contextData);

    void sendDirectZalo(String zaloPhone, String zaloTemplateId, java.util.Map<String, Object> contextData);

    /**
     * Marks a specific notification recipient record as read.
     * @param recipientRecordId The UUID of the NotificationRecipient.
     */
    void markAsRead(UUID recipientRecordId);

    void markAllAsRead(UUID userId);

    long getUnreadCount(UUID userId);

    void upsertPreference(UUID userId, String templateCode, String channel, boolean isEnabled);
}
