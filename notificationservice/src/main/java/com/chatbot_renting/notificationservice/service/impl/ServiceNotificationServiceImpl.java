package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dispatch.DeliveryDispatchPublisher;
import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import com.chatbot_renting.notificationservice.entity.*;
import com.chatbot_renting.notificationservice.event.DeliveriesCreatedEvent;
import com.chatbot_renting.notificationservice.repository.*;
import com.chatbot_renting.notificationservice.service.ServiceNotificationService;
import com.chatbot_renting.notificationservice.client.UserAccountClient;
import com.chatbot_renting.notificationservice.dto.response.client.AllUsersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceNotificationServiceImpl implements ServiceNotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationPayloadRepository payloadRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DeliveryDispatchPublisher dispatchPublisher;
    private final UserAccountClient userAccountClient;

    @Override
    @Transactional
    public UUID sendNotification(NotificationSendRequest request) {
        log.info("Processing notification dispatch for template: {}", request.getTemplateCode());

        NotificationTemplate template = templateRepository.findByCode(request.getTemplateCode())
                .orElseThrow(
                        () -> new IllegalArgumentException("Template code not found: " + request.getTemplateCode()));

        NotificationPayload payload = NotificationPayload.builder()
                .template(template)
                .actorId(request.getActorId())
                .sourceEntityId(request.getSourceEntityId())
                .sourceEntityType(request.getSourceEntityType())
                .contextData(request.getContextData())
                .build();
        payload = payloadRepository.save(payload);

        List<UUID> userIds = request.getRecipients().stream()
                .map(NotificationSendRequest.RecipientInfo::getUserId)
                .collect(Collectors.toList());
        Map<String, Boolean> preferenceMap = preferenceRepository.findByUserIdInAndTemplate(userIds, template).stream()
                .collect(Collectors.toMap(
                        p -> p.getUserId() + "|" + p.getChannel(),
                        UserNotificationPreference::getIsEnabled));

        List<NotificationRecipient> recipientsToSave = new ArrayList<>();
        List<NotificationDelivery> deliveriesToSave = new ArrayList<>();

        for (NotificationSendRequest.RecipientInfo info : request.getRecipients()) {
            NotificationRecipient recipient = NotificationRecipient.builder()
                    .payload(payload)
                    .recipientId(info.getUserId())
                    .isRead(false)
                    .isDeleted(false)
                    .build();

            deliveriesToSave.add(NotificationDelivery.builder()
                    .recipientRecord(recipient)
                    .channel("IN_APP")
                    .status("PENDING")
                    .build());

            if (StringUtils.hasText(info.getEmail())
                    && preferenceMap.getOrDefault(info.getUserId() + "|EMAIL", true)) {
                deliveriesToSave.add(NotificationDelivery.builder()
                        .recipientRecord(recipient)
                        .channel("EMAIL")
                        .destination(info.getEmail())
                        .status("PENDING")
                        .build());
            }

            if (StringUtils.hasText(info.getDeviceToken())
                    && preferenceMap.getOrDefault(info.getUserId() + "|FCM_PUSH", true)) {
                deliveriesToSave.add(NotificationDelivery.builder()
                        .recipientRecord(recipient)
                        .channel("FCM_PUSH")
                        .destination(info.getDeviceToken())
                        .status("PENDING")
                        .build());
            }

            recipientsToSave.add(recipient);
        }

        recipientRepository.saveAll(recipientsToSave);
        List<NotificationDelivery> saved = deliveryRepository.saveAll(deliveriesToSave);

        publishDispatchEvent(saved);

        log.info("Successfully scheduled {} deliveries for payload {}", saved.size(), payload.getId());
        return payload.getId();
    }

    @Override
    @Transactional
    public void broadcastNotification(String templateCode, Map<String, Object> contextData, UUID actorId) {
        log.info("Broadcasting notification for template: {}", templateCode);
        
        AllUsersResponse response;
        try {
            response = userAccountClient.getAllUsers();
        } catch (Exception e) {
            log.error("Failed to fetch users from authservice for broadcast notification", e);
            return;
        }

        if (response == null || response.getAllUsers() == null || response.getAllUsers().isEmpty()) {
            log.warn("No users found to broadcast notification.");
            return;
        }

        List<AllUsersResponse.UserResponse> allUsers = response.getAllUsers();
        log.info("Fetched {} users for broadcast.", allUsers.size());
        
        int batchSize = 1000;
        for (int i = 0; i < allUsers.size(); i += batchSize) {
            List<AllUsersResponse.UserResponse> batch = allUsers.subList(i, Math.min(i + batchSize, allUsers.size()));
            
            List<NotificationSendRequest.RecipientInfo> recipients = batch.stream()
                .filter(user -> user.isActive())
                .map(user -> NotificationSendRequest.RecipientInfo.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());

            if (!recipients.isEmpty()) {
                NotificationSendRequest request = NotificationSendRequest.builder()
                        .templateCode(templateCode)
                        .contextData(contextData)
                        .actorId(actorId)
                        .recipients(recipients)
                        .build();

                sendNotification(request);
            }
        }
        
        log.info("Broadcast notification processing completed.");
    }

    @Override
    @Transactional
    public UUID sendDirectEmail(String email, String templateCode, Map<String, Object> contextData) {
        NotificationSendRequest request = NotificationSendRequest.builder()
                .templateCode(templateCode)
                .contextData(contextData)
                .recipients(List.of(NotificationSendRequest.RecipientInfo.builder()
                        .userId(UUID.randomUUID())
                        .email(email)
                        .build()))
                .build();
        return sendNotification(request);
    }

    @Override
    @Transactional
    public UUID sendDirectZalo(String zaloPhone, String templateCode, Map<String, Object> contextData) {
        NotificationTemplate template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template code not found: " + templateCode));

        NotificationPayload payload = payloadRepository.save(NotificationPayload.builder()
                .template(template)
                .contextData(contextData)
                .build());

        NotificationRecipient recipient = recipientRepository.save(NotificationRecipient.builder()
                .payload(payload)
                .recipientId(UUID.randomUUID())
                .isRead(false)
                .isDeleted(false)
                .build());

        NotificationDelivery delivery = deliveryRepository.save(NotificationDelivery.builder()
                .recipientRecord(recipient)
                .channel("ZALO")
                .destination(zaloPhone)
                .status("PENDING")
                .build());

        publishDispatchEvent(List.of(delivery));
        return payload.getId();
    }

    @Override
    @Transactional
    public void markDeliverySent(UUID deliveryId) {
        NotificationDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));
        delivery.setStatus("SENT");
        delivery.setErrorMessage(null);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void markDeliveryFailed(UUID deliveryId, String errorMessage) {
        NotificationDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));
        delivery.setStatus("FAILED");
        delivery.setErrorMessage(errorMessage);
        delivery.setRetryCount(delivery.getRetryCount() == null ? 1 : delivery.getRetryCount() + 1);
        deliveryRepository.save(delivery);
    }

    private void publishDispatchEvent(List<NotificationDelivery> deliveries) {
        List<DeliveriesCreatedEvent.DeliveryRef> refs = deliveries.stream()
                .filter(d -> !"IN_APP".equals(d.getChannel()))
                .map(d -> new DeliveriesCreatedEvent.DeliveryRef(d.getId(), d.getChannel()))
                .collect(Collectors.toList());
        if (!refs.isEmpty()) {
            eventPublisher.publishEvent(new DeliveriesCreatedEvent(this, refs));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeliveriesCreated(DeliveriesCreatedEvent event) {
        event.getDeliveries().forEach(ref -> dispatchPublisher.publish(ref.deliveryId(), ref.channel()));
    }
}
