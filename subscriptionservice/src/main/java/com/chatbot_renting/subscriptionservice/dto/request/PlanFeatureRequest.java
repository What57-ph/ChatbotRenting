package com.chatbot_renting.subscriptionservice.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PlanFeatureRequest {
    @NotBlank
    private String featureKey;
    
    @NotBlank
    private String featureValue;
}
