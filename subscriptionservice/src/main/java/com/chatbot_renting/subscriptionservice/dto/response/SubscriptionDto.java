package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubscriptionDto {
    private Long id;
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
