package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.response.PlanFeatureDto;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.utils.PlanUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SubscriptionPlanMapper {

    @Autowired
    protected PlanUtils planUtils;

    @Mapping(target = "monthlyPrice", ignore = true)
    @Mapping(target = "yearlyPrice", ignore = true)
    public abstract SubscriptionPlanDto toDto(SubscriptionPlan entity);

    public abstract PlanFeatureDto toDto(PlanFeature entity);

    @AfterMapping
    protected void enrichPrices(@MappingTarget SubscriptionPlanDto dto, SubscriptionPlan entity) {
        planUtils.enrichPlanDto(dto, entity);
    }
}
