package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreateResponse {
    private SubscriptionDto subscription;
    private OrderDto order;
    private InvoiceDto invoice;
}
