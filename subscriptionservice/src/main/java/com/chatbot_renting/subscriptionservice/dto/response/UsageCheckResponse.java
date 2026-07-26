package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsageCheckResponse {
    private Boolean allowed;
    private String reason;
    private Long current;
    private Long limit;
    private Long remaining;
    private Boolean upgradeRequired;
    private String message;
}
