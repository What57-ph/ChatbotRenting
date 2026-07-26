package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.dto.response.PagedResponse;
import java.util.UUID;

public interface OrderService {
    PagedResponse<OrderDto> getUserOrders(UUID userId, int page, int limit, String status);
    OrderDto getOrder(UUID userId, UUID orderId);
}
