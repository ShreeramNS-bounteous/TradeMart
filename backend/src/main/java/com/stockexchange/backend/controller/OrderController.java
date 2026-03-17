package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.request.PlaceOrderRequest;
import com.stockexchange.backend.dto.response.OrderResponse;
import com.stockexchange.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {

        OrderResponse response = orderService.placeOrder(request);

        HttpStatus status = switch (response.getStatus()) {
            case EXECUTED, PENDING, PARTIAL -> HttpStatus.CREATED;
            case REJECTED -> HttpStatus.OK;
            case CANCELLED -> HttpStatus.OK;
        };

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @GetMapping("/my/pending")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<OrderResponse>> getMyPendingOrders() {

        return ResponseEntity.ok(orderService.getMyPendingOrders());
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PutMapping("/cancel-all-pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> cancelAllPending() {

        orderService.cancelAllPendingOrdersAtMarketClose();

        return ResponseEntity.ok(
                Map.of("message",
                        "All pending orders cancelled successfully"));
    }
}