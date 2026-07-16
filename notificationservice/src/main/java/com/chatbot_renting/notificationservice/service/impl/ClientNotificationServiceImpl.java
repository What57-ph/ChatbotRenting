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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientNotificationServiceImpl implements ClientNotificationService {

    private final NotificationRecipientRepository recipientRepository;

    @Override
    public Page<NotificationTimelineResponse> getTimeline(UUID userId, Pageable pageable) {
        return recipientRepository.findTimelineByRecipientId(userId, pageable)
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
    }

    @Override
    @Transactional
    public void markAsRead(UUID userId, UUID recipientRecordId) {
        NotificationRecipient recipient = recipientRepository.findByIdAndRecipientId(recipientRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found or not owned by user"));

        if (Boolean.TRUE.equals(recipient.getIsRead())) {
            return;
        }
        recipient.setIsRead(true);
        recipient.setReadAt(OffsetDateTime.now());
        recipientRepository.save(recipient);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        recipientRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID userId, UUID recipientRecordId) {
        NotificationRecipient recipient = recipientRepository.findByIdAndRecipientId(recipientRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found or not owned by user"));

        recipient.setIsDeleted(true);
        recipient.setDeletedAt(OffsetDateTime.now());
        recipientRepository.save(recipient);
    }

    @Override
    public long getUnreadCount(UUID userId) {
        return recipientRepository.countByRecipientIdAndIsReadFalseAndIsDeletedFalse(userId);
    }
}
