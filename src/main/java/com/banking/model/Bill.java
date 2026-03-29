package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Bill Entity
 * GRASP - High Cohesion: Bill focuses only on bill payment data
 */
@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bill_id", unique = true, nullable = false, length = 30)
    private String billId;
    
    @Column(nullable = false, length = 50)
    private String billType; // ELECTRICITY, WATER, GAS, INTERNET, etc.
    
    @Column(nullable = false)
    private Double billAmount;
    
    @Column(length = 100)
    private String biller;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(nullable = false, length = 20)
    private String status; // PENDING, PAID, OVERDUE
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "paid_date")
    private LocalDateTime paidDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (billId == null) {
            billId = generateBillId();
        }
    }
    
    private String generateBillId() {
        return "BILL" + System.currentTimeMillis();
    }
}
