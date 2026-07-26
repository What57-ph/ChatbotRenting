package com.chatbot_renting.subscriptionservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionAutoRenewRequest {
    @NotNull
    private Boolean autoRenew;
}
