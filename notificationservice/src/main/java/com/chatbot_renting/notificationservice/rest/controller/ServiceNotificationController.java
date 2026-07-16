package com.chatbot_renting.notificationservice.rest.controller;

import com.chatbot_renting.notificationservice.dto.request.BroadcastRequest;
import com.chatbot_renting.notificationservice.dto.request.DirectEmailRequest;
import com.chatbot_renting.notificationservice.dto.request.DirectZaloRequest;
import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import com.chatbot_renting.notificationservice.rest.api.ServiceNotificationApi;
import com.chatbot_renting.notificationservice.service.ServiceNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ServiceNotificationController implements ServiceNotificationApi {

    private final ServiceNotificationService notificationService;

    @Override
    public ResponseEntity<UUID> dispatchNotification(NotificationSendRequest request) {
        UUID payloadId = notificationService.sendNotification(request);
        return new ResponseEntity<>(payloadId, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> broadcastNotification(BroadcastRequest request) {
        notificationService.broadcastNotification(request.getTemplateCode(), request.getContextData(),
                request.getActorId());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> sendDirectEmail(DirectEmailRequest request) {
        notificationService.sendDirectEmail(request.getEmail(), request.getTemplateCode(), request.getContextData());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> sendDirectZalo(DirectZaloRequest request) {
        notificationService.sendDirectZalo(request.getZaloIdentifier(), request.getZaloTemplateId(),
                request.getContextData());
        return ResponseEntity.ok().build();
    }
}
