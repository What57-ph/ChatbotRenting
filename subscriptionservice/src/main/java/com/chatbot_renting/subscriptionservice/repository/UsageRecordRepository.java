package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
}
