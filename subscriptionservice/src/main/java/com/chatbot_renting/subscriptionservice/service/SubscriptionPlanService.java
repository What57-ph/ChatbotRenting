package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {
    List<SubscriptionPlanDto> getActivePlans();
    List<SubscriptionPlanDto> getAllPlans();
    SubscriptionPlanDto getPlan(UUID planId);
    SubscriptionPlanDto createPlan(com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest request);
    SubscriptionPlanDto updatePlan(UUID planId, com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest request);
    void softDeletePlan(UUID planId);
}
