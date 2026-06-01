package com.chatbot_renting.subscriptionservice.service;


import com.chatbot_renting.subscriptionservice.dto.request.Order.CreateOrderRequest;
import com.chatbot_renting.subscriptionservice.dto.response.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse payOrder(Long orderId, Long userId);

    OrderResponse cancelOrder(Long orderId, String reason);

    OrderResponse failOrder(Long orderId, String reason);

    OrderResponse refundOrder(Long orderId, String reason);

    OrderResponse getOrder(Long orderId);
    List<OrderResponse> getAllOrder(Pageable pageable);

    List<OrderResponse> getOrdersByUser(Long userId);
}
