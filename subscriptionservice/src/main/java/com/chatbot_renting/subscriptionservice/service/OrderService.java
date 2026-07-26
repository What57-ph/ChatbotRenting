package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.dto.response.PagedResponse;

public interface OrderService {
    PagedResponse<OrderDto> getUserOrders(Long userId, int page, int limit, String status);
    OrderDto getOrder(Long userId, Long orderId);
}
