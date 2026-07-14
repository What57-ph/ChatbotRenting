package com.chatbot_renting.subscriptionservice.rest.api;

import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.InvoiceStatusUpdateResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/callbacks/v1/invoices")
public interface CallbackInvoiceApi {

    @PostMapping("/{invoiceId}/status")
    ResponseEntity<InvoiceStatusUpdateResponse> updateInvoiceStatus(
            @PathVariable("invoiceId") Long invoiceId,
            @Valid @RequestBody InvoiceStatusUpdateRequest request);
}
