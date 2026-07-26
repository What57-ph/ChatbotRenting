package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppBadRequestException;
import com.chatbot_renting.subscriptionservice.dto.request.PlanFeatureRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.mapper.SubscriptionPlanMapper;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.utils.PlanFeatureKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanServiceImplTest {

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private SubscriptionPlanMapper planMapper;

    @Mock
    private com.chatbot_renting.subscriptionservice.utils.PlanUtils planUtils;

    @InjectMocks
    private SubscriptionPlanServiceImpl planService;

    private SubscriptionPlanCreateRequest validCreateReq;

    // Khai báo biến ID dùng chung cho các bài test
    private UUID planId;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID(); // Khởi tạo ID cố định cho mỗi lần chạy test

        validCreateReq = new SubscriptionPlanCreateRequest();
        validCreateReq.setCode("BASIC");
        validCreateReq.setName("Basic Plan");
        validCreateReq.setMaxChatbots(10);
        validCreateReq.setMaxMonthlyTokens(5000);
        validCreateReq.setMaxStorageMb(1024);

        PlanFeatureRequest feat1 = new PlanFeatureRequest();
        feat1.setFeatureKey(PlanFeatureKeys.MONTHLY_PRICE);
        feat1.setFeatureValue("10");

        PlanFeatureRequest feat2 = new PlanFeatureRequest();
        feat2.setFeatureKey(PlanFeatureKeys.YEARLY_PRICE);
        feat2.setFeatureValue("100");

        validCreateReq.setFeatures(Arrays.asList(feat1, feat2));
    }

    @Test
    void createPlan_Success_HasPrices() {
        // Arrange
        when(planRepository.existsByCode("BASIC")).thenReturn(false);
        when(planRepository.save(any(SubscriptionPlan.class))).thenAnswer(i -> {
            SubscriptionPlan p = i.getArgument(0);
            p.setId(UUID.randomUUID()); // Ở đây dùng UUID.randomUUID() là hợp lý vì giả lập DB tự sinh ID
            return p;
        });

        // Act
        planService.createPlan(validCreateReq);

        // Assert
        verify(planRepository).save(argThat(plan ->
                plan.getCode().equals("BASIC") &&
                        plan.getFeatures().size() == 2 &&
                        plan.getActive()
        ));
    }

    @Test
    void createPlan_MissingPrices_ThrowsException() {
        // Arrange
        validCreateReq.setFeatures(Collections.emptyList());

        // Act & Assert
        AppBadRequestException ex = assertThrows(AppBadRequestException.class, () -> planService.createPlan(validCreateReq));
        assertEquals(SubscriptionErrorCode.PLAN_MISSING_PRICE.getCode(), ex.getErrors().get(0).getErrCode().getCode());
    }

    @Test
    void createPlan_DuplicateCode_ThrowsException() {
        // Arrange
        when(planRepository.existsByCode("BASIC")).thenReturn(true);

        // Act & Assert
        AppBadRequestException ex = assertThrows(AppBadRequestException.class, () -> planService.createPlan(validCreateReq));
        assertEquals(SubscriptionErrorCode.PLAN_CODE_ALREADY_EXISTS.getCode(), ex.getErrors().get(0).getErrCode().getCode());
    }

    @Test
    void updatePlan_UnlockedAttributes_SavesSuccessfully() {
        // Arrange
        SubscriptionPlan activePlan = SubscriptionPlan.builder().id(planId).active(true).build();

        // Sử dụng biến planId đã khởi tạo thay vì tạo mới
        when(planRepository.findById(planId)).thenReturn(Optional.of(activePlan));

        SubscriptionPlanUpdateRequest updateReq = new SubscriptionPlanUpdateRequest();
        updateReq.setName("Basic V2");
        updateReq.setActive(false);
        updateReq.setMaxChatbots(20);

        // Act
        planService.updatePlan(planId, updateReq);

        // Assert
        verify(planRepository).save(argThat(plan ->
                plan.getName().equals("Basic V2") &&
                        !plan.getActive() &&
                        plan.getMaxChatbots() == 20
        ));
    }

    @Test
    void getActivePlans_ReturnsList() {
        when(planRepository.findByActiveTrue()).thenReturn(Collections.singletonList(new SubscriptionPlan()));
        when(planMapper.toDto(any(SubscriptionPlan.class))).thenReturn(new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto());
        when(planUtils.monthlyPriceComparator()).thenReturn(java.util.Comparator.comparing(dto -> 0.0));

        java.util.List<com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto> result = planService.getActivePlans();
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllPlans_ReturnsList() {
        when(planRepository.findAll()).thenReturn(Collections.singletonList(new SubscriptionPlan()));
        when(planMapper.toDto(any(SubscriptionPlan.class))).thenReturn(new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto());
        when(planUtils.monthlyPriceComparator()).thenReturn(java.util.Comparator.comparing(dto -> 0.0));

        java.util.List<com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto> result = planService.getAllPlans();
        assertFalse(result.isEmpty());
    }

    @Test
    void getPlan_Exists_ReturnsDto() {
        // Sử dụng planId
        when(planRepository.findById(planId)).thenReturn(Optional.of(new SubscriptionPlan()));
        when(planMapper.toDto(any(SubscriptionPlan.class))).thenReturn(new com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto());

        com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto result = planService.getPlan(planId);
        assertNotNull(result);
    }

    @Test
    void getPlan_NotFound_ThrowsException() {
        // Sử dụng planId
        when(planRepository.findById(planId)).thenReturn(Optional.empty());
        assertThrows(com.chatbot_renting.commonservice.exception.AppNotFoundException.class, () -> planService.getPlan(planId));
    }

    @Test
    void softDeletePlan_Success() {
        // Sử dụng planId
        SubscriptionPlan plan = SubscriptionPlan.builder().id(planId).active(true).build();
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        planService.softDeletePlan(planId);

        verify(planRepository).save(argThat(p -> !p.getActive()));
    }
}