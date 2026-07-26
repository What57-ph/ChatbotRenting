package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private UUID userId;
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
