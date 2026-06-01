package com.chatbot_renting.subscriptionservice.dto.request.Order;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {

    private Long userId;

    private Long planId;

    private BillingCycle billingCycle;

    private BigDecimal amount;
}
