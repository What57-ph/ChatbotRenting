package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionAutoRenewRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionDowngradeRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionUpgradeRequest;
import com.chatbot_renting.subscriptionservice.dto.response.CurrentSubscriptionResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionCreateResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;
import java.util.UUID;

public interface SubscriptionService {
    SubscriptionCreateResponse createSubscription(UUID userId, SubscriptionCreateRequest request);
    CurrentSubscriptionResponse getCurrentSubscription(UUID userId);
    SubscriptionDto getSubscriptionByUserId(UUID userId);
    SubscriptionDto cancelSubscription(UUID userId);
    SubscriptionCreateResponse upgradeSubscription(UUID userId, SubscriptionUpgradeRequest request);
    SubscriptionDto downgradeSubscription(UUID userId, SubscriptionDowngradeRequest request);
    SubscriptionDto toggleAutoRenew(UUID userId, SubscriptionAutoRenewRequest request);
}
