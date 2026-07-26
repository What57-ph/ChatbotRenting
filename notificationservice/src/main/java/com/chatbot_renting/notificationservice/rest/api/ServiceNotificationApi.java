package com.chatbot_renting.notificationservice.rest.api;

import com.chatbot_renting.notificationservice.dto.request.BroadcastRequest;
import com.chatbot_renting.notificationservice.dto.request.DirectEmailRequest;
import com.chatbot_renting.notificationservice.dto.request.NotificationSendRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/service-api/v1/notifications")
public interface ServiceNotificationApi {

    @PostMapping("/dispatch")
    ResponseEntity<UUID> dispatchNotification(@Valid @RequestBody NotificationSendRequest request);

    @PostMapping("/broadcast")
    ResponseEntity<Void> broadcastNotification(@Valid @RequestBody BroadcastRequest request);

    @PostMapping("/direct-email")
    ResponseEntity<Void> sendDirectEmail(@Valid @RequestBody DirectEmailRequest request);
}
