package com.stockexchange.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInvestedValue;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentMarketValue;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalProfitLoss;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal totalProfitLossPercentage;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;
}