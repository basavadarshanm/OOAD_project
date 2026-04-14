package com.onlinebanking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.onlinebanking.model.Loan;

/**
 * Service for managing loans with EMI calculation, approval workflow, and tracking
 */
public class LoanService {
    private final ConcurrentHashMap<Long, Loan> loanStore = new ConcurrentHashMap<>();
    private long loanIdCounter = 1;

    public LoanService() {}

    /**
     * Apply for a new loan
     */
    public Loan applyForLoan(long userId, long accountId, String loanType, BigDecimal amount, 
                             BigDecimal interestRate, int term, String purpose) {
        if (!isValidLoanType(loanType)) {
            throw new IllegalArgumentException("Invalid loan type: " + loanType);
        }

        Loan loan = new Loan(userId, accountId, loanType, amount, interestRate, term, purpose);
        loan.setLoanId(loanIdCounter++);
        loanStore.put(loan.getLoanId(), loan);
        return loan;
    }

    /**
     * Get all loans for a user
     */
    public List<Loan> getLoansByUser(long userId) {
        List<Loan> userLoans = new ArrayList<>();
        for (Loan loan : loanStore.values()) {
            if (loan.getUserId() == userId) {
                userLoans.add(loan);
            }
        }
        return userLoans;
    }

    /**
     * Get a specific loan
     */
    public Loan getLoan(long loanId) {
        return loanStore.getOrDefault(loanId, null);
    }

    /**
     * Approve a loan application (Admin)
     */
    public Loan approveLoan(long loanId) {
        Loan loan = loanStore.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found: " + loanId);
        }
        if (!"APPLIED".equals(loan.getStatus())) {
            throw new IllegalStateException("Can only approve loans in APPLIED status");
        }
        loan.approveLoan();
        loanStore.put(loanId, loan);
        return loan;
    }

    /**
     * Reject a loan application (Admin)
     */
    public Loan rejectLoan(long loanId) {
        Loan loan = loanStore.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found: " + loanId);
        }
        if (!"APPLIED".equals(loan.getStatus())) {
            throw new IllegalStateException("Can only reject loans in APPLIED status");
        }
        loan.rejectLoan();
        loanStore.put(loanId, loan);
        return loan;
    }

    /**
     * Activate an approved loan
     */
    public Loan activateLoan(long loanId) {
        Loan loan = loanStore.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found: " + loanId);
        }
        if (!"APPROVED".equals(loan.getStatus())) {
            throw new IllegalStateException("Can only activate approved loans");
        }
        loan.activateLoan();
        loanStore.put(loanId, loan);
        return loan;
    }

    /**
     * Record EMI payment and update balance
     */
    public synchronized boolean recordEmiPayment(long loanId, BigDecimal paymentAmount) {
        Loan loan = loanStore.get(loanId);
        if (loan == null || !"ACTIVE".equals(loan.getStatus())) {
            return false;
        }

        BigDecimal newBalance = loan.getRemainingBalance().subtract(paymentAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.closeLoan();
        } else {
            loan.setRemainingBalance(newBalance);
        }

        loanStore.put(loanId, loan);
        return true;
    }

    /**
     * Get loan details with amortization info
     */
    public LoanDetails getLoanDetails(long loanId) {
        Loan loan = loanStore.get(loanId);
        if (loan == null) {
            return null;
        }

        return new LoanDetails(
            loan.getLoanId(),
            loan.getPrincipalAmount(),
            loan.getInterestRate(),
            loan.getLoanTerm(),
            loan.getMonthlyEmi(),
            loan.getRemainingBalance(),
            loan.getStatus(),
            loan.getApplicationDate(),
            loan.getApprovalDate(),
            loan.getStartDate(),
            loan.getEndDate()
        );
    }

    /**
     * Get all active loans (for admin dashboard)
     */
    public List<Loan> getAllActiveLans() {
        List<Loan> activeLoans = new ArrayList<>();
        for (Loan loan : loanStore.values()) {
            if ("ACTIVE".equals(loan.getStatus())) {
                activeLoans.add(loan);
            }
        }
        return activeLoans;
    }

    /**
     * Get all pending loan applications (for admin review)
     */
    public List<Loan> getPendingLoanApplications() {
        List<Loan> pendingLoans = new ArrayList<>();
        for (Loan loan : loanStore.values()) {
            if ("APPLIED".equals(loan.getStatus())) {
                pendingLoans.add(loan);
            }
        }
        return pendingLoans;
    }

    private boolean isValidLoanType(String loanType) {
        return "PERSONAL".equals(loanType) || "HOME".equals(loanType) || 
               "AUTO".equals(loanType) || "EDUCATION".equals(loanType);
    }

    // Inner class for detailed loan information
    public static class LoanDetails {
        public long loanId;
        public BigDecimal principal;
        public BigDecimal interestRate;
        public int term;
        public BigDecimal monthlyEmi;
        public BigDecimal remainingBalance;
        public String status;
        public LocalDate applicationDate;
        public LocalDate approvalDate;
        public LocalDate startDate;
        public LocalDate endDate;
        public int monthsPaid;
        public BigDecimal totalInterest;

        public LoanDetails(long loanId, BigDecimal principal, BigDecimal interestRate, int term,
                          BigDecimal monthlyEmi, BigDecimal remainingBalance, String status,
                          LocalDate applicationDate, LocalDate approvalDate, LocalDate startDate, LocalDate endDate) {
            this.loanId = loanId;
            this.principal = principal;
            this.interestRate = interestRate;
            this.term = term;
            this.monthlyEmi = monthlyEmi;
            this.remainingBalance = remainingBalance;
            this.status = status;
            this.applicationDate = applicationDate;
            this.approvalDate = approvalDate;
            this.startDate = startDate;
            this.endDate = endDate;
            
            if (startDate != null) {
                this.monthsPaid = (int) java.time.temporal.ChronoUnit.MONTHS.between(startDate, LocalDate.now());
            }
            this.totalInterest = monthlyEmi.multiply(BigDecimal.valueOf(term)).subtract(principal);
        }
    }
}
