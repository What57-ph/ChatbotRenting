package com.chatbot_renting.subscriptionservice.rest.controller;

import com.chatbot_renting.subscriptionservice.dto.request.UsageCheckRequest;
import com.chatbot_renting.subscriptionservice.dto.request.UsageRecordRequest;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;
import com.chatbot_renting.subscriptionservice.dto.response.UsageCheckResponse;
import com.chatbot_renting.subscriptionservice.rest.api.ServiceApi;
import com.chatbot_renting.subscriptionservice.service.SubscriptionService;
import com.chatbot_renting.subscriptionservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ServiceApiController implements ServiceApi {

    private final SubscriptionService subscriptionService;
    private final UsageService usageService;

    @Override
    public ResponseEntity<SubscriptionDto> getCurrentSubscription(Long userId) {
        log.info("Service getting current subscription for user {}", userId);
        return ResponseEntity.ok(subscriptionService.getSubscriptionByUserId(userId));
    }

    @Override
    public ResponseEntity<Void> recordUsage(UsageRecordRequest request) {
        log.info("Recording usage for user {}: {}", request.getUserId(), request.getUsageType());
        usageService.recordUsage(request);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UsageCheckResponse> checkUsage(UsageCheckRequest request) {
        log.info("Checking usage quota for user {}: {}", request.getUserId(), request.getUsageType());
        return ResponseEntity.ok(usageService.checkQuota(request));
    }
}
