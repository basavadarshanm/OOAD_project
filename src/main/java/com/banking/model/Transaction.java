package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Transaction Entity
 * GRASP - High Cohesion: Transaction focuses only on transaction data
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transaction_id", unique = true, nullable = false, length = 30)
    private String transactionId;
    
    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType; // WITHDRAWAL, DEPOSIT, TRANSFER, BILL_PAYMENT
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status; // SUCCESS, PENDING, FAILED
    
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(name = "receiver_account_number")
    private String receiverAccountNumber;
    
    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
        if (transactionId == null) {
            transactionId = generateTransactionId();
        }
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
}
