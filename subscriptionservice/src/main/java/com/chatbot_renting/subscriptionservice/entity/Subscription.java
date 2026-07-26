package com.chatbot_renting.subscriptionservice.entity;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")
public class Subscription extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_plan_id")
    private SubscriptionPlan previousPlan;

    /** Lưu planId dự kiến cho lần gia hạn tiếp (downgrade scheduling) */
    private Long scheduledPlanId;

    /**
     * Chu kỳ thanh toán hiện tại của subscription.
     * Job tự động gia hạn dùng field này để biết phải tạo đơn
     * theo giá MONTHLY hay YEARLY — không cần lội ngược tìm đơn cũ.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'MONTHLY'")
    private BillingCycle billingCycle;

    // Snapshot limits for the current active cycle
    private Integer currentMaxChatbots;
    
    private Integer currentMaxStorageMb;
    
    private Integer currentMaxMonthlyTokens;

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoRenew = true;

    private LocalDateTime startDate;

    /** Điểm neo chu kỳ — bắt đầu kỳ thanh toán hiện tại */
    private LocalDateTime currentPeriodStart;

    /** Điểm neo chu kỳ — kết thúc kỳ thanh toán hiện tại */
    private LocalDateTime currentPeriodEnd;

    private LocalDateTime cancelledAt;
}
