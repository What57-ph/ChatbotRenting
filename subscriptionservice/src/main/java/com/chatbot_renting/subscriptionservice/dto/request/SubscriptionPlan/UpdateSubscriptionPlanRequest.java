package com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan;

import com.chatbot_renting.subscriptionservice.dto.request.PlanFeature.CreatePlanFeatureRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionPlanRequest {

    private String name;

    private String description;

    private BigDecimal monthlyPrice;

    private BigDecimal yearlyPrice;

    private Integer maxChatbots;

    private Integer maxFiles;

    private Integer maxStorageMb;

    private Long maxMonthlyTokens;

    private Boolean active;

    @Valid
    private List<CreatePlanFeatureRequest> features;
}
