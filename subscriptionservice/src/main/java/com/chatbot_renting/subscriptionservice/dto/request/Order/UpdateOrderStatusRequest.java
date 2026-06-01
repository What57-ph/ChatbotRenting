package com.chatbot_renting.subscriptionservice.dto.request.Order;

import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}
