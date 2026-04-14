package com.onlinebanking.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillPayment {
    private long billId;
    private long accountId;
    private String billerName;
    private String billerCategory; // ELECTRICITY, WATER, GAS, INTERNET, MOBILE, etc.
    private String consumerNumber;
    private BigDecimal billAmount;
    private LocalDate billDate;
    private LocalDate dueDate;
    private String status; // PENDING, PAID, OVERDUE, CANCELLED
    private LocalDate paymentDate;
    private String referenceNumber;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillPayment() {}

    public BillPayment(long accountId, String billerName, String billerCategory, 
                      String consumerNumber, BigDecimal billAmount, LocalDate dueDate) {
        this.accountId = accountId;
        this.billerName = billerName;
        this.billerCategory = billerCategory;
        this.consumerNumber = consumerNumber;
        this.billAmount = billAmount;
        this.billDate = LocalDate.now();
        this.dueDate = dueDate;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void payBill(String referenceNumber) {
        this.status = "PAID";
        this.paymentDate = LocalDate.now();
        this.referenceNumber = referenceNumber;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsOverdue() {
        if (LocalDate.now().isAfter(dueDate) && "PENDING".equals(status)) {
            this.status = "OVERDUE";
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !"PAID".equals(status);
    }

    public BigDecimal calculateLatePenalty() {
        if (!isOverdue()) {
            return BigDecimal.ZERO;
        }
        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        BigDecimal penaltyPerDay = billAmount.multiply(BigDecimal.valueOf(0.01)); // 1% per day
        return penaltyPerDay.multiply(BigDecimal.valueOf(daysOverdue));
    }

    // Getters and Setters
    public long getBillId() { return billId; }
    public void setBillId(long billId) { this.billId = billId; }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getBillerName() { return billerName; }
    public void setBillerName(String billerName) { this.billerName = billerName; }

    public String getBillerCategory() { return billerCategory; }
    public void setBillerCategory(String billerCategory) { this.billerCategory = billerCategory; }

    public String getConsumerNumber() { return consumerNumber; }
    public void setConsumerNumber(String consumerNumber) { this.consumerNumber = consumerNumber; }

    public BigDecimal getBillAmount() { return billAmount; }
    public void setBillAmount(BigDecimal billAmount) { this.billAmount = billAmount; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getReferencenumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "BillPayment{" +
                "billId=" + billId +
                ", billerName='" + billerName + '\'' +
                ", billAmount=" + billAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
