package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.entity.Invoice;
import com.chatbot_renting.subscriptionservice.entity.Order;

public interface InvoiceService {
    Invoice createInvoice(Order order);

    Invoice markPaid(Invoice invoice);

    Invoice markRefund(Invoice invoice);

    Invoice getByOrderId(Long orderId);

    String generateInvoiceNumber();
}
