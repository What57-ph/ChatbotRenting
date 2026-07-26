package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class InvoiceStatusUpdateResponse {
    private UUID invoiceId;
    private String invoiceStatus;
    private String orderStatus;
    private String subscriptionStatus;
    private String currentPeriodStart;
    private String currentPeriodEnd;
    private String message;
}
