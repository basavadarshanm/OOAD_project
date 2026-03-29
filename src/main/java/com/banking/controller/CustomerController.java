package com.banking.controller;

import com.banking.exception.BankingException;
import com.banking.model.Customer;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CustomerController - Manager operations for customer management
 * GRASP - Controller: Handles customer management system operations
 * Use Case: Manage Users (MAJOR)
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private final UserService userService;
    
    /**
     * Get customer by ID
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable Long customerId) {
        try {
            Optional<Customer> customer = userService.getCustomerById(customerId);
            
            if (customer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
            }
            
            return ResponseEntity.ok(customer.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching customer");
        }
    }
    
    /**
     * Update customer profile - Major Use Case: Manage Users
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = userService.updateCustomer(customerId, customerDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Customer profile updated successfully");
            response.put("customer", updatedCustomer);
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating customer");
        }
    }
    
    /**
     * Deactivate customer account - Major Use Case: Manage Users
     */
    @PostMapping("/{customerId}/deactivate")
    public ResponseEntity<?> deactivateCustomer(@PathVariable Long customerId) {
        try {
            userService.deactivateUser(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Customer account deactivated successfully");
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deactivating customer");
        }
    }
}
