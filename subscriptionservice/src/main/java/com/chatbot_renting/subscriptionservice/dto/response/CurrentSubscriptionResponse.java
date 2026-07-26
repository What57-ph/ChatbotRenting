package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CurrentSubscriptionResponse {
    private UUID id;
    private String status;
    private Boolean autoRenew;
    private String startDate;
    private String currentPeriodStart;
    private String currentPeriodEnd;
    private String cancelledAt;
    private SubscriptionPlanDto plan;
    private UsageSummaryDto usage;
}
