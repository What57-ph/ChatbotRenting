package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.entity.KnowledgeSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeSourceRepository extends JpaRepository<KnowledgeSource, UUID> {
    
    @EntityGraph(attributePaths = {"sourceFiles", "sourceUrl", "sourceText"})
    Optional<KnowledgeSource> findByIdAndChatbotId(UUID id, UUID chatbotId);
    
    @EntityGraph(attributePaths = {"sourceFiles", "sourceUrl", "sourceText"})
    Page<KnowledgeSource> findAllByChatbotId(UUID chatbotId, Pageable pageable);
}
