package com.chatbot_renting.subscriptionservice.rest.controller;

import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.InvoiceStatusUpdateResponse;
import com.chatbot_renting.subscriptionservice.rest.api.CallbackInvoiceApi;
import com.chatbot_renting.subscriptionservice.service.InvoiceService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CallbackInvoiceController implements CallbackInvoiceApi {

    private final InvoiceService invoiceService;

    @Override
    public ResponseEntity<InvoiceStatusUpdateResponse> updateInvoiceStatus(UUID invoiceId, InvoiceStatusUpdateRequest request) {
        log.info("Callback received to update invoice {} status to {}", invoiceId, request.getStatus());
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId, request));
    }
}
