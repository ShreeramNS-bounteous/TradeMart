package com.stockexchange.backend.dto.request;

import com.stockexchange.backend.enums.OrderSide;
import com.stockexchange.backend.enums.OrderType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlaceOrderRequest {

    @NotBlank
    private String ticker;

    @NotNull
    private OrderSide side;

    @NotNull
    private OrderType type;

    @NotNull
    @Min(1)
    private Integer quantity;

    private BigDecimal limitPrice;

    private boolean useMargin;

}