package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.response.PortfolioResponse;
import com.stockexchange.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PortfolioResponse> getMyPortfolio() {

        return ResponseEntity.ok(
                portfolioService.getMyPortfolio());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PortfolioResponse> getPortfolioByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                portfolioService.getPortfolioByUserId(userId));
    }
}