package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.entity.Invoice;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderType;
import com.chatbot_renting.subscriptionservice.repository.InvoiceRepository;
import com.chatbot_renting.subscriptionservice.repository.OrderRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.repository.UsageSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private UsageSummaryRepository usageSummaryRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Test
    void updateInvoiceStatus_PAID_SnapshotsLimits() {
        // Arrange
        UUID planId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID(); // Khởi tạo ID cố định

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(planId)
                .maxChatbots(100)
                .maxStorageMb(2048)
                .maxMonthlyTokens(1000)
                .durationMonths(1)
                .build();

        Subscription subscription = Subscription.builder()
                .id(subscriptionId)
                .plan(plan)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .orderType(OrderType.NEW_SUBSCRIPTION)
                .subscription(subscription)
                .build();

        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .status(InvoiceStatus.UNPAID)
                .order(order)
                .build();

        // Dùng invoiceId đã khởi tạo
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        InvoiceStatusUpdateRequest req = new InvoiceStatusUpdateRequest();
        req.setStatus(InvoiceStatus.PAID);

        // Act - Truyền đúng invoiceId vào hàm
        invoiceService.updateInvoiceStatus(invoiceId, req);

        // Assert
        verify(subscriptionRepository).save(org.mockito.ArgumentMatchers.argThat(sub ->
                sub.getCurrentMaxChatbots() != null && sub.getCurrentMaxChatbots() == 100 &&
                        sub.getCurrentMaxStorageMb() != null && sub.getCurrentMaxStorageMb() == 2048
        ));
    }

    @Test
    void updateInvoiceStatus_NotFound_ThrowsException() {
        // Arrange
        UUID invoiceId = UUID.randomUUID();
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        InvoiceStatusUpdateRequest req = new InvoiceStatusUpdateRequest();
        req.setStatus(InvoiceStatus.PAID);

        // Act & Assert
        assertThrows(AppNotFoundException.class, () -> invoiceService.updateInvoiceStatus(invoiceId, req));
    }
}