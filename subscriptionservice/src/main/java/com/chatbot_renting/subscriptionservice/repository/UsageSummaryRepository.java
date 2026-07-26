package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.UsageSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsageSummaryRepository extends JpaRepository<UsageSummary, Long> {

    /** Tìm summary theo subscription và điểm bắt đầu chu kỳ (Billing Anchor). */
    Optional<UsageSummary> findBySubscriptionIdAndPeriodStart(Long subscriptionId, LocalDateTime periodStart);

    /** Lấy tất cả summaries theo subscription, sắp xếp chu kỳ mới nhất trước. */
    List<UsageSummary> findBySubscriptionIdOrderByPeriodStartDesc(Long subscriptionId);
}
