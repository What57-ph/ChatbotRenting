package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.request.PlanFeature.CreatePlanFeatureRequest;
import com.chatbot_renting.subscriptionservice.dto.response.PlanFeatureResponse;
import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanFeatureMapper {

    PlanFeature toEntity(CreatePlanFeatureRequest request);

    PlanFeatureResponse toResponse(PlanFeature entity);
}
