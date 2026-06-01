package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.CreateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanResponse;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = PlanFeatureMapper.class
)
public interface SubscriptionPlanMapper {

    SubscriptionPlan toEntity(CreateSubscriptionPlanRequest request);

    SubscriptionPlanResponse toResponse(SubscriptionPlan entity);
}
