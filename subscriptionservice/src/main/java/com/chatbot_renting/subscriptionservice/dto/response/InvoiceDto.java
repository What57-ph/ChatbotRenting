package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class InvoiceDto {
    private UUID id;
    private String invoiceNumber;
    private Double amount;
    private String currency;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
    private LocalDateTime dueDate;
    private String paymentMethod;
    
    private OrderDto order;
}
