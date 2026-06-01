package com.chatbot_renting.subscriptionservice.dto.response;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {

    private Long orderId;

    private String orderNumber;

    private Long userId;

    private Long planId;

    private String planName;

    private BigDecimal amount;

    private String currency;

    private BillingCycle billingCycle;

    private OrderStatus status;

    private Long subscriptionId;

    private Long invoiceId;

    private LocalDateTime createdAt;
}
