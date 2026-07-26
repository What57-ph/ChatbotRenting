package com.chatbot_renting.coreservice.entity;

import com.chatbot_renting.coreservice.entity.enums.KnowledgeSourceType;
import com.chatbot_renting.coreservice.entity.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "knowledge_sources", indexes = { @Index(columnList = "chatbot_id") })
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class KnowledgeSource extends BaseEntity {

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
}
