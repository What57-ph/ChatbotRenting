package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.entity.SourceText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SourceTextRepository extends JpaRepository<SourceText, UUID> {
}
