package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.subscriptionservice.dto.request.UsageCheckRequest;
import com.chatbot_renting.subscriptionservice.dto.response.UsageCheckResponse;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.UsageSummary;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.UsageType;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.repository.UsageRecordRepository;
import com.chatbot_renting.subscriptionservice.repository.UsageSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chatbot_renting.subscriptionservice.dto.request.UsageRecordRequest;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageServiceImplTest {

    @Mock
    private UsageSummaryRepository usageSummaryRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @InjectMocks
    private UsageServiceImpl usageService;

    @Test
    void checkUsage_UsesSnapshotLimits_NotPlanLimits() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();

        SubscriptionPlan livePlan = SubscriptionPlan.builder()
                .maxChatbots(50) // Live plan allows 50
                .build();

        Subscription sub = Subscription.builder()
                .id(subscriptionId)
                .status(SubscriptionStatus.ACTIVE)
                .plan(livePlan)
                .currentMaxChatbots(10) // Snapshot allows only 10
                .currentPeriodStart(java.time.LocalDateTime.now())
                .currentPeriodEnd(java.time.LocalDateTime.now().plusMonths(1))
                .build();

        UsageSummary summary = UsageSummary.builder()
                .chatbotCount(10) // They already have 10
                .build();

        // Sử dụng biến userId và subscriptionId đã khởi tạo
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), any()))
                .thenReturn(Collections.singletonList(sub));
        when(usageSummaryRepository.findBySubscriptionIdAndPeriodStart(eq(subscriptionId), any()))
                .thenReturn(java.util.Optional.of(summary));

        UsageCheckRequest req = new UsageCheckRequest();
        req.setUserId(userId); // Truyền đúng userId đã mock
        req.setUsageType(UsageType.CHATBOT_CREATED);

        // Act
        UsageCheckResponse res = usageService.checkQuota(req);

        // Assert
        assertFalse(res.getAllowed());
        assertEquals("CHATBOT_LIMIT_EXCEEDED", res.getReason());
        assertEquals(10L, res.getLimit());
        assertEquals(10L, res.getCurrent());
    }

    @Test
    void recordUsage_CreatesNewSummaryIfNoneExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();

        Subscription sub = Subscription.builder()
                .id(subscriptionId)
                .status(SubscriptionStatus.ACTIVE)
                .currentPeriodStart(java.time.LocalDateTime.now())
                .currentPeriodEnd(java.time.LocalDateTime.now().plusDays(30))
                .build();

        // Sử dụng biến userId và subscriptionId đã khởi tạo
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), any()))
                .thenReturn(Collections.singletonList(sub));
        when(usageSummaryRepository.findBySubscriptionIdAndPeriodStart(eq(subscriptionId), any()))
                .thenReturn(java.util.Optional.empty()); // No existing summary

        UsageRecordRequest req = new UsageRecordRequest();
        req.setUserId(userId); // Truyền đúng userId đã mock
        req.setUsageType(UsageType.TOKEN_USED);
        req.setQuantity(50);

        // Act
        usageService.recordUsage(req);

        // Assert
        org.mockito.Mockito.verify(usageRecordRepository).save(any());
        org.mockito.Mockito.verify(usageSummaryRepository).save(argThat(summary ->
                summary.getTokensUsed() == 50
        ));
    }
}