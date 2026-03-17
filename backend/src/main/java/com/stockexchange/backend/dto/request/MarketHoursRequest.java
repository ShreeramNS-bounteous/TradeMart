package com.stockexchange.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class MarketHoursRequest {

    @NotNull
    private LocalTime openTime;

    @NotNull
    private LocalTime closeTime;

    @NotBlank
    private String timezone;

    private boolean forceClose;

}