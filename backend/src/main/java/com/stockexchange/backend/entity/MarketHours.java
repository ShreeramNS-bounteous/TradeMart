package com.stockexchange.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "market_hours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private boolean isMarketOpen;

    @Column(nullable = false)
    private String timezone;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}