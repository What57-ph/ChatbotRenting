package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;

public interface SubscriptionService {
    Subscription createSubscription(Order order, Long userId);

    void revokeSubscription(Subscription subscription);
}
