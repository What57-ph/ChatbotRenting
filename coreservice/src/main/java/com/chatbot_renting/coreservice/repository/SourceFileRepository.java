package com.chatbot_renting.coreservice.repository;

import com.chatbot_renting.coreservice.domain.entity.SourceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SourceFileRepository extends JpaRepository<SourceFile, Long> {
}
