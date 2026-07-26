package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import java.util.List;

public interface SubscriptionPlanService {
    List<SubscriptionPlanDto> getActivePlans();
    List<SubscriptionPlanDto> getAllPlans();
    SubscriptionPlanDto getPlan(Long planId);
    SubscriptionPlanDto createPlan(com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest request);
    SubscriptionPlanDto updatePlan(Long planId, com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest request);
    void softDeletePlan(Long planId);
}
