package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.request.MarketHoursRequest;
import com.stockexchange.backend.entity.MarketHours;
import com.stockexchange.backend.enums.MarketStatus;
import com.stockexchange.backend.service.MarketHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketHoursService marketHoursService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMarketStatus() {

        MarketStatus status = marketHoursService.getMarketStatus();
        MarketHours  hours  = marketHoursService.getActiveMarketHours();

        return ResponseEntity.ok(Map.of(
                "status",    status,
                "openTime",  hours.getOpenTime(),
                "closeTime", hours.getCloseTime(),
                "timezone",  hours.getTimezone(),
                "isOpen",    marketHoursService.isMarketOpen()
        ));
    }

    @PutMapping("/hours")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarketHours> updateMarketHours(
            @Valid @RequestBody MarketHoursRequest request) {

        return ResponseEntity.ok(
                marketHoursService.updateMarketHours(request));
    }

    @PutMapping("/open")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> openMarket() {

        marketHoursService.toggleMarket(true);

        return ResponseEntity.ok(
                Map.of("message", "Market opened successfully"));
    }

    @PutMapping("/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> closeMarket() {

        marketHoursService.toggleMarket(false);

        return ResponseEntity.ok(
                Map.of("message", "Market closed successfully"));
    }
}