package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.request.Order.CreateOrderRequest;
import com.chatbot_renting.subscriptionservice.dto.response.*;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "subscription.id", target = "subscriptionId")
    @Mapping(source = "invoice.id", target = "invoiceId")
    OrderResponse toResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus.PENDING)")
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    Order toEntity(CreateOrderRequest request);
}
