package com.stockexchange.backend.controller;

import com.stockexchange.backend.dto.response.CandleResponse;
import com.stockexchange.backend.service.CandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candles")
@RequiredArgsConstructor
public class CandleController {

    private final CandleService candleService;

    @GetMapping
    public List<CandleResponse> getCandles(
            @RequestParam String ticker
    ) {
        return candleService.getCandles(ticker);
    }
}