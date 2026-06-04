package com.example.banking.controller;

import com.example.banking.model.*;
import com.example.banking.service.BankingService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/accounts")
public class BankingController {

    private final BankingService service;

    public BankingController(BankingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAll() {
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccount> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getAccount(accountNumber));
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getTransactionHistory(accountNumber));
    }

    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@RequestBody Map<String, String> body) {
        BankAccount account = service.createAccount(
                body.get("ownerName"),
                body.get("ownerEmail"),
                new BigDecimal(body.get("initialDeposit")),
                BankAccount.AccountType.valueOf(body.get("type"))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<BankAccount> deposit(@PathVariable String accountNumber,
                                               @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.deposit(accountNumber,
                new BigDecimal(body.get("amount")),
                body.getOrDefault("description", "Deposit")));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<BankAccount> withdraw(@PathVariable String accountNumber,
                                                @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.withdraw(accountNumber,
                new BigDecimal(body.get("amount")),
                body.getOrDefault("description", "Withdrawal")));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody Map<String, String> body) {
        service.transfer(body.get("from"), body.get("to"),
                new BigDecimal(body.get("amount")),
                body.getOrDefault("description", "Transfer"));
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    @PostMapping("/{accountNumber}/interest")
    public ResponseEntity<Map<String, Object>> applyInterest(
            @PathVariable String accountNumber,
            @RequestBody Map<String, String> body) {
        double rate = Double.parseDouble(body.getOrDefault("annualRate", "5.0"));
        BigDecimal interest = service.applyInterest(accountNumber, rate);
        return ResponseEntity.ok(Map.of("interestApplied", interest));
    }

    @PatchMapping("/{accountNumber}/freeze")
    public ResponseEntity<BankAccount> freeze(@PathVariable String accountNumber,
                                              @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(service.freezeAccount(accountNumber, body.get("frozen")));
    }
}
