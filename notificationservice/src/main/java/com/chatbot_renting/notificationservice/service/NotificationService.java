package com.chatbot_renting.notificationservice.service;

import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Page<com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse> getTimeline(UUID userId,
            Pageable pageable);

    List<com.chatbot_renting.notificationservice.dto.response.PreferenceResponse> getUserPreferences(UUID userId);

    UUID sendNotification(NotificationSendRequest request);

    void broadcastNotification(String templateCode, java.util.Map<String, Object> contextData, UUID actorId);

    // Giờ tái sử dụng chung pipeline Payload/Recipient/Delivery thay vì tạo
    // Delivery mồ côi
    UUID sendDirectEmail(UUID userId, String email, String templateCode, java.util.Map<String, Object> contextData);

    UUID sendDirectZalo(UUID userId, String zaloPhone, String templateCode, java.util.Map<String, Object> contextData);

    /**
     * @param userId            người gọi API (để check ownership)
     * @param recipientRecordId bản ghi NotificationRecipient cần đánh dấu đã đọc
     */
    void markAsRead(UUID userId, UUID recipientRecordId);

    void markAllAsRead(UUID userId);

    void deleteNotification(UUID userId, UUID recipientRecordId);

    long getUnreadCount(UUID userId);

    void upsertPreference(UUID userId, String templateCode, String channel, boolean isEnabled);

    // Gọi bởi dispatch consumer (Kafka listener) sau khi gửi thật xong
    void markDeliverySent(UUID deliveryId);

    void markDeliveryFailed(UUID deliveryId, String errorMessage);
}