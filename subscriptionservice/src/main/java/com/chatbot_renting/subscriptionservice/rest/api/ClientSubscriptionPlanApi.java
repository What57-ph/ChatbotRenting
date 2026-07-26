package com.chatbot_renting.subscriptionservice.rest.api;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionPlanUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionPlanDto;
import jakarta.validation.Valid;
import java.util.UUID;
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

    @GetMapping("/{id}")
    ResponseEntity<SubscriptionPlanDto> getPlan(@PathVariable("id") UUID id);

    @PostMapping
    ResponseEntity<SubscriptionPlanDto> createPlan(@RequestBody SubscriptionPlanCreateRequest request);

    @PutMapping("/{id}")
    ResponseEntity<SubscriptionPlanDto> updatePlan(@PathVariable("id") UUID id, @RequestBody SubscriptionPlanUpdateRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> softDeletePlan(@PathVariable("id") UUID id);

}
