package com.banking.controller;

import com.banking.dto.LoginRequest;
import com.banking.dto.SignupRequest;
import com.banking.exception.BankingException;
import com.banking.model.Customer;
import com.banking.model.User;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AuthController - Controller Layer
 * GRASP - Controller: Handles all authentication-related system operations
 * Demonstrates GRASP principle of assigning responsibility to controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    
    /**
     * Use Case: Create Account (Signup)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            Customer customer = new Customer();
            customer.setUsername(signupRequest.getUsername());
            customer.setEmail(signupRequest.getEmail());
            customer.setFullName(signupRequest.getFullName());
            customer.setPhoneNumber(signupRequest.getPhoneNumber());
            
            Customer savedCustomer = userService.signup(customer, signupRequest.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signup successful");
            response.put("customerId", savedCustomer.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BankingException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * Use Case: Login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("userType", user.getClass().getSimpleName());
            
            return ResponseEntity.ok(response);
        } catch (BankingException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * Get user profile
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            Optional<User> user = userService.getUserById(userId);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            
            return ResponseEntity.ok(user.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
        }
    }
}
