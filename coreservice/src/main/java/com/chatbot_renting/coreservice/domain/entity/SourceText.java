package com.chatbot_renting.coreservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "source_texts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource knowledgeSource;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
