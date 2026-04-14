package com.onlinebanking.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Loan {
    private long loanId;
    private long userId;
    private long accountId;
    private String loanType; // PERSONAL, HOME, AUTO, EDUCATION
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int loanTerm; // in months
    private BigDecimal monthlyEmi;
    private BigDecimal remainingBalance;
    private String status; // APPLIED, APPROVED, REJECTED, ACTIVE, CLOSED
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String purpose;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Loan() {}

    public Loan(long userId, long accountId, String loanType, BigDecimal principalAmount, 
                BigDecimal interestRate, int loanTerm, String purpose) {
        this.userId = userId;
        this.accountId = accountId;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        this.purpose = purpose;
        this.status = "APPLIED";
        this.remainingBalance = principalAmount;
        this.applicationDate = LocalDate.now();
        this.monthlyEmi = calculateEmi();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate EMI using simple formula: EMI = (P * r * (1+r)^n) / ((1+r)^n - 1)
     * Where: P = Principal, r = Monthly interest rate, n = Number of months
     */
    public BigDecimal calculateEmi() {
        if (principalAmount == null || interestRate == null || loanTerm <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal principal = principalAmount;
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), 10, java.math.RoundingMode.HALF_UP);
        
        if (monthlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            return principal.divide(BigDecimal.valueOf(loanTerm), 2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal numerator = monthlyRate.multiply(
            monthlyRate.add(BigDecimal.ONE).pow(loanTerm)
        );
        BigDecimal denominator = monthlyRate.add(BigDecimal.ONE).pow(loanTerm).subtract(BigDecimal.ONE);

        return principal.multiply(numerator).divide(denominator, 2, java.math.RoundingMode.HALF_UP);
    }

    public void approveLoan() {
        this.status = "APPROVED";
        this.approvalDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void rejectLoan() {
        this.status = "REJECTED";
        this.updatedAt = LocalDateTime.now();
    }

    public void activateLoan() {
        this.status = "ACTIVE";
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusMonths(loanTerm);
        this.updatedAt = LocalDateTime.now();
    }

    public void closeLoan() {
        this.status = "CLOSED";
        this.remainingBalance = BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public long getLoanId() { return loanId; }
    public void setLoanId(long loanId) { this.loanId = loanId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { 
        this.principalAmount = principalAmount;
        this.monthlyEmi = calculateEmi();
    }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { 
        this.interestRate = interestRate;
        this.monthlyEmi = calculateEmi();
    }

    public int getLoanTerm() { return loanTerm; }
    public void setLoanTerm(int loanTerm) { 
        this.loanTerm = loanTerm;
        this.monthlyEmi = calculateEmi();
    }

    public BigDecimal getMonthlyEmi() { return monthlyEmi; }
    public void setMonthlyEmi(BigDecimal monthlyEmi) { this.monthlyEmi = monthlyEmi; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }

    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", loanType='" + loanType + '\'' +
                ", principalAmount=" + principalAmount +
                ", monthlyEmi=" + monthlyEmi +
                ", status='" + status + '\'' +
                '}';
    }
}
