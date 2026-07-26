package com.chatbot_renting.subscriptionservice.dto.request;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class SubscriptionUpgradeRequest {
    @NotNull
    private UUID planId;

    @NotNull
    private BillingCycle billingCycle;
}
