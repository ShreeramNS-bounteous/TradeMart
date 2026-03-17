package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.response.StockResponse;
import com.stockexchange.backend.entity.Stock;
import com.stockexchange.backend.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/all")
    public ResponseEntity<List<StockResponse>> getAllStocks() {

        return ResponseEntity.ok(
                stockService.getAllActiveStocks());
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<StockResponse> getStockByTicker(
            @PathVariable String ticker) {

        return ResponseEntity.ok(
                stockService.getStockByTicker(ticker));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> addStock(
            @RequestBody Stock stock) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stockService.addStock(stock));
    }

    @PutMapping("/{ticker}/delist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> delistStock(
            @PathVariable String ticker) {

        stockService.delistStock(ticker);

        return ResponseEntity.ok(
                Map.of("message",
                        ticker + " delisted successfully"));
    }

    @PutMapping("/initialize-day")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> initializeDayPrices() {

        stockService.initializeDayPrices();

        return ResponseEntity.ok(
                Map.of("message",
                        "Day prices initialized for all stocks"));
    }
}