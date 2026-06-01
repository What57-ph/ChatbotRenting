package com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan;

import com.chatbot_renting.subscriptionservice.dto.request.PlanFeature.CreatePlanFeatureRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateSubscriptionPlanRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal monthlyPrice;

    @NotNull
    private BigDecimal yearlyPrice;

    private Integer maxChatbots;

    private Integer maxFiles;

    private Integer maxStorageMb;

    private Long maxMonthlyTokens;

    @Valid
    private List<CreatePlanFeatureRequest> features;
}
