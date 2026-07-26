package com.chatbot_renting.subscriptionservice.dto.request;

import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceStatusUpdateRequest {
    @NotNull
    private InvoiceStatus status;
    private String paymentMethod;
    private String paymentReference;
    private String notes;
}
