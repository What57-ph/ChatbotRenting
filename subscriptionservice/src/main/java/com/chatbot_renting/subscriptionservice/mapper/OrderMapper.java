package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SubscriptionPlanMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order entity);
}
