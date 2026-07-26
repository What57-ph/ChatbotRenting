package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.InvoiceStatusUpdateResponse;

public interface InvoiceService {
    InvoiceStatusUpdateResponse updateInvoiceStatus(Long invoiceId, InvoiceStatusUpdateRequest request);
}
