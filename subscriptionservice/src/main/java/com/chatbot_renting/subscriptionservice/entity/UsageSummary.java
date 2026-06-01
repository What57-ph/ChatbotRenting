package com.chatbot_renting.subscriptionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "usage_summary",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "subscription_id",
                                "year",
                                "month"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageSummary extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    private Integer year;

    private Integer month;

    private Long tokensUsed = 0L;

    private Long storageUsedMb = 0L;

    private Long apiCalls = 0L;

    private Integer chatbotCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
}
