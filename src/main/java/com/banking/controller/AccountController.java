package com.banking.controller;

import com.banking.dto.TransferRequest;
import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AccountController - REST API for Account operations
 * GRASP - Controller: Handles account-related system operations
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {
    
    private final AccountService accountService;
    private final TransactionService transactionService;
    
    /**
     * Use Case: Create Account
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestParam Long customerId, 
                                          @RequestParam String accountType,
                                          @RequestParam Double initialBalance) {
        try {
            Account account = accountService.createAccount(customerId, accountType, initialBalance);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account created successfully");
            response.put("accountNumber", account.getAccountNumber());
            response.put("accountId", account.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get account details
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId) {
        try {
            Optional<Account> account = accountService.getAccountById(accountId);
            
            if (account.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
            
            return ResponseEntity.ok(account.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching account");
        }
    }
    
    /**
     * Use Case: Check Balance (Minor)
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> checkBalance(@PathVariable Long accountId) {
        try {
            Double balance = accountService.checkBalance(accountId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("balance", balance);
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all accounts for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerAccounts(@PathVariable Long customerId) {
        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching accounts");
        }
    }
    
    /**
     * Use Case: Transfer Money
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody TransferRequest transferRequest) {
        try {
            var transaction = transactionService.transferMoney(
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountNumber(),
                transferRequest.getAmount(),
                transferRequest.getDescription()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Transfer successful");
            response.put("transactionId", transaction.getId());
            response.put("transactionDate", transaction.getTransactionDate());
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Deposit money
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> depositMoney(@PathVariable Long accountId, @RequestParam Double amount) {
        try {
            accountService.depositMoney(accountId, amount);
            transactionService.recordDeposit(accountId, amount, "Deposit");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Deposit successful");
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Withdraw money
     */
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdrawMoney(@PathVariable Long accountId, @RequestParam Double amount) {
        try {
            accountService.withdrawMoney(accountId, amount);
            transactionService.recordWithdrawal(accountId, amount, "Withdrawal");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Withdrawal successful");
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
