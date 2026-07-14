package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class SubscriptionPlanDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Double monthlyPrice;
    private Double yearlyPrice;
    private Integer maxChatbots;
    private Integer maxStorageMb;
    private Integer maxMonthlyTokens;
    private Integer durationMonths;
    private Boolean active;
    private List<PlanFeatureDto> features;
}
