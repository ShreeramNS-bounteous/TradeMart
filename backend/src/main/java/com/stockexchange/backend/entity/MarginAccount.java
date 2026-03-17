package com.stockexchange.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "margin_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarginAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal marginLimit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal marginUsed;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal marginAvailable;

    @Column(nullable = false)
    private BigDecimal marginMultiplier;

    @Column(nullable = false)
    private boolean marginCallTriggered;

    @Column(precision = 10, scale = 2)
    private BigDecimal marginCallThreshold;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;
}