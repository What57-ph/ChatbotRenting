package com.chatbot_renting.subscriptionservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.chatbot_renting.subscriptionservice.entity.Invoice;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import com.chatbot_renting.subscriptionservice.repository.InvoiceRepository;
import com.chatbot_renting.subscriptionservice.service.InvoiceService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public Invoice createInvoice(Order order) {

        invoiceRepository.findByOrderId(order.getId())
                .ifPresent(inv -> {
                    throw new IllegalStateException("Invoice already exists for this order");
                });

        Invoice invoice = new Invoice();

        invoice.setOrder(order);
        invoice.setAmount(order.getAmount());
        invoice.setCurrency(order.getCurrency());

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setPaidAt(LocalDateTime.now());

        invoice.setInvoiceNumber(generateInvoiceNumber());

        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public Invoice markPaid(Invoice invoice) {

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return invoice;
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public Invoice markRefund(Invoice invoice) {

        if (invoice.getStatus() == InvoiceStatus.REFUNDED) {
            return invoice;
        }

        invoice.setStatus(InvoiceStatus.REFUNDED);

        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}
