package com.chatbot_renting.coreservice.domain.entity;

import com.chatbot_renting.coreservice.domain.enums.KnowledgeSourceType;
import com.chatbot_renting.coreservice.domain.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "knowledge_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatbot_id", nullable = false)
    private Chatbot chatbot;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private KnowledgeSourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProcessingStatus status;

    @OneToMany(mappedBy = "knowledgeSource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SourceFile> sourceFiles;

    @OneToOne(mappedBy = "knowledgeSource", cascade = CascadeType.ALL, orphanRemoval = true)
    private SourceUrl sourceUrl;

    @OneToOne(mappedBy = "knowledgeSource", cascade = CascadeType.ALL, orphanRemoval = true)
    private SourceText sourceText;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
