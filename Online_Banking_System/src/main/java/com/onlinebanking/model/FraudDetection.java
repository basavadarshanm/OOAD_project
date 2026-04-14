package com.onlinebanking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FraudDetection {
    private long fraudId;
    private long transactionId;
    private long userId;
    private BigDecimal riskScore; // 0.0 to 1.0
    private String detectionType; // RULE_BASED, ANOMALY, ML_BASED
    private String reason;
    private String status; // FLAGGED, CONFIRMED, FALSE_POSITIVE
    private String flags; // Comma-separated flags: UNUSUAL_AMOUNT, UNUSUAL_TIME, GEOGRAPHIC_ANOMALY, etc.
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public FraudDetection() {}

    public FraudDetection(long transactionId, long userId, BigDecimal amount, 
                         BigDecimal averageTransactionAmount, boolean isUnusualTime) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.status = "FLAGGED";
        this.detectionType = "RULE_BASED";
        this.createdAt = LocalDateTime.now();
        this.riskScore = calculateRiskScore(amount, averageTransactionAmount, isUnusualTime);
    }

    /**
     * Calculate risk score based on various factors
     */
    private BigDecimal calculateRiskScore(BigDecimal amount, BigDecimal avgAmount, boolean isUnusualTime) {
        double score = 0.0;
        StringBuilder flagsBuilder = new StringBuilder();

        // Check for unusual transaction amount (> 3x average)
        if (amount.compareTo(avgAmount.multiply(BigDecimal.valueOf(3))) > 0) {
            score += 0.4;
            flagsBuilder.append("UNUSUAL_AMOUNT,");
        }

        // Check for transaction at unusual time (11 PM to 5 AM)
        if (isUnusualTime) {
            score += 0.2;
            flagsBuilder.append("UNUSUAL_TIME,");
        }

        // Check for multiple transactions in short time (handled elsewhere)
        score = Math.min(score, 1.0); // Cap at 1.0

        this.flags = flagsBuilder.toString();
        if (flags.endsWith(",")) {
            this.flags = flags.substring(0, flags.length() - 1);
        }

        return BigDecimal.valueOf(score);
    }

    public void confirmFraud() {
        this.status = "CONFIRMED";
        this.resolvedAt = LocalDateTime.now();
    }

    public void markAsFalsePositive() {
        this.status = "FALSE_POSITIVE";
        this.resolvedAt = LocalDateTime.now();
    }

    public boolean isSuspicious() {
        return riskScore.compareTo(BigDecimal.valueOf(0.5)) >= 0;
    }

    // Getters and Setters
    public long getFraudId() { return fraudId; }
    public void setFraudId(long fraudId) { this.fraudId = fraudId; }

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }

    public String getDetectionType() { return detectionType; }
    public void setDetectionType(String detectionType) { this.detectionType = detectionType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFlags() { return flags; }
    public void setFlags(String flags) { this.flags = flags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    @Override
    public String toString() {
        return "FraudDetection{" +
                "fraudId=" + fraudId +
                ", transactionId=" + transactionId +
                ", riskScore=" + riskScore +
                ", status='" + status + '\'' +
                ", flags='" + flags + '\'' +
                '}';
    }
}
