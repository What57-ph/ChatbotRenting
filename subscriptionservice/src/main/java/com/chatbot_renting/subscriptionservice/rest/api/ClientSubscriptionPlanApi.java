package com.chatbot_renting.subscriptionservice.rest.api;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/client-api/v1/subscription-plans")
public interface ClientSubscriptionPlanApi {

    @GetMapping("/active")
    ResponseEntity<List<SubscriptionPlanDto>> getActivePlans();

    @GetMapping
    ResponseEntity<List<SubscriptionPlanDto>> getAllPlans();

    @PostMapping
    ResponseEntity<SubscriptionPlanDto> createPlan(@RequestBody SubscriptionPlanCreateRequest request);

    @PutMapping("/{id}")
    ResponseEntity<SubscriptionPlanDto> updatePlan(@PathVariable("id") Long id, @RequestBody SubscriptionPlanUpdateRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> softDeletePlan(@PathVariable("id") Long id);

}
