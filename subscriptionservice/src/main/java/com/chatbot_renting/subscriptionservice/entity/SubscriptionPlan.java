package com.chatbot_renting.subscriptionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    private BigDecimal monthlyPrice;

    private BigDecimal yearlyPrice;

    private Integer maxChatbots;

    private Integer maxFiles;

    private Integer maxStorageMb;

    private Long maxMonthlyTokens;

    private Integer duration; //month

    private Boolean active = true;

    @OneToMany(
            mappedBy = "plan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PlanFeature> features = new ArrayList<>();

    @OneToMany(mappedBy = "plan")
    private List<Subscription> subscriptions = new ArrayList<>();
}
