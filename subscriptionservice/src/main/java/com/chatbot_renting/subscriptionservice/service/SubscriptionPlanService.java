package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.CreateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.UpdateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanResponse;
import java.util.List;
public interface SubscriptionPlanService {

    SubscriptionPlanResponse createSubscriptionPlan(
            CreateSubscriptionPlanRequest request);

    SubscriptionPlanResponse updateSubscriptionPlan(
            Long id,
            UpdateSubscriptionPlanRequest request);

    SubscriptionPlanResponse getSubscriptionPlan(Long id);

    List<SubscriptionPlanResponse> getAllSubscriptionPlans();

    List<SubscriptionPlanResponse> getActiveSubscriptionPlans();

    SubscriptionPlanResponse activatePlan(Long id);

    SubscriptionPlanResponse deactivatePlan(Long id);

    SubscriptionPlanResponse deletePlan(Long id);
}
