package com.chatbot_renting.subscriptionservice.repository;

import com.chatbot_renting.subscriptionservice.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findByOrderUserIdOrderByIssuedAtDesc(UUID userId, Pageable pageable);
}
