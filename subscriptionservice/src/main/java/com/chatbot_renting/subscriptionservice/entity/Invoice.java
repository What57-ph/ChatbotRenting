package com.chatbot_renting.subscriptionservice.entity;

import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime paidAt;

    @Column(nullable = false, columnDefinition = "timestamp(6) default CURRENT_TIMESTAMP")
    private LocalDateTime dueDate;

    private String paymentMethod;

    private String paymentReference;

    private String notes;

    /**
     * JSON snapshot của SubscriptionPlan tại thời điểm xuất hoá đơn.
     */
    @Column(columnDefinition = "TEXT")
    private String planSnapshot;
}
