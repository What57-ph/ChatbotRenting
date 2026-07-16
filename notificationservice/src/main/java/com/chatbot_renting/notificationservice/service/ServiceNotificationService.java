package com.chatbot_renting.notificationservice.service;

import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import java.util.Map;
import java.util.UUID;

public interface ServiceNotificationService {
    UUID sendNotification(NotificationSendRequest request);
    void broadcastNotification(String templateCode, Map<String, Object> contextData, UUID actorId);
    UUID sendDirectEmail(String email, String templateCode, Map<String, Object> contextData);
    UUID sendDirectZalo(String zaloPhone, String templateCode, Map<String, Object> contextData);
    void markDeliverySent(UUID deliveryId);
    void markDeliveryFailed(UUID deliveryId, String errorMessage);
}
