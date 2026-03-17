package com.stockexchange.backend.service;

import com.stockexchange.backend.dto.response.CandleResponse;
import com.stockexchange.backend.entity.PriceHistory;
import com.stockexchange.backend.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CandleService {

    private final PriceHistoryRepository priceHistoryRepository;

    public List<CandleResponse> getCandles(String ticker) {

        List<PriceHistory> prices =
                priceHistoryRepository
                        .findTop200ByTickerOrderByTimeDesc(ticker);

        Collections.reverse(prices); // oldest first

        Map<Long, List<PriceHistory>> grouped = new LinkedHashMap<>();

        int interval = 30;

        for (PriceHistory p : prices) {

            long bucket = (p.getTime() / interval) * interval;

            grouped.computeIfAbsent(bucket, k -> new ArrayList<>())
                    .add(p);
        }

        List<CandleResponse> candles = new ArrayList<>();

        for (Map.Entry<Long, List<PriceHistory>> entry : grouped.entrySet()) {

            List<PriceHistory> list = entry.getValue();

            BigDecimal open = list.get(0).getPrice();
            BigDecimal close = list.get(list.size() - 1).getPrice();

            BigDecimal high = list.stream()
                    .map(PriceHistory::getPrice)
                    .max(BigDecimal::compareTo).get();

            BigDecimal low = list.stream()
                    .map(PriceHistory::getPrice)
                    .min(BigDecimal::compareTo).get();

            candles.add(new CandleResponse(
                    entry.getKey(),
                    open, high, low, close
            ));
        }

        return candles;
    }
}