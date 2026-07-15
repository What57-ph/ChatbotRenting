package com.chatbot_renting.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendRequest {

    @NotBlank
    private String templateCode;

    private UUID actorId;

    @NotBlank
    private String sourceEntityId;

    @NotBlank
    private String sourceEntityType;

    private Map<String, Object> contextData;

    @NotNull
    private List<RecipientInfo> recipients;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientInfo {
        @NotNull
        private UUID userId;
        
        private String email;
        private String phone;
        private String deviceToken;
    }
}
