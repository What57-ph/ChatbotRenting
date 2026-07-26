package com.chatbot_renting.coreservice.entity;

import com.chatbot_renting.coreservice.entity.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.entity.enums.ChatbotStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "chatbots", indexes = { @Index(columnList = "user_id") })
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Chatbot extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatbotStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChatbotLanguage language;

    @OneToMany(mappedBy = "chatbot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatbotSituation> situations;

    @OneToMany(mappedBy = "chatbot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeSource> knowledgeSources;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
