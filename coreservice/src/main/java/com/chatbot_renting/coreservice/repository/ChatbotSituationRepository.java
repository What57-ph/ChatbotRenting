package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.entity.ChatbotSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatbotSituationRepository extends JpaRepository<ChatbotSituation, UUID> {
    List<ChatbotSituation> findAllByChatbotId(UUID chatbotId);
}
