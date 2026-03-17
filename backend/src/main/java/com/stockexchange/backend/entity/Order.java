package com.stockexchange.backend.entity;

import com.stockexchange.backend.enums.OrderSide;
import com.stockexchange.backend.enums.OrderStatus;
import com.stockexchange.backend.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal limitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal executedPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalOrderValue;

    @Column(nullable = false)
    private boolean marginOrder;

    @Column(nullable = false)
    private Integer filledQuantity = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime placedAt;

    private LocalDateTime executedAt;
}