package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Account Entity - GRASP Information Expert
 * Account is responsible for managing balance updates and account operations
 * as it has the necessary data about the account state
 */
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;
    
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType; // SAVINGS, CURRENT, etc.
    
    @Column(nullable = false)
    private Double balance;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    // One Account can have many transactions
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (accountNumber == null) {
            accountNumber = generateAccountNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
    
    /**
     * GRASP - Information Expert: Account manages balance updates
     * Withdraw operation handled by Account as it has the balance information
     */
    public boolean withdraw(Double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * GRASP - Information Expert: Account manages balance updates
     * Deposit operation handled by Account as it has the balance information
     */
    public void deposit(Double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    /**
     * Check if sufficient balance exists
     */
    public boolean hasSufficientBalance(Double amount) {
        return balance >= amount;
    }
}
