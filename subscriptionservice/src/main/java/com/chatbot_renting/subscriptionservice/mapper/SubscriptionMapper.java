package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SubscriptionPlanMapper.class})
public interface SubscriptionMapper {
    SubscriptionDto toDto(Subscription entity);
}
