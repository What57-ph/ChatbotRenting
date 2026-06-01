package com.chatbot_renting.subscriptionservice.dto.request.PlanFeature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanFeatureRequest {

    private String featureKey;

    private String featureValue;
}
