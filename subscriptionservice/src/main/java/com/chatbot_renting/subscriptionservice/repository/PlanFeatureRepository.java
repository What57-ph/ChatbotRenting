package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanFeatureRepository
        extends JpaRepository<PlanFeature, Long> {
}
