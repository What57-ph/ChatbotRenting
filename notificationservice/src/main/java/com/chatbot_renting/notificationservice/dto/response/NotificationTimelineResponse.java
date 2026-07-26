package com.chatbot_renting.notificationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTimelineResponse {
    private UUID id;
    private String templateCode;
    private String sourceEntityId;
    private String sourceEntityType;
    private Object contextData; // Raw parsed JSON or String
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
