package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import com.chatbot_renting.notificationservice.entity.*;
import com.chatbot_renting.notificationservice.repository.*;
import com.chatbot_renting.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationPayloadRepository payloadRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;

    @Override
    @Transactional
    public UUID sendNotification(NotificationSendRequest request) {
        log.info("Processing notification dispatch for template: {}", request.getTemplateCode());

        NotificationTemplate template = templateRepository.findByCode(request.getTemplateCode())
                .orElseThrow(() -> new IllegalArgumentException("Template code not found: " + request.getTemplateCode()));

        NotificationPayload payload = NotificationPayload.builder()
                .template(template)
                .actorId(request.getActorId())
                .sourceEntityId(request.getSourceEntityId())
                .sourceEntityType(request.getSourceEntityType())
                .contextData(request.getContextData())
                .build();

        payload = payloadRepository.save(payload);

        List<NotificationRecipient> recipientsToSave = new ArrayList<>();
        List<NotificationDelivery> deliveriesToSave = new ArrayList<>();

        for (NotificationSendRequest.RecipientInfo info : request.getRecipients()) {
            NotificationRecipient recipient = NotificationRecipient.builder()
                    .payload(payload)
                    .recipientId(info.getUserId())
                    .isRead(false)
                    .isDeleted(false)
                    .build();
            
            // Add IN_APP delivery naturally
            NotificationDelivery inAppDelivery = NotificationDelivery.builder()
                    .recipientRecord(recipient)
                    .channel("IN_APP")
                    .status("PENDING")
                    .build();
            deliveriesToSave.add(inAppDelivery);

            // Add EMAIL delivery if email provided and user is opted in
            if (StringUtils.hasText(info.getEmail()) && isChannelEnabled(info.getUserId(), template, "EMAIL")) {
                NotificationDelivery emailDelivery = NotificationDelivery.builder()
                        .recipientRecord(recipient)
                        .channel("EMAIL")
                        .destination(info.getEmail())
                        .status("PENDING")
                        .build();
                deliveriesToSave.add(emailDelivery);
            }

            // Add FCM_PUSH delivery if token provided and user is opted in
            if (StringUtils.hasText(info.getDeviceToken()) && isChannelEnabled(info.getUserId(), template, "FCM_PUSH")) {
                NotificationDelivery pushDelivery = NotificationDelivery.builder()
                        .recipientRecord(recipient)
                        .channel("FCM_PUSH")
                        .destination(info.getDeviceToken())
                        .status("PENDING")
                        .build();
                deliveriesToSave.add(pushDelivery);
            }

            recipientsToSave.add(recipient);
        }

        recipientRepository.saveAll(recipientsToSave);
        deliveryRepository.saveAll(deliveriesToSave);

        log.info("Successfully scheduled {} deliveries for payload {}", deliveriesToSave.size(), payload.getId());
        return payload.getId();
    }

    private boolean isChannelEnabled(UUID userId, NotificationTemplate template, String channel) {
        return preferenceRepository.findByUserIdAndTemplateAndChannel(userId, template, channel)
                .map(UserNotificationPreference::getIsEnabled)
                .orElse(true); // Default to true if no explicit preference exists
    }

    @Override
    @Transactional
    public void broadcastNotification(String templateCode, java.util.Map<String, Object> contextData, UUID actorId) {
        log.info("Broadcasting notification for template: {}", templateCode);
        // Here we would typically fetch all users via FeignClient to coreservice or paginated DB query
        // For the sake of the notification service scope, this is a placeholder indicating where the call goes
        log.warn("Broadcast triggered! A real implementation would fetch all users from User Service and map to recipients.");
    }

    @Override
    @Transactional
    public void sendDirectEmail(String email, String templateCode, java.util.Map<String, Object> contextData) {
        log.info("Sending direct email to {} using template {}", email, templateCode);
        // This simulates placing a direct Email dispatch without triggering In-App alerts
        NotificationDelivery delivery = NotificationDelivery.builder()
                .channel("EMAIL")
                .destination(email)
                .status("PENDING")
                .build();
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void sendDirectZalo(String zaloPhone, String zaloTemplateId, java.util.Map<String, Object> contextData) {
        log.info("Sending direct Zalo to {} using Zalo-specific logic", zaloPhone);
        NotificationDelivery delivery = NotificationDelivery.builder()
                .channel("ZALO")
                .destination(zaloPhone)
                .status("PENDING")
                .build();
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        // Should fetch all unread for user and update. We need a custom query in repository for this.
    }

    @Override
    public long getUnreadCount(UUID userId) {
        // Needs a custom repository count query
        return 0;
    }

    @Override
    @Transactional
    public void upsertPreference(UUID userId, String templateCode, String channel, boolean isEnabled) {
        log.info("Upserting preference for user {}: {} -> {} = {}", userId, templateCode, channel, isEnabled);
        NotificationTemplate template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template code not found: " + templateCode));

        UserNotificationPreference preference = preferenceRepository.findByUserIdAndTemplateAndChannel(userId, template, channel)
                .orElse(UserNotificationPreference.builder()
                        .userId(userId)
                        .template(template)
                        .channel(channel)
                        .build());
                        
        preference.setIsEnabled(isEnabled);
        preferenceRepository.save(preference);
    }

    @Override
    @Transactional
    public void markAsRead(UUID recipientRecordId) {
        NotificationRecipient recipient = recipientRepository.findById(recipientRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient record not found: " + recipientRecordId));
        
        recipient.setIsRead(true);
        recipient.setReadAt(OffsetDateTime.now());
        recipientRepository.save(recipient);
    }
}
