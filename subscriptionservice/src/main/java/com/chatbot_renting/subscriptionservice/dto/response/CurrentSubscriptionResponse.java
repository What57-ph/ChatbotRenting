package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;

@Data
public class CurrentSubscriptionResponse {
    private Long id;
    private String status;
    private Boolean autoRenew;
    private String startDate;
    private String currentPeriodStart;
    private String currentPeriodEnd;
    private String cancelledAt;
    private SubscriptionPlanDto plan;
    private UsageSummaryDto usage;
}
