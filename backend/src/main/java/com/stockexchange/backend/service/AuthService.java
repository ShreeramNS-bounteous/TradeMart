package com.stockexchange.backend.service;

import com.stockexchange.backend.dto.request.LoginRequest;
import com.stockexchange.backend.dto.request.RegisterRequest;
import com.stockexchange.backend.dto.response.AuthResponse;
import com.stockexchange.backend.entity.MarginAccount;
import com.stockexchange.backend.entity.Portfolio;
import com.stockexchange.backend.entity.User;
import com.stockexchange.backend.enums.Role;
import com.stockexchange.backend.repository.MarginAccountRepository;
import com.stockexchange.backend.repository.PortfolioRepository;
import com.stockexchange.backend.repository.UserRepository;
import com.stockexchange.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final MarginAccountRepository marginAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${margin.default.multiplier}")
    private BigDecimal defaultMarginMultiplier;

    /**
     * Handles user registration
     */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .balance(request.getInitialBalance())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        /*
         * Create Portfolio
         */
        Portfolio portfolio = Portfolio.builder()
                .user(savedUser)
                .totalInvestedValue(BigDecimal.ZERO)
                .currentMarketValue(BigDecimal.ZERO)
                .totalProfitLoss(BigDecimal.ZERO)
                .totalProfitLossPercentage(BigDecimal.ZERO)
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        portfolioRepository.save(portfolio);

        /*
         * Create Margin Account
         */
        BigDecimal marginLimit = request.getInitialBalance()
                .multiply(defaultMarginMultiplier);

        MarginAccount marginAccount = MarginAccount.builder()
                .user(savedUser)
                .marginLimit(marginLimit)
                .marginUsed(BigDecimal.ZERO)
                .marginAvailable(marginLimit)
                .marginMultiplier(defaultMarginMultiplier)
                .marginCallTriggered(false)
                .marginCallThreshold(
                        marginLimit.multiply(new BigDecimal("0.8"))
                )
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        marginAccountRepository.save(marginAccount);

        String token = jwtTokenProvider.generateToken(savedUser.getUsername());
        log.info("New user registered: {}", savedUser.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .balance(savedUser.getBalance())
                .build();
    }

    /**
     * Handles login authentication
     */
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        log.info("User logged in: {}", user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .balance(user.getBalance())
                .build();
    }
}