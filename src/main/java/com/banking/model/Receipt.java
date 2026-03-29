package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Receipt Entity
 * GRASP - High Cohesion: Receipt focuses only on receipt generation data
 */
@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "receipt_id", unique = true, nullable = false, length = 30)
    private String receiptId;
    
    @Column(nullable = false, length = 20)
    private String receiptType; // TRANSACTION, BILL_PAYMENT, WITHDRAWAL, DEPOSIT
    
    @Column(length = 255)
    private String details;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        if (receiptId == null) {
            receiptId = generateReceiptId();
        }
    }
    
    private String generateReceiptId() {
        return "RCP" + System.currentTimeMillis();
    }
}
