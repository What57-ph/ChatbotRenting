package com.chatbot_renting.subscriptionservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionDowngradeRequest {
    @NotNull
    private Long planId;
}
