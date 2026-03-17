package com.stockexchange.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TradeResponse {

    private Long id;

    private String ticker;
    private String companyName;

    private String buyerUsername;
    private String sellerUsername;

    private Integer quantity;

    private BigDecimal executedPrice;
    private BigDecimal totalTradeValue;

    private String side;

    private LocalDateTime executedAt;

}