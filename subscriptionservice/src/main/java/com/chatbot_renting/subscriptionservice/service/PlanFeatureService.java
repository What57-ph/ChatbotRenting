package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.PlanFeature.CreatePlanFeatureRequest;
import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import java.util.List;

public interface PlanFeatureService {
    List<PlanFeature> saveAllPlanFeatures(
            List<CreatePlanFeatureRequest> request);
}
