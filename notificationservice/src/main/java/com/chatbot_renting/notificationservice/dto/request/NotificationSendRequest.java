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

    /**
     * Context data for the notification payload.
     * Expected metadata fields (from outside services):
     * - "subject" (String): The email subject (if channel isEMAIL).
     * - "htmlContent" (String): The raw compiled HTML body (if channel is EMAIL).
     * - "actionUrl" (String): URL for the user to click in In-App notifications.
     * - "imageUrl" (String): Avatar or icon image for notification.
     * - Any other dynamic variables needed for frontend rendering.
     */
    private Map<String, Object> contextData;

    /**
     * Optional constants class to strongly type the Keys pushed into contextData
     */
    public static class ContextKeys {
        public static final String SUBJECT = "subject";
        public static final String HTML_CONTENT = "htmlContent";
        public static final String ACTION_URL = "actionUrl";
        public static final String IMAGE_URL = "imageUrl";
    }

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
