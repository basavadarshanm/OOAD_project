package com.banking.controller;

import com.banking.exception.BankingException;
import com.banking.model.Receipt;
import com.banking.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ReceiptController - REST API for Receipt operations
 * GRASP - Controller: Handles receipt generation system operations
 */
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReceiptController {
    
    private final ReceiptService receiptService;
    
    /**
     * Use Case: Generate Receipt (Minor)
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateReceipt(
            @RequestParam Long transactionId,
            @RequestParam String receiptType,
            @RequestParam String details) {
        try {
            Receipt receipt = receiptService.generateReceipt(transactionId, receiptType, details);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Receipt generated successfully");
            response.put("receiptId", receipt.getReceiptId());
            response.put("amount", receipt.getAmount());
            response.put("generatedAt", receipt.getGeneratedAt());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get receipt by receipt ID
     */
    @GetMapping("/{receiptId}")
    public ResponseEntity<?> getReceipt(@PathVariable String receiptId) {
        try {
            Optional<Receipt> receipt = receiptService.getReceiptByReceiptId(receiptId);
            
            if (receipt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found");
            }
            
            return ResponseEntity.ok(receipt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching receipt");
        }
    }
}
