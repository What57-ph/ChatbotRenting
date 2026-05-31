package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.domain.entity.KnowledgeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeSourceRepository extends JpaRepository<KnowledgeSource, Long> {
    Optional<KnowledgeSource> findByIdAndChatbotId(Long id, Long chatbotId);
    List<KnowledgeSource> findAllByChatbotId(Long chatbotId);
}
