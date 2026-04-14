package com.onlinebanking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.onlinebanking.model.BillPayment;

/**
 * Service for managing bill payments, recharges, and utility payments
 */
public class BillPaymentService {
    private final ConcurrentHashMap<Long, BillPayment> billStore = new ConcurrentHashMap<>();
    private long billIdCounter = 1;

    // Predefined billers
    private static final String[][] BILLERS = {
        {"Electricity Board", "ELECTRICITY"},
        {"Water Department", "WATER"},
        {"Gas Company", "GAS"},
        {"Internet Provider", "INTERNET"},
        {"Mobile Operator", "MOBILE"},
        {"Credit Card", "CREDIT_CARD"},
        {"Insurance", "INSURANCE"}
    };

    public BillPaymentService() {}

    /**
     * Add a new bill payment
     */
    public BillPayment addBill(long accountId, String billerName, String category, 
                               String consumerNumber, BigDecimal amount, LocalDate dueDate) {
        BillPayment bill = new BillPayment(accountId, billerName, category, consumerNumber, amount, dueDate);
        bill.setBillId(billIdCounter++);
        billStore.put(bill.getBillId(), bill);
        return bill;
    }

    /**
     * Get pending bills for account
     */
    public List<BillPayment> getPendingBills(long accountId) {
        List<BillPayment> pendingBills = new ArrayList<>();
        for (BillPayment bill : billStore.values()) {
            if (bill.getAccountId() == accountId && "PENDING".equals(bill.getStatus())) {
                pendingBills.add(bill);
            }
        }
        return pendingBills;
    }

    /**
     * Get all bills for account
     */
    public List<BillPayment> getAllBills(long accountId) {
        List<BillPayment> allBills = new ArrayList<>();
        for (BillPayment bill : billStore.values()) {
            if (bill.getAccountId() == accountId) {
                allBills.add(bill);
            }
        }
        return allBills;
    }

    /**
     * Get a specific bill
     */
    public BillPayment getBill(long billId) {
        return billStore.getOrDefault(billId, null);
    }

    /**
     * Pay a bill
     */
    public synchronized BillPayment payBill(long billId, String referenceNumber) {
        BillPayment bill = billStore.get(billId);
        if (bill == null) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        if (!"PENDING".equals(bill.getStatus())) {
            throw new IllegalStateException("Bill is not pending");
        }

        bill.payBill(referenceNumber);
        billStore.put(billId, bill);
        return bill;
    }

    /**
     * Get overdue bills
     */
    public List<BillPayment> getOverdueBills(long accountId) {
        List<BillPayment> overdueBills = new ArrayList<>();
        for (BillPayment bill : billStore.values()) {
            if (bill.getAccountId() == accountId && bill.isOverdue()) {
                bill.markAsOverdue();
                billStore.put(bill.getBillId(), bill);
                overdueBills.add(bill);
            }
        }
        return overdueBills;
    }

    /**
     * Get total overdue amount for account
     */
    public BigDecimal getTotalOverdueAmount(long accountId) {
        BigDecimal totalOverdue = BigDecimal.ZERO;
        for (BillPayment bill : getOverdueBills(accountId)) {
            totalOverdue = totalOverdue.add(bill.getBillAmount());
            totalOverdue = totalOverdue.add(bill.calculateLatePenalty());
        }
        return totalOverdue;
    }

    /**
     * Schedule recurring bill payment (subscription)
     */
    public void scheduleRecurringPayment(long accountId, String billerName, String category,
                                        String consumerNumber, BigDecimal amount, LocalDate dueDate) {
        // In production, this would create recurring entries
        BillPayment bill = addBill(accountId, billerName, category, consumerNumber, amount, dueDate);
    }

    /**
     * Get available billers for user to add
     */
    public List<BillerInfo> getAvailableBillers() {
        List<BillerInfo> billers = new ArrayList<>();
        for (String[] biller : BILLERS) {
            billers.add(new BillerInfo(biller[0], biller[1]));
        }
        return billers;
    }

    /**
     * Get bill payment history (last 12 months)
     */
    public List<BillPayment> getBillPaymentHistory(long accountId) {
        List<BillPayment> history = new ArrayList<>();
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        for (BillPayment bill : billStore.values()) {
            if (bill.getAccountId() == accountId && "PAID".equals(bill.getStatus())) {
                if (bill.getPaymentDate() != null && bill.getPaymentDate().isAfter(oneYearAgo)) {
                    history.add(bill);
                }
            }
        }
        return history;
    }

    /**
     * Get bill payment analytics
     */
    public BillAnalytics getBillAnalytics(long accountId) {
        List<BillPayment> allBills = getAllBills(accountId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        int paidCount = 0;
        int pendingCount = 0;
        int overdueCount = 0;

        for (BillPayment bill : allBills) {
            totalAmount = totalAmount.add(bill.getBillAmount());
            if ("PAID".equals(bill.getStatus())) {
                paidCount++;
            } else if ("PENDING".equals(bill.getStatus())) {
                pendingCount++;
            } else if ("OVERDUE".equals(bill.getStatus())) {
                overdueCount++;
            }
        }

        return new BillAnalytics(totalAmount, paidCount, pendingCount, overdueCount);
    }

    // Inner class for biller information
    public static class BillerInfo {
        public String billerName;
        public String category;

        public BillerInfo(String billerName, String category) {
            this.billerName = billerName;
            this.category = category;
        }
    }

    // Inner class for bill analytics
    public static class BillAnalytics {
        public BigDecimal totalAmount;
        public int paidBills;
        public int pendingBills;
        public int overdueBills;

        public BillAnalytics(BigDecimal totalAmount, int paidBills, int pendingBills, int overdueBills) {
            this.totalAmount = totalAmount;
            this.paidBills = paidBills;
            this.pendingBills = pendingBills;
            this.overdueBills = overdueBills;
        }
    }
}
