package com.chatbot_renting.subscriptionservice.dto.request;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionCreateRequest {
    @NotNull
    private Long planId;

    @NotNull
    private BillingCycle billingCycle;
}
