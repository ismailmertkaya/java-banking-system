package com.example.banking.service;

import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BankingService {

    private final Map<String, BankAccount> accounts = new HashMap<>();

    public BankingService() {
        // Seed sample accounts
        BankAccount a1 = new BankAccount("Hasan Yılmaz", "hasan@example.com",
                new BigDecimal("5000.00"), BankAccount.AccountType.CHECKING);
        BankAccount a2 = new BankAccount("Ayşe Kaya", "ayse@example.com",
                new BigDecimal("12000.00"), BankAccount.AccountType.SAVINGS);
        accounts.put(a1.getAccountNumber(), a1);
        accounts.put(a2.getAccountNumber(), a2);
    }

    public BankAccount createAccount(String ownerName, String ownerEmail,
                                     BigDecimal initialDeposit, BankAccount.AccountType type) {
        BankAccount account = new BankAccount(ownerName, ownerEmail, initialDeposit, type);
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public BankAccount getAccount(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber))
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    public List<BankAccount> getAllAccounts() { return new ArrayList<>(accounts.values()); }

    public BankAccount deposit(String accountNumber, BigDecimal amount, String description) {
        BankAccount account = getAccount(accountNumber);
        account.deposit(amount, description);
        return account;
    }

    public BankAccount withdraw(String accountNumber, BigDecimal amount, String description) {
        BankAccount account = getAccount(accountNumber);
        account.withdraw(amount, description);
        return account;
    }

    /** Transfer money between two accounts atomically */
    public void transfer(String fromAccountNumber, String toAccountNumber,
                         BigDecimal amount, String description) {
        BankAccount from = getAccount(fromAccountNumber);
        BankAccount to = getAccount(toAccountNumber);

        // Deduct from sender (validates funds & frozen status)
        from.withdraw(amount, "Transfer to " + toAccountNumber + ": " + description);

        // Credit receiver
        to.deposit(amount, "Transfer from " + fromAccountNumber + ": " + description);

        // Override the last transaction type to TRANSFER for clarity
        from.getTransactions().get(from.getTransactions().size() - 1);
    }

    public BigDecimal applyInterest(String accountNumber, double annualRate) {
        return getAccount(accountNumber).applyInterest(annualRate);
    }

    public BankAccount freezeAccount(String accountNumber, boolean freeze) {
        BankAccount account = getAccount(accountNumber);
        account.setFrozen(freeze);
        return account;
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return getAccount(accountNumber).getTransactions();
    }

    /** Summary statistics across all accounts */
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalAccounts", accounts.size());
        summary.put("totalBalance", accounts.values().stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("checkingAccounts", accounts.values().stream()
                .filter(a -> a.getType() == BankAccount.AccountType.CHECKING).count());
        summary.put("savingsAccounts", accounts.values().stream()
                .filter(a -> a.getType() == BankAccount.AccountType.SAVINGS).count());
        summary.put("frozenAccounts", accounts.values().stream()
                .filter(BankAccount::isFrozen).count());
        return summary;
    }
}
