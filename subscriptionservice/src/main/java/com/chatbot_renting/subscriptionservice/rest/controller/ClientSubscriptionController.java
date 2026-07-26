package com.chatbot_renting.subscriptionservice.rest.controller;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionAutoRenewRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionDowngradeRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionUpgradeRequest;
import com.chatbot_renting.subscriptionservice.dto.response.CurrentSubscriptionResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionCreateResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;
import com.chatbot_renting.subscriptionservice.rest.api.ClientSubscriptionApi;
import com.chatbot_renting.subscriptionservice.service.SubscriptionService;
import com.chatbot_renting.subscriptionservice.utils.SecurityUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientSubscriptionController implements ClientSubscriptionApi {

    private final SubscriptionService subscriptionService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<SubscriptionCreateResponse> createSubscription(SubscriptionCreateRequest request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Creating subscription for user {}", userId);
        return ResponseEntity.ok(subscriptionService.createSubscription(userId, request));
    }

    @Override
    public ResponseEntity<CurrentSubscriptionResponse> getCurrentSubscription() {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Getting current subscription for user {}", userId);
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(userId));
    }

    @Override
    public ResponseEntity<SubscriptionDto> cancelSubscription() {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Cancelling subscription for user {}", userId);
        return ResponseEntity.ok(subscriptionService.cancelSubscription(userId));
    }

    @Override
    public ResponseEntity<SubscriptionCreateResponse> upgradeSubscription(SubscriptionUpgradeRequest request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Upgrading subscription for user {}", userId);
        return ResponseEntity.ok(subscriptionService.upgradeSubscription(userId, request));
    }

    @Override
    public ResponseEntity<SubscriptionDto> downgradeSubscription(SubscriptionDowngradeRequest request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Downgrading subscription for user {}", userId);
        return ResponseEntity.ok(subscriptionService.downgradeSubscription(userId, request));
    }

    @Override
    public ResponseEntity<SubscriptionDto> toggleAutoRenew(SubscriptionAutoRenewRequest request) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Toggling auto-renew for user {} to {}", userId, request.getAutoRenew());
        return ResponseEntity.ok(subscriptionService.toggleAutoRenew(userId, request));
    }
}
