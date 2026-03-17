package com.stockexchange.backend.repository;

import com.stockexchange.backend.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findTop200ByTickerOrderByTimeDesc(String ticker);
}