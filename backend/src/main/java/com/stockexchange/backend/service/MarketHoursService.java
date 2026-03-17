package com.stockexchange.backend.service;

import com.stockexchange.backend.dto.request.MarketHoursRequest;
import com.stockexchange.backend.entity.MarketHours;
import com.stockexchange.backend.entity.User;
import com.stockexchange.backend.enums.MarketStatus;
import com.stockexchange.backend.repository.MarketHoursRepository;
import com.stockexchange.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketHoursService {

    private final MarketHoursRepository marketHoursRepository;
    private final UserRepository        userRepository;

    @Value("${market.open.time}")
    private String defaultOpenTime;

    @Value("${market.close.time}")
    private String defaultCloseTime;

    @Value("${market.timezone}")
    private String defaultTimezone;

    // ── Market Status Check ──────────────────────────────────────────────────

    public boolean isMarketOpen() {

        MarketHours marketHours = getActiveMarketHours();

        if (!marketHours.isMarketOpen()) {
            return false;
        }

        ZonedDateTime now = ZonedDateTime.now(
                ZoneId.of(marketHours.getTimezone()));

        LocalTime currentTime = now.toLocalTime();

        return !currentTime.isBefore(marketHours.getOpenTime())
                && !currentTime.isAfter(marketHours.getCloseTime());
    }

    public MarketStatus getMarketStatus() {

        MarketHours marketHours = getActiveMarketHours();

        if (!marketHours.isMarketOpen()) {
            return MarketStatus.CLOSED;
        }

        ZonedDateTime now = ZonedDateTime.now(
                ZoneId.of(marketHours.getTimezone()));

        LocalTime currentTime = now.toLocalTime();

        if (currentTime.isBefore(marketHours.getOpenTime())) {
            return MarketStatus.PRE_MARKET;
        } else if (currentTime.isAfter(marketHours.getCloseTime())) {
            return MarketStatus.POST_MARKET;
        } else {
            return MarketStatus.OPEN;
        }
    }

    // ── Admin Controls ───────────────────────────────────────────────────────

    @Transactional
    public MarketHours updateMarketHours(MarketHoursRequest request) {

        validateMarketHoursRequest(request);

        MarketHours marketHours = getActiveMarketHours();
        User admin = getCurrentAdmin();

        marketHours.setOpenTime(request.getOpenTime());
        marketHours.setCloseTime(request.getCloseTime());
        marketHours.setTimezone(request.getTimezone());
        marketHours.setUpdatedBy(admin);
        marketHours.setLastUpdatedAt(
                java.time.LocalDateTime.now());

        if (request.isForceClose()) {
            marketHours.setMarketOpen(false);

            log.info("Market forcefully closed by admin: {}",
                    admin.getUsername());
        }

        MarketHours saved = marketHoursRepository.save(marketHours);

        log.info("Market hours updated by admin {}: {} - {}",
                admin.getUsername(),
                request.getOpenTime(),
                request.getCloseTime());

        return saved;
    }

    @Transactional
    public MarketHours toggleMarket(boolean open) {

        MarketHours marketHours = getActiveMarketHours();
        User admin = getCurrentAdmin();

        marketHours.setMarketOpen(open);
        marketHours.setUpdatedBy(admin);
        marketHours.setLastUpdatedAt(java.time.LocalDateTime.now());

        log.info("Market {} by admin: {}",
                open ? "OPENED" : "CLOSED",
                admin.getUsername());

        return marketHoursRepository.save(marketHours);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    public MarketHours getActiveMarketHours() {

        return marketHoursRepository.findTopByOrderByLastUpdatedAtDesc()
                .orElseGet(this::createDefaultMarketHours);
    }

    @Transactional
    private MarketHours createDefaultMarketHours() {

        MarketHours defaults = new MarketHours();

        defaults.setOpenTime(LocalTime.parse(defaultOpenTime));
        defaults.setCloseTime(LocalTime.parse(defaultCloseTime));
        defaults.setTimezone(defaultTimezone);

        defaults.setMarketOpen(false);

        defaults.setLastUpdatedAt(java.time.LocalDateTime.now());

        return marketHoursRepository.save(defaults);
    }

    private void validateMarketHoursRequest(MarketHoursRequest request) {

        if (request.getOpenTime().isAfter(request.getCloseTime())) {
            throw new IllegalArgumentException(
                    "Open time must be before close time");
        }
    }

    private User getCurrentAdmin() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }
}