package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.response.OrderResponse;
import com.stockexchange.backend.dto.response.PortfolioResponse;
import com.stockexchange.backend.dto.response.TradeResponse;
import com.stockexchange.backend.entity.MarginAccount;
import com.stockexchange.backend.entity.User;
import com.stockexchange.backend.enums.Role;
import com.stockexchange.backend.repository.MarginAccountRepository;
import com.stockexchange.backend.repository.UserRepository;
import com.stockexchange.backend.service.OrderService;
import com.stockexchange.backend.service.PortfolioService;
import com.stockexchange.backend.service.StockService;
import com.stockexchange.backend.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository          userRepository;
    private final MarginAccountRepository marginAccountRepository;
    private final PortfolioService        portfolioService;
    private final TradeService            tradeService;
    private final OrderService            orderService;
    private final StockService            stockService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(
                userRepository.findByActiveTrue());
    }

    @GetMapping("/users/inactive")
    public ResponseEntity<List<User>> getInactiveUsers() {
        return ResponseEntity.ok(
                userRepository.findByActiveFalse());
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + userId));

        if (user.getRole() == Role.ROLE_ADMIN) {

            long activeAdminCount = userRepository
                    .findByActiveTrue()
                    .stream()
                    .filter(u -> u.getRole() == Role.ROLE_ADMIN)
                    .count();

            if (activeAdminCount <= 1) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message",
                                "Cannot deactivate the last active admin"));
            }
        }

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User " + user.getUsername()
                        + " deactivated successfully"));
    }

    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<Map<String, String>> activateUser(
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + userId));

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User " + user.getUsername()
                        + " activated successfully"));
    }

    @GetMapping("/users/{userId}/portfolio")
    public ResponseEntity<PortfolioResponse> getUserPortfolio(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                portfolioService.getPortfolioByUserId(userId));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + userId));

        return ResponseEntity.ok(
                orderService.getOrdersByUser(user));
    }

    @GetMapping("/margin/calls")
    public ResponseEntity<List<MarginAccount>> getMarginCallAccounts() {

        return ResponseEntity.ok(
                marginAccountRepository.findAllMarginCallAccounts());
    }

    @GetMapping("/margin/active")
    public ResponseEntity<List<MarginAccount>> getActiveMarginUsers() {

        return ResponseEntity.ok(
                marginAccountRepository.findAllActiveMarginUsers());
    }

    @PutMapping("/margin/{userId}/resolve")
    public ResponseEntity<Map<String, String>> resolveMarginCall(
            @PathVariable Long userId) {

        MarginAccount margin = marginAccountRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Margin account not found for user: " + userId));

        margin.setMarginCallTriggered(false);
        marginAccountRepository.save(margin);

        return ResponseEntity.ok(Map.of(
                "message", "Margin call resolved for user: " + userId));
    }

    @GetMapping("/trades")
    public ResponseEntity<List<TradeResponse>> getAllTrades() {
        return ResponseEntity.ok(tradeService.getAllTrades());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPlatformSummary() {

        long totalUsers    = userRepository.count();
        long activeUsers   = userRepository.findByActiveTrue().size();
        long totalTrades   = tradeService.getAllTrades().size();
        long marginCalls   = marginAccountRepository
                .findAllMarginCallAccounts().size();
        long activeStocks  = stockService.getAllActiveStocks().size();

        return ResponseEntity.ok(Map.of(
                "totalUsers",       totalUsers,
                "activeUsers",      activeUsers,
                "totalTrades",      totalTrades,
                "activeMarginCalls", marginCalls,
                "activeStocks",     activeStocks
        ));
    }
}