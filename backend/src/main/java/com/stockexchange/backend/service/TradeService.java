package com.stockexchange.backend.service;

import com.stockexchange.backend.dto.response.TradeResponse;
import com.stockexchange.backend.entity.*;
import com.stockexchange.backend.enums.OrderSide;
import com.stockexchange.backend.enums.OrderStatus;
import com.stockexchange.backend.enums.OrderType;
import com.stockexchange.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository     tradeRepository;
    private final OrderRepository     orderRepository;
    private final UserRepository      userRepository;
    private final StockRepository     stockRepository;
    private final PortfolioService    portfolioService;
    private final MarginService       marginService;

    // ── Core Trade Execution ─────────────────────────────────────────────────

    @Transactional
    public void executeUserToUserTrade(Order buyOrder, Order sellOrder,
                                       BigDecimal executedPrice) {

        User  buyer  = buyOrder.getUser();
        User  seller = sellOrder.getUser();
        Stock stock  = buyOrder.getStock();

        int buyRemaining  = buyOrder.getQuantity()
                - buyOrder.getFilledQuantity();

        int sellRemaining = sellOrder.getQuantity()
                - sellOrder.getFilledQuantity();

        int qty = Math.min(buyRemaining, sellRemaining);

        BigDecimal tradeValue = executedPrice
                .multiply(BigDecimal.valueOf(qty));

        // ── Buyer side ──

        if (buyOrder.getType() == OrderType.MARKET) {

            if (buyOrder.isMarginOrder()) {
                marginService.allocateMargin(buyer, tradeValue);
            } else {
                buyer.setBalance(buyer.getBalance().subtract(tradeValue));
                userRepository.save(buyer);
            }
        }

        portfolioService.updateHoldingOnBuy(
                buyer,
                stock,
                qty,
                executedPrice,
                buyOrder.isMarginOrder());

        // ── Seller side ──

        if (sellOrder.isMarginOrder()) {
            marginService.releaseMargin(seller, tradeValue);
        } else {
            seller.setBalance(seller.getBalance().add(tradeValue));
            userRepository.save(seller);
        }

        portfolioService.updateHoldingOnSell(
                seller,
                stock,
                qty,
                executedPrice);

        // ── Update filled quantities ──

        buyOrder.setFilledQuantity(
                buyOrder.getFilledQuantity() + qty);

        sellOrder.setFilledQuantity(
                sellOrder.getFilledQuantity() + qty);

        updateOrderStatus(buyOrder, executedPrice, tradeValue);
        updateOrderStatus(sellOrder, executedPrice, tradeValue);

        // ── Trade record ──

        Trade trade = new Trade();

        trade.setBuyOrder(buyOrder);
        trade.setSellOrder(sellOrder);
        trade.setStock(stock);
        trade.setBuyer(buyer);
        trade.setSeller(seller);
        trade.setQuantity(qty);
        trade.setExecutedPrice(executedPrice);
        trade.setTotalTradeValue(tradeValue);
        trade.setExecutedAt(LocalDateTime.now());

        tradeRepository.save(trade);

        log.info(
                "PARTIAL/FULL trade: {} x{} @ {} | {} → {}",
                stock.getTicker(),
                qty,
                executedPrice,
                buyer.getUsername(),
                seller.getUsername());
    }

    private void updateOrderStatus(Order order,
                                   BigDecimal executedPrice,
                                   BigDecimal tradeValue) {

        int remaining = order.getQuantity()
                - order.getFilledQuantity();

        if (remaining == 0) {

            order.setStatus(OrderStatus.EXECUTED);
            order.setExecutedPrice(executedPrice);
            order.setTotalOrderValue(
                    executedPrice.multiply(
                            BigDecimal.valueOf(order.getQuantity())));
            order.setExecutedAt(LocalDateTime.now());

        } else {

            order.setStatus(OrderStatus.PARTIAL);
            order.setExecutedPrice(executedPrice);
        }

        orderRepository.save(order);
    }

    // ── Exchange Execution ───────────────────────────────────────────────────

    @Transactional
    public void executeTrade(Order order, BigDecimal executedPrice) {

        User  user  = order.getUser();
        Stock stock = order.getStock();
        int   qty   = order.getQuantity();

        BigDecimal tradeValue = executedPrice
                .multiply(BigDecimal.valueOf(qty));

        if (order.getSide() == OrderSide.BUY) {

            executeBuyTrade(
                    order,
                    user,
                    stock,
                    qty,
                    executedPrice,
                    tradeValue);

        } else {

            executeSellTrade(
                    order,
                    user,
                    stock,
                    qty,
                    executedPrice,
                    tradeValue);
        }
    }

    // ── Buy Execution ────────────────────────────────────────────────────────

    @Transactional
    protected void executeBuyTrade(Order order,
                                   User user,
                                   Stock stock,
                                   int qty,
                                   BigDecimal executedPrice,
                                   BigDecimal tradeValue) {

        if (order.getType() == OrderType.MARKET) {

            if (order.isMarginOrder()) {
                marginService.allocateMargin(user, tradeValue);
            } else {
                user.setBalance(
                        user.getBalance().subtract(tradeValue));
                userRepository.save(user);
            }
        }

        stock.setTotalSharesAvailable(
                stock.getTotalSharesAvailable() - qty);

        stockRepository.save(stock);

        portfolioService.updateHoldingOnBuy(
                user,
                stock,
                qty,
                executedPrice,
                order.isMarginOrder());

        updateOrderAsExecuted(order, executedPrice, tradeValue);

        Trade trade = buildTrade(
                order,
                user,
                null,
                stock,
                qty,
                executedPrice,
                tradeValue);

        tradeRepository.save(trade);
    }

    // ── Sell Execution ───────────────────────────────────────────────────────

    @Transactional
    protected void executeSellTrade(Order order,
                                    User user,
                                    Stock stock,
                                    int qty,
                                    BigDecimal executedPrice,
                                    BigDecimal tradeValue) {

        if (order.isMarginOrder()) {
            marginService.releaseMargin(user, tradeValue);
        } else {
            user.setBalance(user.getBalance().add(tradeValue));
            userRepository.save(user);
        }

        stock.setTotalSharesAvailable(
                stock.getTotalSharesAvailable() + qty);

        stockRepository.save(stock);

        portfolioService.updateHoldingOnSell(
                user,
                stock,
                qty,
                executedPrice);

        updateOrderAsExecuted(order, executedPrice, tradeValue);

        Trade trade = buildTrade(
                order,
                null,
                user,
                stock,
                qty,
                executedPrice,
                tradeValue);

        tradeRepository.save(trade);
    }

    // ── Trade History ────────────────────────────────────────────────────────

    public List<TradeResponse> getMyTradeHistory() {

        User user = getCurrentUser();

        return tradeRepository.findAllTradesByUser(user)
                .stream()
                .map(t -> toTradeResponse(t, user))
                .toList();
    }

    public List<TradeResponse> getAllTrades() {

        return tradeRepository.findAllTradesOrderedByDate()
                .stream()
                .map(this::toTradeResponseForAdmin)
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void updateOrderAsExecuted(Order order,
                                       BigDecimal executedPrice,
                                       BigDecimal tradeValue) {

        order.setStatus(OrderStatus.EXECUTED);
        order.setExecutedPrice(executedPrice);
        order.setTotalOrderValue(tradeValue);
        order.setExecutedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    private Trade buildTrade(Order order,
                             User buyer,
                             User seller,
                             Stock stock,
                             int qty,
                             BigDecimal executedPrice,
                             BigDecimal tradeValue) {

        Trade trade = new Trade();

        trade.setBuyOrder(
                order.getSide() == OrderSide.BUY ? order : null);

        trade.setSellOrder(
                order.getSide() == OrderSide.SELL ? order : null);

        trade.setStock(stock);
        trade.setBuyer(buyer);
        trade.setSeller(seller);
        trade.setQuantity(qty);
        trade.setExecutedPrice(executedPrice);
        trade.setTotalTradeValue(tradeValue);
        trade.setExecutedAt(LocalDateTime.now());

        return trade;
    }

    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + username));
    }

    public TradeResponse toTradeResponse(Trade trade,
                                         User requestingUser) {

        String side = "N/A";

        if (requestingUser != null) {
            side = trade.getBuyer() != null
                    && trade.getBuyer().getId()
                    .equals(requestingUser.getId())
                    ? "BUY" : "SELL";
        }

        return TradeResponse.builder()
                .id(trade.getId())
                .ticker(trade.getStock().getTicker())
                .companyName(trade.getStock().getCompanyName())
                .buyerUsername(trade.getBuyer() != null
                        ? trade.getBuyer().getUsername() : "EXCHANGE")
                .sellerUsername(trade.getSeller() != null
                        ? trade.getSeller().getUsername() : "EXCHANGE")
                .quantity(trade.getQuantity())
                .executedPrice(trade.getExecutedPrice())
                .totalTradeValue(trade.getTotalTradeValue())
                .side(side)
                .executedAt(trade.getExecutedAt())
                .build();
    }

    private TradeResponse toTradeResponseForAdmin(Trade trade) {

        String side = trade.getSeller() == null ? "BUY" : "SELL";

        return TradeResponse.builder()
                .id(trade.getId())
                .ticker(trade.getStock().getTicker())
                .companyName(trade.getStock().getCompanyName())
                .buyerUsername(trade.getBuyer() != null
                        ? trade.getBuyer().getUsername() : "EXCHANGE")
                .sellerUsername(trade.getSeller() != null
                        ? trade.getSeller().getUsername() : "EXCHANGE")
                .quantity(trade.getQuantity())
                .executedPrice(trade.getExecutedPrice())
                .totalTradeValue(trade.getTotalTradeValue())
                .side(side)
                .executedAt(trade.getExecutedAt())
                .build();
    }
}