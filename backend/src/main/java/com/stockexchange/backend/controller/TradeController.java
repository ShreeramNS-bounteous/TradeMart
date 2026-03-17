package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.response.TradeResponse;
import com.stockexchange.backend.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TradeResponse>> getMyTradeHistory() {

        return ResponseEntity.ok(
                tradeService.getMyTradeHistory());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TradeResponse>> getAllTrades() {

        return ResponseEntity.ok(
                tradeService.getAllTrades());
    }
}