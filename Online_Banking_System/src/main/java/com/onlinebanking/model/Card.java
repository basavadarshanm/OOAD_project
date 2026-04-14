package com.onlinebanking.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Card {
    private long cardId;
    private long accountId;
    private String cardNumber;
    private String cardType; // DEBIT, CREDIT, PREPAID
    private LocalDate expiryDate;
    private String cvv;
    private String status; // ACTIVE, BLOCKED, EXPIRED
    private BigDecimal dailyLimit;
    private BigDecimal spendingLimit;
    private BigDecimal currentSpending;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Card() {}

    public Card(long accountId, String cardNumber, String cardType, LocalDate expiryDate, 
                String cvv, BigDecimal dailyLimit, BigDecimal spendingLimit) {
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.dailyLimit = dailyLimit;
        this.spendingLimit = spendingLimit;
        this.status = "ACTIVE";
        this.currentSpending = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public long getCardId() { return cardId; }
    public void setCardId(long cardId) { this.cardId = cardId; }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(BigDecimal dailyLimit) { this.dailyLimit = dailyLimit; }

    public BigDecimal getSpendingLimit() { return spendingLimit; }
    public void setSpendingLimit(BigDecimal spendingLimit) { this.spendingLimit = spendingLimit; }

    public BigDecimal getCurrentSpending() { return currentSpending; }
    public void setCurrentSpending(BigDecimal currentSpending) { this.currentSpending = currentSpending; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void blockCard() {
        this.status = "BLOCKED";
        this.updatedAt = LocalDateTime.now();
    }

    public void unblockCard() {
        this.status = "ACTIVE";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) && LocalDate.now().isBefore(expiryDate);
    }

    public boolean canSwipe(BigDecimal amount) {
        return this.currentSpending.add(amount).compareTo(this.dailyLimit) <= 0
                && this.currentSpending.add(amount).compareTo(this.spendingLimit) <= 0;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", cardNumber='" + maskCardNumber() + '\'' +
                ", cardType='" + cardType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    private String maskCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
