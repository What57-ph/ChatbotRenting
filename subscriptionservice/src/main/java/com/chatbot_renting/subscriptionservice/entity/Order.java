package com.chatbot_renting.subscriptionservice.entity;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(unique = true)
    private String orderNumber;

    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private Invoice invoice;
}
