package com.banking.controller;

import com.banking.exception.BankingException;
import com.banking.model.Bill;
import com.banking.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * BillController - REST API for Bill operations
 * GRASP - Controller: Handles bill-related system operations
 */
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillController {
    
    private final BillService billService;
    
    /**
     * Create a bill for a customer
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBill(
            @RequestParam Long customerId,
            @RequestParam String billType,
            @RequestParam Double billAmount,
            @RequestParam String biller,
            @RequestParam String dueDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime due = LocalDateTime.parse(dueDate, formatter);
            
            Bill bill = billService.createBill(customerId, billType, billAmount, biller, due);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bill created successfully");
            response.put("billId", bill.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid date format"));
        }
    }
    
    /**
     * Use Case: Pay Bills (Major)
     */
    @PostMapping("/{billId}/pay")
    public ResponseEntity<?> payBill(@PathVariable Long billId, @RequestParam Long accountId) {
        try {
            Bill bill = billService.payBill(billId, accountId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bill paid successfully");
            response.put("billId", bill.getId());
            response.put("paidAmount", bill.getBillAmount());
            response.put("paidDate", bill.getPaidDate());
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get pending bills for a customer
     */
    @GetMapping("/customer/{customerId}/pending")
    public ResponseEntity<?> getPendingBills(@PathVariable Long customerId) {
        try {
            List<Bill> bills = billService.getPendingBills(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("bills", bills);
            response.put("totalPending", bills.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching bills");
        }
    }
    
    /**
     * Get all bills for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerBills(@PathVariable Long customerId) {
        try {
            List<Bill> bills = billService.getCustomerBills(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("bills", bills);
            response.put("totalBills", bills.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching bills");
        }
    }
}
