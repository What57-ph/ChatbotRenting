package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanFeatureResponse {

    private Long id;

    private String featureKey;

    private String featureValue;
}
