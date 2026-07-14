package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;

@Data
public class InvoiceStatusUpdateResponse {
    private Long invoiceId;
    private String invoiceStatus;
    private String orderStatus;
    private String subscriptionStatus;
    private String currentPeriodStart;
    private String currentPeriodEnd;
    private String message;
}
