package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderType;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatus status, Pageable pageable);
    boolean existsBySubscriptionIdAndOrderTypeAndStatus(UUID subscriptionId, OrderType orderType, OrderStatus status);
}
