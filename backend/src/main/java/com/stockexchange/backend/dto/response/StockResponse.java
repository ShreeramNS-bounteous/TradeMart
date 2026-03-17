package com.stockexchange.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StockResponse {

    private Long id;

    private String ticker;
    private String companyName;

    private BigDecimal currentPrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;

    private BigDecimal previousClosePrice;

    private BigDecimal priceChangeAmount;
    private BigDecimal priceChangePercent;

    private String priceDirection;

    private Long totalSharesAvailable;
    private boolean active;

    private LocalDateTime lastUpdatedAt;

}