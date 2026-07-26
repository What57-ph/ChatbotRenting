package com.chatbot_renting.notificationservice.repository;

import com.chatbot_renting.notificationservice.entity.NotificationPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationPayloadRepository extends JpaRepository<NotificationPayload, UUID> {
}
