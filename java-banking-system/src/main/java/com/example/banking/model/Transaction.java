package com.example.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction — an immutable record of a single financial event.
 */
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST }

    private final Type type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(Type type, BigDecimal amount, BigDecimal balanceAfter, String description) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f TL → Balance: %.2f TL (%s)",
                timestamp.toLocalDate(), type, amount, balanceAfter, description);
    }
}
