package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.entity.Chatbot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatbotRepository extends JpaRepository<Chatbot, UUID> {
    
    @EntityGraph(attributePaths = {"situations"})
    Optional<Chatbot> findByIdAndUserId(UUID id, UUID userId);
    
    @EntityGraph(attributePaths = {"situations"})
    Page<Chatbot> findAllByUserId(UUID userId, Pageable pageable);
}
