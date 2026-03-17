package com.stockexchange.backend.dto.response;

import com.stockexchange.backend.enums.Role;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private Role role;
    private BigDecimal balance;

}