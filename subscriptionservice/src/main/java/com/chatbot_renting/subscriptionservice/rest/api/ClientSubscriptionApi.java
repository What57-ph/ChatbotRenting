package com.chatbot_renting.subscriptionservice.rest.api;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionAutoRenewRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionDowngradeRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionUpgradeRequest;
import com.chatbot_renting.subscriptionservice.dto.response.CurrentSubscriptionResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionCreateResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/client-api/v1/subscriptions")
public interface ClientSubscriptionApi {

    @PostMapping
    ResponseEntity<SubscriptionCreateResponse> createSubscription(
            @Valid @RequestBody SubscriptionCreateRequest request);

    @GetMapping("/current")
    ResponseEntity<CurrentSubscriptionResponse> getCurrentSubscription();

    @PostMapping("/cancel")
    ResponseEntity<SubscriptionDto> cancelSubscription();

    @PostMapping("/upgrade")
    ResponseEntity<SubscriptionCreateResponse> upgradeSubscription(
            @Valid @RequestBody SubscriptionUpgradeRequest request);

    @PostMapping("/downgrade")
    ResponseEntity<SubscriptionDto> downgradeSubscription(
            @Valid @RequestBody SubscriptionDowngradeRequest request);

    @PatchMapping("/auto-renew")
    ResponseEntity<SubscriptionDto> toggleAutoRenew(
            @Valid @RequestBody SubscriptionAutoRenewRequest request);
}
