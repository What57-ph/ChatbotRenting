package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.InvoiceStatusUpdateResponse;
import java.util.UUID;

public interface InvoiceService {
    InvoiceStatusUpdateResponse updateInvoiceStatus(UUID invoiceId, InvoiceStatusUpdateRequest request);
}
