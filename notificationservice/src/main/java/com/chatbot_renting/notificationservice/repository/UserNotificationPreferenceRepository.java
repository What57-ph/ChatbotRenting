package com.chatbot_renting.notificationservice.repository;

import com.chatbot_renting.notificationservice.entity.NotificationTemplate;
import com.chatbot_renting.notificationservice.entity.UserNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, UUID> {
    Optional<UserNotificationPreference> findByUserIdAndTemplateAndChannel(UUID userId, NotificationTemplate template,
            String channel);

    @EntityGraph(attributePaths = { "template" })
    List<UserNotificationPreference> findByUserId(UUID userId);

    List<UserNotificationPreference> findByUserIdInAndTemplate(List<UUID> userIds, NotificationTemplate template);
}
