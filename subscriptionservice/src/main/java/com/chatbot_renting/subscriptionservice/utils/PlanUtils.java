package com.chatbot_renting.subscriptionservice.utils;

import com.chatbot_renting.commonservice.exception.AppBadRequestException;
import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.subscriptionservice.dto.response.PlanFeatureDto;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanUtils {

    private final ObjectMapper objectMapper;

    public double resolvePrice(SubscriptionPlan plan, BillingCycle billingCycle) {
        String featureKey = billingCycle == BillingCycle.MONTHLY
                ? PlanFeatureKeys.MONTHLY_PRICE
                : PlanFeatureKeys.YEARLY_PRICE;

        return findFeatureValue(plan, featureKey)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        log.error("Invalid price value '{}' for plan {} feature {}", value, plan.getId(), featureKey);
                        throw new AppBadRequestException(new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND));
                    }
                })
                .orElseThrow(() -> new AppBadRequestException(
                        new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND,
                                "Missing price feature '" + featureKey + "' for plan " + plan.getId())
                ));
    }

    public Optional<Double> resolveMonthlyPrice(SubscriptionPlan plan) {
        return findFeatureValue(plan, PlanFeatureKeys.MONTHLY_PRICE).map(Double::parseDouble);
    }

    public Optional<Double> resolveYearlyPrice(SubscriptionPlan plan) {
        return findFeatureValue(plan, PlanFeatureKeys.YEARLY_PRICE).map(Double::parseDouble);
    }

    public void enrichPlanDto(SubscriptionPlanDto dto, SubscriptionPlan plan) {
        dto.setMonthlyPrice(resolveMonthlyPrice(plan).orElse(null));
        dto.setYearlyPrice(resolveYearlyPrice(plan).orElse(null));
    }

    public Comparator<SubscriptionPlan> monthlyPriceComparator() {
        return Comparator.comparing(plan -> resolveMonthlyPrice(plan).orElse(Double.MAX_VALUE));
    }

    public String buildPlanSnapshot(SubscriptionPlan plan, BillingCycle billingCycle, double amount) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("planId", plan.getId());
        snapshot.put("code", plan.getCode());
        snapshot.put("name", plan.getName());
        snapshot.put("description", plan.getDescription());
        snapshot.put("billingCycle", billingCycle.name());
        snapshot.put("amount", amount);
        snapshot.put("maxChatbots", plan.getMaxChatbots());
        snapshot.put("maxStorageMb", plan.getMaxStorageMb());
        snapshot.put("maxMonthlyTokens", plan.getMaxMonthlyTokens());
        snapshot.put("durationMonths", plan.getDurationMonths());
        snapshot.put("features", plan.getFeatures().stream()
                .map(f -> Map.of("featureKey", f.getFeatureKey(), "featureValue", f.getFeatureValue()))
                .toList());

        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize plan snapshot for plan {}", plan.getId(), e);
            throw new AppBadRequestException(new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND));
        }
    }

    public List<PlanFeatureDto> toFeatureDtos(List<PlanFeature> features) {
        return features.stream()
                .map(f -> {
                    PlanFeatureDto dto = new PlanFeatureDto();
                    dto.setFeatureKey(f.getFeatureKey());
                    dto.setFeatureValue(f.getFeatureValue());
                    return dto;
                })
                .toList();
    }

    private Optional<String> findFeatureValue(SubscriptionPlan plan, String featureKey) {
        if (plan.getFeatures() == null) {
            return Optional.empty();
        }
        return plan.getFeatures().stream()
                .filter(f -> featureKey.equals(f.getFeatureKey()))
                .map(PlanFeature::getFeatureValue)
                .findFirst();
    }
}
