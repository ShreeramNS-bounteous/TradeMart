package com.stockexchange.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "portfolio_holdings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id","stock_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private Integer quantityOwned;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averageBuyPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalInvestedAmount;

    @Column(nullable = false)
    private boolean marginPosition;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;
}