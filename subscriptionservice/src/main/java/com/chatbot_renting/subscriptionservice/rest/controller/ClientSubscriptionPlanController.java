package com.chatbot_renting.subscriptionservice.rest.controller;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import com.chatbot_renting.subscriptionservice.rest.api.ClientSubscriptionPlanApi;
import com.chatbot_renting.subscriptionservice.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientSubscriptionPlanController implements ClientSubscriptionPlanApi {

    private final SubscriptionPlanService planService;

    private final SubscriptionPlanService subscriptionPlanService;

    @Override
    public ResponseEntity<List<SubscriptionPlanDto>> getAllPlans() {
        log.info("REST request to get all subscription plans (Admin)");
        return ResponseEntity.ok(subscriptionPlanService.getAllPlans());
    }

    @Override
    public ResponseEntity<SubscriptionPlanDto> createPlan(SubscriptionPlanCreateRequest request) {
        log.info("REST request to create subscription plan: {}", request.getCode());
        SubscriptionPlanDto createdPlan = subscriptionPlanService.createPlan(request);
        return ResponseEntity.created(URI.create("/admin-api/v1/subscription-plans/" + createdPlan.getId()))
                .body(createdPlan);
    }

    @Override
    public ResponseEntity<SubscriptionPlanDto> updatePlan(Long planId, SubscriptionPlanUpdateRequest request) {
        log.info("REST request to update subscription plan ID: {}", planId);
        return ResponseEntity.ok(subscriptionPlanService.updatePlan(planId, request));
    }

    @Override
    public ResponseEntity<Void> softDeletePlan(Long planId) {
        log.info("REST request to soft delete subscription plan ID: {}", planId);
        subscriptionPlanService.softDeletePlan(planId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<SubscriptionPlanDto>> getActivePlans() {
        log.info("Fetching active subscription plans");
        return ResponseEntity.ok(planService.getActivePlans());
    }
}
