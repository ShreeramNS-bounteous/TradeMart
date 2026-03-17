package com.stockexchange.backend.service;

import com.stockexchange.backend.entity.Order;
import com.stockexchange.backend.enums.OrderSide;
import com.stockexchange.backend.enums.OrderType;
import com.stockexchange.backend.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingEngineService {

    private final OrderRepository orderRepository;
    private final TradeService    tradeService;

    @Transactional
    public boolean tryMatchOrder(Order incomingOrder, BigDecimal currentPrice) {
        boolean anyMatchFound = false;

        while (true) {
            Long orderId = incomingOrder.getId();
            // Reload order to get latest filledQuantity from DB

            incomingOrder = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException(
                            "Order not found: " + orderId));

            int remaining = incomingOrder.getQuantity()
                    - incomingOrder.getFilledQuantity();

            if (remaining == 0) break;
            // Order fully filled — stop matching

            Long stockId = incomingOrder.getStock().getId();
            Long userId  = incomingOrder.getUser().getId();

            Optional<Order> match;
            BigDecimal executedPrice;

            if (incomingOrder.getSide() == OrderSide.BUY) {

                BigDecimal buyPrice = incomingOrder.getType() == OrderType.MARKET
                        ? currentPrice
                        : incomingOrder.getLimitPrice();

                match = orderRepository.findBestSellMatch(
                        stockId, buyPrice, userId);

                if (match.isEmpty()) {

                    tradeService.executeTrade(
                            incomingOrder,
                            currentPrice
                    );

                    log.info("EXCHANGE MATCH: BUY {} from EXCHANGE @ {}",
                            incomingOrder.getStock().getTicker(),
                            currentPrice);

                    anyMatchFound = true;
                    break;
                }
                // No more sell orders available

                Order sellOrder = match.get();

                executedPrice = sellOrder.getType() == OrderType.LIMIT
                        ? sellOrder.getLimitPrice()
                        : currentPrice;

                tradeService.executeUserToUserTrade(
                        incomingOrder,
                        sellOrder,
                        executedPrice);

                log.info("USER-TO-USER MATCH: BUY {} matched SELL from {} @ {}",
                        incomingOrder.getStock().getTicker(),
                        sellOrder.getUser().getUsername(),
                        executedPrice);

            } else {

                BigDecimal sellPrice = incomingOrder.getType() == OrderType.MARKET
                        ? currentPrice
                        : incomingOrder.getLimitPrice();

                match = orderRepository.findBestBuyMatch(
                        stockId, sellPrice, userId);

                if (match.isEmpty()) {

                    tradeService.executeTrade(
                            incomingOrder,
                            currentPrice
                    );

                    log.info("EXCHANGE MATCH: SELL {} to EXCHANGE @ {}",
                            incomingOrder.getStock().getTicker(),
                            currentPrice);

                    anyMatchFound = true;
                    break;
                }
                // No more buy orders available

                Order buyOrder = match.get();

                executedPrice = buyOrder.getType() == OrderType.LIMIT
                        ? buyOrder.getLimitPrice()
                        : currentPrice;

                tradeService.executeUserToUserTrade(
                        buyOrder,
                        incomingOrder,
                        executedPrice);

                log.info("USER-TO-USER MATCH: SELL {} matched BUY from {} @ {}",
                        incomingOrder.getStock().getTicker(),
                        buyOrder.getUser().getUsername(),
                        executedPrice);
            }

            anyMatchFound = true;
        }

        return anyMatchFound;
    }
}