package com.onlinebanking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.onlinebanking.model.FraudDetection;
import com.onlinebanking.model.Transaction;

/**
 * Service for detecting fraudulent transactions using rule-based and anomaly detection
 */
public class FraudDetectionService {
    private final ConcurrentHashMap<Long, FraudDetection> fraudStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Transaction>> userTransactionHistory = new ConcurrentHashMap<>();
    private long fraudIdCounter = 1;

    // Thresholds
    private static final BigDecimal HIGH_TRANSACTION_THRESHOLD = BigDecimal.valueOf(50000);
    private static final int SUSPICIOUS_TRANSACTION_COUNT_THRESHOLD = 5; // 5 transactions in 10 mins
    private static final LocalTime SUSPICIOUS_TIME_START = LocalTime.of(23, 0); // 11 PM
    private static final LocalTime SUSPICIOUS_TIME_END = LocalTime.of(5, 0); // 5 AM

    public FraudDetectionService() {}

    /**
     * Analyze transaction for fraud indicators
     */
    public FraudDetection analyzeTransaction(long transactionId, long userId, BigDecimal amount, 
                                            Transaction transaction, List<Transaction> userRecent) {
        FraudDetection fraud = new FraudDetection(transactionId, userId, amount, 
                                                  calculateAverageTransactionAmount(userRecent),
                                                  isUnusualTime());
        fraud.setFraudId(fraudIdCounter++);
        
        // Apply rule-based checks
        applyRuleBasedDetection(fraud, transaction, userRecent);
        
        if (fraud.isSuspicious()) {
            fraudStore.put(fraud.getFraudId(), fraud);
        }
        
        return fraud;
    }

    /**
     * Rule-based fraud detection logic
     */
    private void applyRuleBasedDetection(FraudDetection fraud, Transaction transaction, List<Transaction> userRecent) {
        double riskScore = 0.0;
        StringBuilder reasons = new StringBuilder();

        // Rule 1: Unusual transaction amount
        if (transaction.getAmount().compareTo(HIGH_TRANSACTION_THRESHOLD) > 0) {
            riskScore += 0.3;
            reasons.append("High transaction amount. ");
        }

        // Rule 2: Multiple rapid transactions
        int recentCount = (int) userRecent.stream()
            .filter(t -> java.time.temporal.ChronoUnit.MINUTES.between(
                t.getOccurredAt(), LocalDateTime.now()) < 10)
            .count();
        
        if (recentCount > SUSPICIOUS_TRANSACTION_COUNT_THRESHOLD) {
            riskScore += 0.3;
            reasons.append("Multiple rapid transactions. ");
        }

        // Rule 3: Unusual time
        if (isUnusualTime()) {
            riskScore += 0.2;
            reasons.append("Transaction at unusual time. ");
        }

        // Rule 4: Unusual recipient (new beneficiary)
        if (isNewBeneficiary(transaction)) {
            riskScore += 0.2;
            reasons.append("Transaction to new beneficiary. ");
        }

        fraud.setRiskScore(BigDecimal.valueOf(Math.min(riskScore, 1.0)));
        fraud.setReason(reasons.toString());
        fraud.setDetectionType("RULE_BASED");
    }

    /**
     * Check if transaction is at unusual time
     */
    private boolean isUnusualTime() {
        LocalTime now = LocalTime.now();
        return now.isAfter(SUSPICIOUS_TIME_START) || now.isBefore(SUSPICIOUS_TIME_END);
    }

    /**
     * Calculate average transaction amount for user
     */
    private BigDecimal calculateAverageTransactionAmount(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.valueOf(10000); // Default average
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            sum = sum.add(t.getAmount());
        }
        return sum.divide(BigDecimal.valueOf(transactions.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Check if beneficiary is new (not in user's transaction history)
     */
    private boolean isNewBeneficiary(Transaction transaction) {
        // This is a simplified check - in production, check against beneficiary table
        return false; // Placeholder
    }

    /**
     * Get flagged fraud cases
     */
    public List<FraudDetection> getFlaggedFraudCases() {
        List<FraudDetection> flagged = new ArrayList<>();
        for (FraudDetection fraud : fraudStore.values()) {
            if ("FLAGGED".equals(fraud.getStatus())) {
                flagged.add(fraud);
            }
        }
        return flagged;
    }

    /**
     * Get fraud cases for a user
     */
    public List<FraudDetection> getUserFraudCases(long userId) {
        List<FraudDetection> userCases = new ArrayList<>();
        for (FraudDetection fraud : fraudStore.values()) {
            if (fraud.getUserId() == userId) {
                userCases.add(fraud);
            }
        }
        return userCases;
    }

    /**
     * Confirm fraud case (Admin)
     */
    public FraudDetection confirmFraud(long fraudId) {
        FraudDetection fraud = fraudStore.get(fraudId);
        if (fraud != null) {
            fraud.confirmFraud();
            fraudStore.put(fraudId, fraud);
        }
        return fraud;
    }

    /**
     * Mark as false positive
     */
    public FraudDetection markAsFalsePositive(long fraudId) {
        FraudDetection fraud = fraudStore.get(fraudId);
        if (fraud != null) {
            fraud.markAsFalsePositive();
            fraudStore.put(fraudId, fraud);
        }
        return fraud;
    }

    /**
     * Get fraud statistics for dashboard
     */
    public FraudStatistics getFraudStatistics() {
        int totalFlagged = 0;
        int confirmed = 0;
        int falsePositives = 0;
        BigDecimal totalRiskAmount = BigDecimal.ZERO;

        for (FraudDetection fraud : fraudStore.values()) {
            if ("FLAGGED".equals(fraud.getStatus())) {
                totalFlagged++;
            } else if ("CONFIRMED".equals(fraud.getStatus())) {
                confirmed++;
            } else if ("FALSE_POSITIVE".equals(fraud.getStatus())) {
                falsePositives++;
            }
        }

        return new FraudStatistics(totalFlagged, confirmed, falsePositives, totalRiskAmount);
    }

    // Inner class for statistics
    public static class FraudStatistics {
        public int totalFlagged;
        public int confirmedFrauds;
        public int falsePositives;
        public BigDecimal totalRiskAmount;

        public FraudStatistics(int totalFlagged, int confirmed, int falsePositives, BigDecimal totalRiskAmount) {
            this.totalFlagged = totalFlagged;
            this.confirmedFrauds = confirmed;
            this.falsePositives = falsePositives;
            this.totalRiskAmount = totalRiskAmount;
        }

        public int getAccuracyRate() {
            if (totalFlagged == 0) return 0;
            return (confirmedFrauds * 100) / totalFlagged;
        }
    }
}
