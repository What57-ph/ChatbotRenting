package com.chatbot_renting.subscriptionservice.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.List;

@Data
public class SubscriptionPlanCreateRequest {
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "maxChatbots is required")
    @Min(0)
    private Integer maxChatbots;
    
    @NotNull(message = "maxStorageMb is required")
    @Min(0)
    private Integer maxStorageMb;
    
    @NotNull(message = "maxMonthlyTokens is required")
    @Min(0)
    private Integer maxMonthlyTokens;
    
    private Integer durationMonths = 1;
    
    private Integer trialDays = 0;
    
    private List<PlanFeatureRequest> features;
}
