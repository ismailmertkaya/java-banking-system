package com.example.banking.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bank Account — stores balance and transaction history.
 * Uses BigDecimal for all monetary calculations (never double/float for money!).
 */
public class BankAccount {

    public enum AccountType { CHECKING, SAVINGS }

    private String accountNumber;
    private String ownerName;
    private String ownerEmail;
    private BigDecimal balance;
    private AccountType type;
    private boolean frozen = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount() {}

    public BankAccount(String ownerName, String ownerEmail,
                       BigDecimal initialDeposit, AccountType type) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.balance = initialDeposit;
        this.type = type;
        logTransaction(Transaction.Type.DEPOSIT, initialDeposit, "Initial deposit");
    }

    public void deposit(BigDecimal amount, String description) {
        validateNotFrozen();
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");
        balance = balance.add(amount);
        logTransaction(Transaction.Type.DEPOSIT, amount, description);
    }

    public void withdraw(BigDecimal amount, String description) {
        validateNotFrozen();
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (balance.compareTo(amount) < 0)
            throw new IllegalStateException("Insufficient funds. Balance: " + balance + ", Requested: " + amount);
        balance = balance.subtract(amount);
        logTransaction(Transaction.Type.WITHDRAWAL, amount, description);
    }

    /** Apply monthly interest for SAVINGS accounts */
    public BigDecimal applyInterest(double annualRatePercent) {
        if (type != AccountType.SAVINGS)
            throw new IllegalStateException("Interest only applies to SAVINGS accounts");
        BigDecimal monthlyRate = BigDecimal.valueOf(annualRatePercent / 100 / 12);
        BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
        balance = balance.add(interest);
        logTransaction(Transaction.Type.INTEREST, interest,
                String.format("Monthly interest at %.2f%% annual rate", annualRatePercent));
        return interest;
    }

    private void logTransaction(Transaction.Type type, BigDecimal amount, String desc) {
        transactions.add(new Transaction(type, amount, balance, desc));
    }

    private void validateNotFrozen() {
        if (frozen) throw new IllegalStateException("Account is frozen");
    }

    private String generateAccountNumber() {
        return "TR" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerEmail() { return ownerEmail; }
    public BigDecimal getBalance() { return balance; }
    public AccountType getType() { return type; }
    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setAccountNumber(String n) { this.accountNumber = n; }
    public void setOwnerName(String n) { this.ownerName = n; }
    public void setOwnerEmail(String e) { this.ownerEmail = e; }
    public void setBalance(BigDecimal b) { this.balance = b; }
    public void setType(AccountType t) { this.type = t; }
    public void setTransactions(List<Transaction> t) { this.transactions = t; }
}
