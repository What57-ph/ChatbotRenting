package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionAutoRenewRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionCreateRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionDowngradeRequest;
import com.chatbot_renting.subscriptionservice.dto.request.SubscriptionUpgradeRequest;
import com.chatbot_renting.subscriptionservice.dto.response.CurrentSubscriptionResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionCreateResponse;
import com.chatbot_renting.subscriptionservice.dto.response.SubscriptionDto;

public interface SubscriptionService {
    SubscriptionCreateResponse createSubscription(Long userId, SubscriptionCreateRequest request);
    CurrentSubscriptionResponse getCurrentSubscription(Long userId);
    SubscriptionDto getSubscriptionByUserId(Long userId);
    SubscriptionDto cancelSubscription(Long userId);
    SubscriptionCreateResponse upgradeSubscription(Long userId, SubscriptionUpgradeRequest request);
    SubscriptionDto downgradeSubscription(Long userId, SubscriptionDowngradeRequest request);
    SubscriptionDto toggleAutoRenew(Long userId, SubscriptionAutoRenewRequest request);
}
