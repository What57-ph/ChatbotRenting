package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SubscriptionPlanResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private BigDecimal monthlyPrice;

    private BigDecimal yearlyPrice;

    private Integer maxChatbots;

    private Integer maxFiles;

    private Integer maxStorageMb;

    private Long maxMonthlyTokens;

    private Boolean active;

    private List<PlanFeatureResponse> features;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
