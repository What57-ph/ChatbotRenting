package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubscriptionDto {
    private UUID id;
    private UUID userId;
    private String status;
    private SubscriptionPlanDto plan;
    private SubscriptionPlanDto previousPlan;
    private SubscriptionPlanDto scheduledPlan;
    private Boolean autoRenew;
    private LocalDateTime startDate;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime cancelledAt;
}
