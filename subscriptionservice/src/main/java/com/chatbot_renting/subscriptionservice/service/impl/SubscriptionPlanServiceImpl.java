package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.request.PlanFeatureRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;

import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.mapper.SubscriptionPlanMapper;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.service.SubscriptionPlanService;
import com.chatbot_renting.subscriptionservice.utils.PlanUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;
    private final PlanUtils planUtils;

    @Override
    public List<SubscriptionPlanDto> getActivePlans() {
        log.info("Starting getActivePlans");
        try {
            List<SubscriptionPlanDto> plans = planRepository.findByActiveTrue().stream()
                    .sorted(planUtils.monthlyPriceComparator())
                    .map(planMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Completed getActivePlans - count={}", plans.size());
            return plans;
        } catch (Exception e) {
            log.error("Error in getActivePlans", e);
            throw e;
        }
    }

    @Override
    public SubscriptionPlanDto getPlan(UUID planId) {
        log.info("Starting getPlan - planId={}", planId);
        try {
            SubscriptionPlanDto dto = planRepository.findById(planId)
                    .map(planMapper::toDto)
                    .orElseThrow(() -> new AppNotFoundException(
                            new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND, "Plan ID: " + planId)
                    ));
            log.info("Completed getPlan - planId={}", planId);
            return dto;
        } catch (Exception e) {
            log.error("Error in getPlan - planId={}", planId, e);
            throw e;
        }
    }

    @Override
    public List<SubscriptionPlanDto> getAllPlans() {
        log.info("Starting getAllPlans");
        try {
            List<SubscriptionPlanDto> plans = planRepository.findAll().stream()
                    .sorted(planUtils.monthlyPriceComparator())
                    .map(planMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Completed getAllPlans - count={}", plans.size());
            return plans;
        } catch (Exception e) {
            log.error("Error in getAllPlans", e);
            throw e;
        }
    }

    @Override
    public SubscriptionPlanDto createPlan(com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest request) {
        log.info("Starting createPlan - code={}", request.getCode());
        if (planRepository.existsByCode(request.getCode())) {
            throw new com.chatbot_renting.commonservice.exception.AppBadRequestException(
                    new AppError(SubscriptionErrorCode.PLAN_CODE_ALREADY_EXISTS));
        }

        validateFeatures(request.getFeatures());

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .maxChatbots(request.getMaxChatbots())
                .maxStorageMb(request.getMaxStorageMb())
                .maxMonthlyTokens(request.getMaxMonthlyTokens())
                .durationMonths(request.getDurationMonths())
                .trialDays(request.getTrialDays())
                .active(true)
                .build();
        
        List<PlanFeature> features = mapFeatures(request.getFeatures(), plan);
        plan.setFeatures(features);

        SubscriptionPlan savedPlan = planRepository.save(plan);
        log.info("Completed createPlan - new planId={}", savedPlan.getId());
        return planMapper.toDto(savedPlan);
    }

    @Override
    public SubscriptionPlanDto updatePlan(UUID planId, com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest request) {
        log.info("Starting updatePlan - planId={}", planId);
        SubscriptionPlan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new AppNotFoundException(
                        new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND, "Plan ID: " + planId)
                ));

        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            validateFeatures(request.getFeatures());
            existingPlan.getFeatures().clear();
            existingPlan.getFeatures().addAll(mapFeatures(request.getFeatures(), existingPlan));
        }

        SubscriptionPlan updatedPlan = existingPlan.toBuilder()
                .name(request.getName())
                .description(request.getDescription())
                .maxChatbots(request.getMaxChatbots())
                .maxStorageMb(request.getMaxStorageMb())
                .maxMonthlyTokens(request.getMaxMonthlyTokens())
                .durationMonths(request.getDurationMonths())
                .trialDays(request.getTrialDays())
                .active(request.getActive())
                .build();

        // Note: We need a notification dispatched here (Implementation planned for next phase)
        log.info("[FUTURE PHASE] Dispatch notification: Plan updated for planId={}", planId);

        SubscriptionPlan savedPlan = planRepository.save(updatedPlan);
        log.info("Completed updatePlan - planId={}", planId);
        return planMapper.toDto(savedPlan);
    }

    @Override
    public void softDeletePlan(UUID planId) {
        log.info("Starting softDeletePlan - planId={}", planId);
        SubscriptionPlan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new AppNotFoundException(
                        new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND, "Plan ID: " + planId)
                ));

        SubscriptionPlan updatedPlan = existingPlan.toBuilder().active(false).build();
        planRepository.save(updatedPlan);
        log.info("Completed softDeletePlan - planId={}", planId);
    }

    private void validateFeatures(List<PlanFeatureRequest> features) {
        if (features == null || features.isEmpty()) {
            throw new com.chatbot_renting.commonservice.exception.AppBadRequestException(
                    new AppError(SubscriptionErrorCode.PLAN_MISSING_PRICE));
        }
        boolean hasMonthly = features.stream().anyMatch(f -> com.chatbot_renting.subscriptionservice.utils.PlanFeatureKeys.MONTHLY_PRICE.equals(f.getFeatureKey()));
        boolean hasYearly = features.stream().anyMatch(f -> com.chatbot_renting.subscriptionservice.utils.PlanFeatureKeys.YEARLY_PRICE.equals(f.getFeatureKey()));
        
        if (!hasMonthly || !hasYearly) {
            throw new com.chatbot_renting.commonservice.exception.AppBadRequestException(
                    new AppError(SubscriptionErrorCode.PLAN_MISSING_PRICE));
        }
    }

    private List<PlanFeature> mapFeatures(
            List<PlanFeatureRequest> requests, 
            SubscriptionPlan plan) {
        return requests.stream().map(req -> PlanFeature.builder()
                .plan(plan)
                .featureKey(req.getFeatureKey())
                .featureValue(req.getFeatureValue())
                .build()).collect(Collectors.toList());
    }
}
