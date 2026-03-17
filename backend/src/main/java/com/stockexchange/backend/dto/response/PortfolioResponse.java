package com.stockexchange.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PortfolioResponse {

    private String username;

    private BigDecimal availableBalance;

    private BigDecimal totalInvestedValue;
    private BigDecimal currentMarketValue;

    private BigDecimal totalProfitLoss;
    private BigDecimal totalProfitLossPercentage;

    private BigDecimal totalPortfolioValue;

    private List<HoldingResponse> holdings;

    private boolean marginEnabled;
    private BigDecimal marginUsed;
    private BigDecimal marginAvailable;

    private boolean marginCallTriggered;

    private LocalDateTime lastUpdatedAt;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class HoldingResponse {

        private String ticker;
        private String companyName;

        private Integer quantityOwned;

        private BigDecimal averageBuyPrice;
        private BigDecimal currentPrice;

        private BigDecimal currentValue;
        private BigDecimal investedAmount;

        private BigDecimal profitLoss;
        private BigDecimal profitLossPercent;

        private String priceDirection;

    }
}