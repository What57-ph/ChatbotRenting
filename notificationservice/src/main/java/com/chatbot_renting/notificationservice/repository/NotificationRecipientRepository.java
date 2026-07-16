package com.chatbot_renting.notificationservice.repository;

import com.chatbot_renting.notificationservice.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, UUID> {

    @EntityGraph(attributePaths = { "payload", "payload.template" })
    Page<NotificationRecipient> findByRecipientIdOrderByPayloadCreatedAtDesc(UUID recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalseAndIsDeletedFalse(UUID recipientId);

    Optional<NotificationRecipient> findByIdAndRecipientId(UUID id, UUID recipientId);

    List<NotificationRecipient> findByRecipientIdAndIsReadFalse(UUID recipientId);

}
