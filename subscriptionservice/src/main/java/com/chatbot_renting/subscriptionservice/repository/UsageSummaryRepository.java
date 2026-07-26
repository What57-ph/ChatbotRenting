package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.UsageSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageSummaryRepository extends JpaRepository<UsageSummary, UUID> {

    /** Tìm summary theo subscription và điểm bắt đầu chu kỳ (Billing Anchor). */
    Optional<UsageSummary> findBySubscriptionIdAndPeriodStart(UUID subscriptionId, LocalDateTime periodStart);

    /** Lấy tất cả summaries theo subscription, sắp xếp chu kỳ mới nhất trước. */
    List<UsageSummary> findBySubscriptionIdOrderByPeriodStartDesc(UUID subscriptionId);
}
