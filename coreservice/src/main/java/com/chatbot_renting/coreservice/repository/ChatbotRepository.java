package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.domain.entity.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotRepository extends JpaRepository<Chatbot, Long> {
    Optional<Chatbot> findByIdAndUserId(Long id, Long userId);
    List<Chatbot> findAllByUserId(Long userId);
}
