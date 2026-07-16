package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse;
import com.chatbot_renting.notificationservice.entity.NotificationRecipient;
import com.chatbot_renting.notificationservice.repository.NotificationRecipientRepository;
import com.chatbot_renting.notificationservice.service.ClientNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientNotificationServiceImpl implements ClientNotificationService {

    private final NotificationRecipientRepository recipientRepository;

    @Override
    public Page<NotificationTimelineResponse> getTimeline(UUID userId, Pageable pageable) {
        log.info("Fetching notification timeline for user: {}, pageable: {}", userId, pageable);
        Page<NotificationTimelineResponse> response = recipientRepository.findByRecipientIdOrderByPayloadCreatedAtDesc(userId, pageable)
                .map(r -> NotificationTimelineResponse.builder()
                        .id(r.getId())
                        .templateCode(r.getPayload() != null && r.getPayload().getTemplate() != null
                                ? r.getPayload().getTemplate().getCode()
                                : null)
                        .sourceEntityId(r.getPayload() != null ? r.getPayload().getSourceEntityId() : null)
                        .sourceEntityType(r.getPayload() != null ? r.getPayload().getSourceEntityType() : null)
                        .contextData(r.getPayload() != null ? r.getPayload().getContextData() : null)
                        .isRead(r.getIsRead())
                        .readAt(r.getReadAt() != null ? r.getReadAt().toLocalDateTime() : null)
                        .createdAt(r.getPayload() != null && r.getPayload().getCreatedAt() != null
                                ? r.getPayload().getCreatedAt().toLocalDateTime()
                                : null)
                        .build());
        log.info("Successfully fetched notification timeline for user: {}", userId);
        return response;
    }

    @Override
    @Transactional
    public void markAsRead(UUID userId, UUID recipientRecordId) {
        log.info("Marking notification {} as read for user: {}", recipientRecordId, userId);
        NotificationRecipient recipient = recipientRepository.findByIdAndRecipientId(recipientRecordId, userId)
                .orElseThrow(() -> {
                    log.error("Failed to mark as read. Notification {} not found or not owned by user: {}", recipientRecordId, userId);
                    return new IllegalArgumentException("Notification not found or not owned by user");
                });

        if (Boolean.TRUE.equals(recipient.getIsRead())) {
            log.info("Notification {} is already read for user: {}", recipientRecordId, userId);
            return;
        }
        recipient.setIsRead(true);
        recipient.setReadAt(OffsetDateTime.now());
        recipientRepository.save(recipient);
        log.info("Successfully marked notification {} as read for user: {}", recipientRecordId, userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        log.info("Starting mark all notifications as read for user: {}", userId);
        List<NotificationRecipient> unread = recipientRepository.findByRecipientIdAndIsReadFalse(userId);
        
        if (unread.isEmpty()) {
            log.info("No unread notifications found for user: {}", userId);
            return;
        }

        unread.forEach(r -> {
            r.setIsRead(true);
            r.setReadAt(OffsetDateTime.now());
        });
        recipientRepository.saveAll(unread);
        log.info("Successfully marked {} notifications as read for user: {}", unread.size(), userId);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID userId, UUID recipientRecordId) {
        log.info("Deleting notification {} for user: {}", recipientRecordId, userId);
        NotificationRecipient recipient = recipientRepository.findByIdAndRecipientId(recipientRecordId, userId)
                .orElseThrow(() -> {
                    log.error("Failed to delete notification. Notification {} not found or not owned by user: {}", recipientRecordId, userId);
                    return new IllegalArgumentException("Notification not found or not owned by user");
                });

        recipient.setIsDeleted(true);
        recipient.setDeletedAt(OffsetDateTime.now());
        recipientRepository.save(recipient);
        log.info("Successfully deleted notification {} for user: {}", recipientRecordId, userId);
    }

    @Override
    public long getUnreadCount(UUID userId) {
        log.info("Fetching unread notification count for user: {}", userId);
        long count = recipientRepository.countByRecipientIdAndIsReadFalseAndIsDeletedFalse(userId);
        log.info("Successfully fetched unread notification count for user {}: {}", userId, count);
        return count;
    }
}
