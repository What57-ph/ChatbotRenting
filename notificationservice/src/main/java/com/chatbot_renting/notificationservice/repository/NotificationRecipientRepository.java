package com.chatbot_renting.notificationservice.repository;

import com.chatbot_renting.notificationservice.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, UUID> {

    @EntityGraph(attributePaths = { "payload", "payload.template" })
    @Query("SELECT r FROM NotificationRecipient r WHERE r.recipientId = :recipientId ORDER BY r.payload.createdAt DESC")
    Page<NotificationRecipient> findTimelineByRecipientId(@Param("recipientId") UUID recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalseAndIsDeletedFalse(UUID recipientId);

    Optional<NotificationRecipient> findByIdAndRecipientId(UUID id, UUID recipientId);

    @Modifying
    @Query("UPDATE NotificationRecipient r SET r.isRead = true, r.readAt = CURRENT_TIMESTAMP WHERE r.recipientId = :recipientId AND r.isRead = false")
    void markAllAsRead(@Param("recipientId") UUID recipientId);

}
