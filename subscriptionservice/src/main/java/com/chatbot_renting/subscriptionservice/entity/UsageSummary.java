package com.chatbot_renting.subscriptionservice.entity;

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
@Table(name = "usage_summaries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"subscription_id", "period_start"})
})
public class UsageSummary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    @Column(nullable = false)
    @Builder.Default
    private Long tokensUsed = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double storageUsedMb = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer chatbotCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer filesCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Long apiCalls = 0L;
}
