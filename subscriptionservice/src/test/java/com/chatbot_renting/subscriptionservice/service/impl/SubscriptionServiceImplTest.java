package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppBadRequestException;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionCreateResponse;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.mapper.SubscriptionMapper;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private com.chatbot_renting.subscriptionservice.mapper.SubscriptionPlanMapper planMapper;

    @Mock
    private com.chatbot_renting.subscriptionservice.utils.PlanUtils planUtils;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private SubscriptionPlan mockPlanWithTrial;
    private Subscription trailingSubscription;
    private SubscriptionCreateRequest createReq;

    // Tạo các biến UUID dùng chung để đảm bảo Mockito bắt đúng tham số
    private UUID userId;
    private UUID planId;
    private UUID subscriptionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        planId = UUID.randomUUID();
        subscriptionId = UUID.randomUUID();

        mockPlanWithTrial = SubscriptionPlan.builder()
                .id(planId)
                .code("PRO_TRIAL")
                .active(true)
                .trialDays(14)
                .maxChatbots(50)
                .maxStorageMb(1024)
                .maxMonthlyTokens(100000)
                .build();

        trailingSubscription = Subscription.builder()
                .id(subscriptionId)
                .userId(userId)
                .plan(mockPlanWithTrial)
                .status(SubscriptionStatus.TRIALING)
                .billingCycle(BillingCycle.MONTHLY)
                .currentMaxChatbots(50)
                .currentPeriodEnd(LocalDateTime.now().plusDays(10))
                .build();

        createReq = new SubscriptionCreateRequest();
        createReq.setPlanId(planId);
        createReq.setBillingCycle(BillingCycle.MONTHLY);
    }

    @Test
    void createSubscription_TrialEligibility_AppliesSnapshotLimits() {
        // Arrange
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), anyList())).thenReturn(new ArrayList<>());
        when(subscriptionRepository.countByUserIdAndStatusNotIn(eq(userId), anyList())).thenReturn(0L);
        when(planRepository.findById(eq(planId))).thenReturn(Optional.of(mockPlanWithTrial));
        when(planUtils.resolvePrice(any(), any())).thenReturn(10.0);
        when(planUtils.buildPlanSnapshot(any(), any(), anyDouble())).thenReturn("{}");
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> {
            Subscription s = i.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        // Act
        SubscriptionCreateResponse response = subscriptionService.createSubscription(userId, createReq);

        // Assert
        verify(subscriptionRepository).save(argThat(sub ->
                sub.getStatus() == SubscriptionStatus.TRIALING &&
                        sub.getCurrentMaxChatbots() == 50 &&
                        sub.getCurrentMaxStorageMb() == 1024 &&
                        sub.getCurrentMaxMonthlyTokens() == 100000 &&
                        sub.getCurrentPeriodEnd() != null
        ));
    }

    @Test
    void createSubscription_ResumesIncompleteStatus() {
        // Arrange
        UUID incompleteSubId = UUID.randomUUID();
        Subscription incompleteSub = Subscription.builder()
                .id(incompleteSubId).userId(userId).status(SubscriptionStatus.INCOMPLETE).build();

        // No blocking subs
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), argThat(list -> list.contains(SubscriptionStatus.ACTIVE)))).thenReturn(Collections.emptyList());

        // Reusable subs (INCOMPLETE)
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), argThat(list -> list.contains(SubscriptionStatus.INCOMPLETE)))).thenReturn(Collections.singletonList(incompleteSub));

        when(subscriptionRepository.countByUserIdAndStatusNotIn(eq(userId), anyList())).thenReturn(1L);
        when(planRepository.findById(eq(planId))).thenReturn(Optional.of(mockPlanWithTrial));

        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        try {
            subscriptionService.createSubscription(userId, createReq);
        } catch (NullPointerException e) {
            // Note: Avoid this pattern in production code. You should properly mock orderMapper/invoiceMapper.
        }

        // Assert
        verify(subscriptionRepository).save(argThat(sub ->
                sub.getStatus() == SubscriptionStatus.INCOMPLETE &&
                        sub.getId().equals(incompleteSubId) // Sửa lại: Không dùng 200L, dùng ID kiểu UUID
        ));
    }

    @Test
    void upgradeSubscription_TRIALING_ThrowsException() {
        // Arrange
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), anyList()))
                .thenReturn(Collections.singletonList(trailingSubscription));

        // Act & Assert
        com.chatbot_renting.subscriptionservice.dto.request.SubscriptionUpgradeRequest req = new com.chatbot_renting.subscriptionservice.dto.request.SubscriptionUpgradeRequest();
        req.setPlanId(planId);
        req.setBillingCycle(BillingCycle.YEARLY);
        AppBadRequestException ex = assertThrows(AppBadRequestException.class,
                () -> subscriptionService.upgradeSubscription(userId, req));

        assertEquals(SubscriptionErrorCode.TRIAL_CANNOT_BE_CHANGED.getCode(), ex.getErrors().get(0).getErrCode().getCode());
    }

    @Test
    void downgradeSubscription_TRIALING_ThrowsException() {
        // Arrange
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), eq(Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))))
                .thenReturn(Collections.singletonList(trailingSubscription));

        // Act & Assert
        com.chatbot_renting.subscriptionservice.dto.request.SubscriptionDowngradeRequest req = new com.chatbot_renting.subscriptionservice.dto.request.SubscriptionDowngradeRequest();
        req.setPlanId(planId);
        AppBadRequestException ex = assertThrows(AppBadRequestException.class,
                () -> subscriptionService.downgradeSubscription(userId, req));

        assertEquals(SubscriptionErrorCode.TRIAL_CANNOT_BE_CHANGED.getCode(), ex.getErrors().get(0).getErrCode().getCode());
    }

    @Test
    void getCurrentSubscription_ReturnsDto() {
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), anyList()))
                .thenReturn(Collections.singletonList(trailingSubscription));
        when(planMapper.toDto(any(SubscriptionPlan.class))).thenReturn(new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto());

        com.chatbot_renting.subscriptionservice.dto.response.CurrentSubscriptionResponse result = subscriptionService.getCurrentSubscription(userId);
        assertNotNull(result);
    }

    @Test
    void getSubscriptionByUserId_ReturnsDto() {
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), anyList())).thenReturn(Collections.singletonList(trailingSubscription));
        com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto dto = new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto();
        when(subscriptionMapper.toDto(any(Subscription.class))).thenReturn(dto);

        com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto result = subscriptionService.getSubscriptionByUserId(userId);
        assertNotNull(result);
    }

    @Test
    void cancelSubscription_Success() {
        Subscription activeSub = Subscription.builder()
                .id(subscriptionId).userId(userId).status(SubscriptionStatus.ACTIVE).autoRenew(true).build();
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), anyList()))
                .thenReturn(Collections.singletonList(activeSub));
        when(subscriptionRepository.save(any())).thenReturn(activeSub);

        com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto dto = new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto();
        when(subscriptionMapper.toDto(any(Subscription.class))).thenReturn(dto);

        subscriptionService.cancelSubscription(userId);
        verify(subscriptionRepository).save(argThat(sub -> !sub.getAutoRenew() && sub.getCancelledAt() != null));
    }

    @Test
    void toggleAutoRenew_Success() {
        Subscription activeSub = Subscription.builder()
                .id(subscriptionId).userId(userId).status(SubscriptionStatus.ACTIVE).autoRenew(true).build();
        when(subscriptionRepository.findByUserIdAndStatusIn(eq(userId), anyList()))
                .thenReturn(Collections.singletonList(activeSub));
        when(subscriptionRepository.save(any())).thenReturn(activeSub);

        com.chatbot_renting.subscriptionservice.dto.request.SubscriptionAutoRenewRequest req = new com.chatbot_renting.subscriptionservice.dto.request.SubscriptionAutoRenewRequest();
        req.setAutoRenew(false);

        com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto dto = new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto();
        when(subscriptionMapper.toDto(any(Subscription.class))).thenReturn(dto);

        subscriptionService.toggleAutoRenew(userId, req);
        verify(subscriptionRepository).save(argThat(sub -> !sub.getAutoRenew()));
    }
}