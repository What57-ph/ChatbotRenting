package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Double amount;
    private String currency;
    private String billingCycle;
    private String status;
    private String orderType;
    private LocalDateTime createdAt;
    
    private SubscriptionPlanDto plan;
    private InvoiceDto invoice;
}
