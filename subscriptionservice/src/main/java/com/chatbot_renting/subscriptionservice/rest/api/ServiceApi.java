package com.chatbot_renting.subscriptionservice.rest.api;

import com.chatbot_renting.subscriptionservice.dto.request.UsageCheckRequest;
import com.chatbot_renting.subscriptionservice.dto.request.UsageRecordRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;
import com.chatbot_renting.subscriptionservice.dto.response.UsageCheckResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/service-api/v1")
public interface ServiceApi {

    @GetMapping("/users/{userId}/subscriptions/current")
    ResponseEntity<SubscriptionDto> getCurrentSubscription(@PathVariable("userId") Long userId);

    @PostMapping("/usage/record")
    ResponseEntity<Void> recordUsage(@Valid @RequestBody UsageRecordRequest request);

    @PostMapping("/usage/check")
    ResponseEntity<UsageCheckResponse> checkUsage(@Valid @RequestBody UsageCheckRequest request);
}
