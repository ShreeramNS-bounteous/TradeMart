package com.stockexchange.backend.enums;

public enum Role {

    ROLE_ADMIN,
    // Platform administrator
    // Can:
    // - Add or delist stocks
    // - Open / close market
    // - View all trades
    // - Manage users
    // - Resolve margin calls

    ROLE_USER
    // Normal trader
    // Can:
    // - Register / login
    // - Deposit funds
    // - Buy and sell stocks
    // - View portfolio
    // - View trade history
}