package com.stockexchange.backend.dto.response;

import com.stockexchange.backend.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String ticker;
    private String companyName;

    private OrderSide side;
    private OrderType type;
    private OrderStatus status;

    private Integer quantity;

    private BigDecimal limitPrice;
    private BigDecimal executedPrice;
    private BigDecimal totalOrderValue;

    private boolean isMarginOrder;
    private String rejectionReason;

    private Integer filledQuantity;

    private LocalDateTime placedAt;
    private LocalDateTime executedAt;

}