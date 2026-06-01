package com.chatbot_renting.subscriptionservice.controller;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.CreateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlan.UpdateSubscriptionPlanRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanResponse;
import com.chatbot_renting.subscriptionservice.service.SubscriptionPlanService;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> createPlan(
            @RequestBody CreateSubscriptionPlanRequest request) {

        return ResponseEntity.ok(
                ResponseBuilder.created(planService.createSubscriptionPlan(request))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> updatePlan(
            @PathVariable Long id,
            @RequestBody UpdateSubscriptionPlanRequest request) {

        return ResponseEntity.ok(
                ResponseBuilder.success(planService.updateSubscriptionPlan(id, request))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> getPlan(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseBuilder.success(planService.getSubscriptionPlan(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getAllPlans() {

        return ResponseEntity.ok(
                ResponseBuilder.success(planService.getAllSubscriptionPlans())
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getActivePlans() {

        return ResponseEntity.ok(
                ResponseBuilder.success(planService.getActiveSubscriptionPlans())
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> activatePlan(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseBuilder.success(planService.activatePlan(id))
                );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> deactivatePlan(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseBuilder.success(planService.deactivatePlan(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> deletePlan(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseBuilder.success( planService.deletePlan(id))
        );
    }
}
