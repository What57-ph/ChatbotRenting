package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.domain.entity.SourceText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceTextRepository extends JpaRepository<SourceText, Long> {
}
