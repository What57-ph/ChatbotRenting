package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.repository.OrderRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.service.SubscriptionService;
import com.lecturemind.commonservice.exception.ExistException;
import com.lecturemind.commonservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final OrderRepository orderRepository;
    @Override
    public Subscription createSubscription(Order order, Long userId) {
        if (orderRepository.existsByUserIdAndStatus(order.getId(), OrderStatus.PENDING)){
            if (subscriptionRepository.existsByUserIdAndStatus(
                    userId,
                    SubscriptionStatus.ACTIVE)) {
                throw new ExistException("User already has active subscription");
            }
//            throw new NotFoundException("Not found order");
        }

        LocalDateTime now = LocalDateTime.now();

        Subscription savingSubscription = new Subscription();
        savingSubscription.setUserId(userId);
        savingSubscription.setPlan(order.getPlan());
        savingSubscription.setStatus(SubscriptionStatus.ACTIVE);
        savingSubscription.setAutoRenew(true);

        savingSubscription.setStartDate(now);
        savingSubscription.setEndDate(now.plusMonths(order.getPlan().getDuration()));
        savingSubscription.setCurrentPeriodStart(now);

        if (order.getBillingCycle().equals(BillingCycle.MONTHLY)){
            savingSubscription.setCurrentPeriodEnd(now.plusMonths(1));
        }

        if (order.getBillingCycle().equals(BillingCycle.YEARLY)){
            savingSubscription.setCurrentPeriodEnd(now.plusYears(1));
        }
        return subscriptionRepository.save(savingSubscription);
    }

    public void revokeSubscription(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }
}
