package com.chatbot_renting.notificationservice.service.impl;

import com.chatbot_renting.notificationservice.dispatch.DeliveryDispatchPublisher;
import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import com.chatbot_renting.notificationservice.dto.response.NotificationTimelineResponse;
import com.chatbot_renting.notificationservice.dto.response.PreferenceResponse;
import com.chatbot_renting.notificationservice.entity.*;
import com.chatbot_renting.notificationservice.event.DeliveriesCreatedEvent;
import com.chatbot_renting.notificationservice.repository.*;
import com.chatbot_renting.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationPayloadRepository payloadRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DeliveryDispatchPublisher dispatchPublisher;

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
    public List<PreferenceResponse> getUserPreferences(UUID userId) {
        return preferenceRepository.findByUserId(userId).stream()
                .map(p -> PreferenceResponse.builder()
                        .templateCode(p.getTemplate() != null ? p.getTemplate().getCode() : null)
                        .channel(p.getChannel())
                        .isEnabled(p.getIsEnabled())
                        .build())
                .collect(Collectors.toList());
    }

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

        // Load preference 1 lần cho toàn bộ recipient, tránh N+1 query trong loop
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

            // IN_APP luôn tạo — baseline không cho tắt qua preference
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
        // TODO: fetch all target userIds via FeignClient tới user-service (phân trang),
        // sau đó build NotificationSendRequest và gọi sendNotification() theo batch,
        // KHÔNG load hết user vào 1 request để tránh OOM khi user lớn.
        log.warn("Broadcast triggered! Cần implement fetch users từ User Service.");
    }

    @Override
    @Transactional
    public UUID sendDirectEmail(UUID userId, String email, String templateCode, Map<String, Object> contextData) {
        // Tái sử dụng chung pipeline thay vì tạo Delivery mồ côi — đảm bảo có
        // Payload/Recipient
        // để user xem lại lịch sử, và Delivery luôn có recipientRecord hợp lệ.
        NotificationSendRequest request = NotificationSendRequest.builder()
                .templateCode(templateCode)
                .contextData(contextData)
                .recipients(List.of(NotificationSendRequest.RecipientInfo.builder()
                        .userId(userId)
                        .email(email)
                        .build()))
                .build();
        return sendNotification(request);
    }

    @Override
    @Transactional
    public UUID sendDirectZalo(UUID userId, String zaloPhone, String templateCode, Map<String, Object> contextData) {
        // Lưu ý: pipeline hiện tại chưa có channel "ZALO" trong nhánh sendNotification,
        // cần thêm field zaloPhone vào RecipientInfo + nhánh xử lý riêng nếu muốn dùng
        // chung.
        // Tạm thời giữ logic riêng nhưng fix bug thiếu recipientRecord:
        NotificationTemplate template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template code not found: " + templateCode));

        NotificationPayload payload = payloadRepository.save(NotificationPayload.builder()
                .template(template)
                .contextData(contextData)
                .build());

        NotificationRecipient recipient = recipientRepository.save(NotificationRecipient.builder()
                .payload(payload)
                .recipientId(userId)
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
    public void markAsRead(UUID userId, UUID recipientRecordId) {
        NotificationRecipient recipient = recipientRepository.findByIdAndRecipientId(recipientRecordId, userId)
                .orElseThrow(() -> new AccessDeniedException("Notification not found or not owned by user"));

        if (Boolean.TRUE.equals(recipient.getIsRead())) {
            return; // idempotent, đã đọc rồi thì thôi
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
                .orElseThrow(() -> new AccessDeniedException("Notification not found or not owned by user"));

        recipient.setIsDeleted(true);
        recipient.setDeletedAt(OffsetDateTime.now());
        recipientRepository.save(recipient);
    }

    @Override
    public long getUnreadCount(UUID userId) {
        return recipientRepository.countByRecipientIdAndIsReadFalseAndIsDeletedFalse(userId);
    }

    @Override
    @Transactional
    public void upsertPreference(UUID userId, String templateCode, String channel, boolean isEnabled) {
        log.info("Upserting preference for user {}: {} -> {} = {}", userId, templateCode, channel, isEnabled);
        NotificationTemplate template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template code not found: " + templateCode));

        UserNotificationPreference preference = preferenceRepository
                .findByUserIdAndTemplateAndChannel(userId, template, channel)
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

    // ---- helpers ----

    private void publishDispatchEvent(List<NotificationDelivery> deliveries) {
        List<DeliveriesCreatedEvent.DeliveryRef> refs = deliveries.stream()
                .filter(d -> !"IN_APP".equals(d.getChannel())) // IN_APP không cần dispatch ra ngoài
                .map(d -> new DeliveriesCreatedEvent.DeliveryRef(d.getId(), d.getChannel()))
                .collect(Collectors.toList());
        if (!refs.isEmpty()) {
            eventPublisher.publishEvent(new DeliveriesCreatedEvent(this, refs));
        }
    }

    // Publish message lên queue CHỈ SAU KHI transaction đã commit thành công,
    // tránh consumer đọc deliveryId trước khi DB transaction ghi xong.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeliveriesCreated(DeliveriesCreatedEvent event) {
        event.getDeliveries().forEach(ref -> dispatchPublisher.publish(ref.deliveryId(), ref.channel()));
    }
}