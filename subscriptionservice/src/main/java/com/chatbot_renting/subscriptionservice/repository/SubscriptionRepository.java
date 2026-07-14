package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByUserIdAndStatusIn(Long userId, List<SubscriptionStatus> statuses);

    List<Subscription> findByStatusAndAutoRenewTrueAndCurrentPeriodEndLessThanEqual(SubscriptionStatus status, LocalDateTime targetDate);

    List<Subscription> findByStatusInAndAutoRenewFalseAndCurrentPeriodEndLessThan(List<SubscriptionStatus> statuses, LocalDateTime targetDate);

    List<Subscription> findByStatusAndCurrentPeriodEndLessThan(SubscriptionStatus status, LocalDateTime targetDate);

    long countByUserIdAndStatusNotIn(Long userId, List<SubscriptionStatus> statuses);

    List<Subscription> findByStatusAndAutoRenewTrueAndCurrentPeriodEndGreaterThanEqualAndCurrentPeriodEndLessThan(SubscriptionStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
