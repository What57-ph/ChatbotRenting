package com.chatbot_renting.notificationservice.dispatch;

import com.chatbot_renting.notificationservice.entity.NotificationDelivery;
import com.chatbot_renting.notificationservice.repository.NotificationDeliveryRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalDeliveryDispatcher implements DeliveryDispatchPublisher {

    private final NotificationDeliveryRepository deliveryRepository;
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void publish(UUID deliveryId, String channel) {
        log.info("Received dispatch request for delivery {} in channel {}", deliveryId, channel);

        if (!"EMAIL".equalsIgnoreCase(channel)) {
            log.warn("Channel {} is not supported by LocalDeliveryDispatcher yet.", channel);
            return;
        }

        NotificationDelivery delivery = deliveryRepository.findById(deliveryId).orElse(null);
        if (delivery == null) {
            log.error("Delivery {} not found in database.", deliveryId);
            return;
        }

        try {
            sendEmail(delivery);
            delivery.setStatus("SENT");
            delivery.setErrorMessage(null);
            deliveryRepository.save(delivery);
            log.info("Successfully sent email for delivery {}", deliveryId);
        } catch (Exception e) {
            log.error("Failed to send email for delivery {}: {}", deliveryId, e.getMessage());
            delivery.setStatus("FAILED");
            delivery.setErrorMessage(e.getMessage());
            delivery.setRetryCount(delivery.getRetryCount() == null ? 1 : delivery.getRetryCount() + 1);
            deliveryRepository.save(delivery);
        }
    }

    private void sendEmail(NotificationDelivery delivery) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Destination is the email address
        helper.setTo(delivery.getDestination());

        Map<String, Object> contextData = delivery.getRecipientRecord().getPayload().getContextData();

        String subject = "Notification from Chatbot Renting"; // Default fallback
        String htmlContent = "";

        if (contextData != null) {
            Object dbSubject = contextData.get(
                    com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest.ContextKeys.SUBJECT);
            if (dbSubject != null && !dbSubject.toString().trim().isEmpty()) {
                subject = dbSubject.toString();
            }

            Object dbHtml = contextData.get(
                    com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest.ContextKeys.HTML_CONTENT);
            if (dbHtml != null && !dbHtml.toString().trim().isEmpty()) {
                htmlContent = dbHtml.toString();
            }
        }

        if (htmlContent.isEmpty()) {
            log.warn("htmlContent is empty for delivery {}. Sending blank email or skipping.", delivery.getId());
        }

        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = isHtml
        mailSender.send(message);
    }
}
