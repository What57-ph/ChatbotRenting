package com.chatbot_renting.subscriptionservice.dto.request.PlanFeature;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanFeatureRequest {

    @NotBlank
    private String featureKey;

    @NotBlank
    private String featureValue;
}
