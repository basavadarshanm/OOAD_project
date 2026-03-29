package com.banking.controller;

import com.banking.exception.BankingException;
import com.banking.model.Transaction;
import com.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TransactionController - REST API for Transaction operations
 * GRASP - Controller: Handles transaction-related system operations
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * Use Case: Get Transaction History (Minor)
     */
    @GetMapping("/history/{accountId}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long accountId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionHistory(accountId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactions);
            response.put("totalRecords", transactions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching transaction history");
        }
    }
    
    /**
     * Get transactions by date range
     */
    @GetMapping("/history/{accountId}/range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(accountId, start, end);
            
            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactions);
            response.put("totalRecords", transactions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format");
        }
    }
}
