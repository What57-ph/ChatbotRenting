package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.subscriptionservice.dto.request.PlanFeature.CreatePlanFeatureRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.CreateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.UpdateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanResponse;
import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.mapper.SubscriptionPlanMapper;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.service.PlanFeatureService;
import com.chatbot_renting.subscriptionservice.service.SubscriptionPlanService;
import com.lecturemind.commonservice.exception.ExistException;
import com.lecturemind.commonservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanFeatureService planFeatureService;
    private final SubscriptionPlanMapper mapper;

    @Override
    @Transactional
    public SubscriptionPlanResponse createSubscriptionPlan(CreateSubscriptionPlanRequest request) {

        validateCodeUnique(request.getCode());
        validatePricing(request);
        validateFeatures(request.getFeatures());

        SubscriptionPlan plan = mapper.toEntity(request);

        attachFeatures(plan);
        planFeatureService.saveAllPlanFeatures(request.getFeatures());
        SubscriptionPlan saved = planRepository.save(plan);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse updateSubscriptionPlan(Long id,
                                               UpdateSubscriptionPlanRequest request) {

        SubscriptionPlan plan = findPlanById(id);

        validateUpdatePricing(request);

        plan.setName(request.getName() != null ? request.getName() : plan.getName());
        plan.setDescription(request.getDescription() != null ? request.getDescription() : plan.getDescription());
        plan.setMonthlyPrice(request.getMonthlyPrice() != null ? request.getMonthlyPrice() : plan.getMonthlyPrice());
        plan.setYearlyPrice(request.getYearlyPrice() != null ? request.getYearlyPrice() : plan.getYearlyPrice());
        plan.setMaxChatbots(request.getMaxChatbots() != null ? request.getMaxChatbots() : plan.getMaxChatbots());
        plan.setMaxFiles(request.getMaxFiles() != null ? request.getMaxFiles() : plan.getMaxFiles());
        plan.setMaxStorageMb(request.getMaxStorageMb() != null ? request.getMaxStorageMb() : plan.getMaxStorageMb());
        plan.setMaxMonthlyTokens(request.getMaxMonthlyTokens() != null ? request.getMaxMonthlyTokens() : plan.getMaxMonthlyTokens());
        plan.setActive(request.getActive() != null ? request.getActive() : plan.getActive());

        if (request.getFeatures() != null) {
            plan.getFeatures().clear();
            List<PlanFeature> newFeatures = request.getFeatures()
                    .stream()
                    .map(f -> {
                        PlanFeature feature = new PlanFeature();
                        feature.setFeatureKey(f.getFeatureKey());
                        feature.setFeatureValue(f.getFeatureValue());
                        feature.setPlan(plan);
                        return feature;
                    }).toList();

            plan.getFeatures().addAll(newFeatures);
        }

        return mapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getSubscriptionPlan(Long id) {
        return mapper.toResponse(findPlanById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getAllSubscriptionPlans() {
        return planRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getActiveSubscriptionPlans() {
        return planRepository.findByActiveTrue()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse activatePlan(Long id) {
        SubscriptionPlan plan = findPlanById(id);
        plan.setActive(true);
        return mapper.toResponse(plan);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse deactivatePlan(Long id) {

        SubscriptionPlan plan = findPlanById(id);

        if (subscriptionRepository.existsByPlanId(id)) {
            throw new ExistException("SUBSCRIPTION_PLAN_EXISTS");
        }

        plan.setActive(false);
        return mapper.toResponse(plan);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse deletePlan(Long id) {
        return deactivatePlan(id);

    }


    private SubscriptionPlan findPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Subscription plan not found"));
    }

    private void validateCodeUnique(String code) {
        if (planRepository.existsByCode(code)) {
            throw new ExistException("PLAN_CODE_EXISTS");
        }
    }

    private void validatePricing(CreateSubscriptionPlanRequest request) {
        if (request.getMonthlyPrice().compareTo(request.getYearlyPrice()) < 0) {

            if (request.getYearlyPrice()
                    .compareTo(request.getMonthlyPrice().multiply(java.math.BigDecimal.valueOf(12))) > 0) {
                throw new RuntimeException("INVALID_YEARLY_PRICE");
            }
        }
    }

    private void validateUpdatePricing(UpdateSubscriptionPlanRequest request) {
        if (request.getMonthlyPrice() != null && request.getYearlyPrice() != null) {
            if (request.getYearlyPrice()
                    .compareTo(request.getMonthlyPrice().multiply(java.math.BigDecimal.valueOf(12))) > 0) {
                throw new RuntimeException("INVALID_YEARLY_PRICE");
            }
        }
    }

    private void validateFeatures(List<CreatePlanFeatureRequest> features) {
        if (features == null) return;

        Set<String> keys = new HashSet<>();

        for (CreatePlanFeatureRequest f : features) {
            if (!keys.add(f.getFeatureKey())) {
                throw new RuntimeException("DUPLICATE_FEATURE_KEY");
            }
        }
    }

    private void attachFeatures(SubscriptionPlan plan) {
        if (plan.getFeatures() == null) return;

        plan.getFeatures().forEach(f -> f.setPlan(plan));
    }
}
