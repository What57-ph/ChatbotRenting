package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.InvoiceStatusUpdateResponse;
import com.chatbot_renting.subscriptionservice.entity.Invoice;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .maxChatbots(100)
                .maxStorageMb(2048)
                .maxMonthlyTokens(1000)
                .durationMonths(1)
                .build();
                
        Subscription subscription = Subscription.builder()
                .id(1L)
                .plan(plan)
                .build();
                
        Order order = Order.builder()
                .id(1L)
                .orderType(OrderType.NEW_SUBSCRIPTION)
                .subscription(subscription)
                .build();
                
        Invoice invoice = Invoice.builder()
                .id(1L)
                .status(InvoiceStatus.UNPAID)
                .order(order)
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        
        InvoiceStatusUpdateRequest req = new InvoiceStatusUpdateRequest();
        req.setStatus(InvoiceStatus.PAID);
        
        // Act
        invoiceService.updateInvoiceStatus(1L, req);
        
        // Assert
        verify(subscriptionRepository).save(org.mockito.ArgumentMatchers.argThat(sub -> 
            sub.getCurrentMaxChatbots() != null && sub.getCurrentMaxChatbots() == 100 &&
            sub.getCurrentMaxStorageMb() != null && sub.getCurrentMaxStorageMb() == 2048
        ));
    }

    @Test
    void updateInvoiceStatus_NotFound_ThrowsException() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());
        
        InvoiceStatusUpdateRequest req = new InvoiceStatusUpdateRequest();
        req.setStatus(InvoiceStatus.PAID);
        
        assertThrows(AppNotFoundException.class, () -> invoiceService.updateInvoiceStatus(1L, req));
    }
}
