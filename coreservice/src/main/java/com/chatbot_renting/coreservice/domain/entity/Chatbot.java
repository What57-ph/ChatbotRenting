package com.chatbot_renting.coreservice.domain.entity;

import com.chatbot_renting.coreservice.domain.enums.ChatbotLanguage;
import com.chatbot_renting.coreservice.domain.enums.ChatbotStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chatbots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
