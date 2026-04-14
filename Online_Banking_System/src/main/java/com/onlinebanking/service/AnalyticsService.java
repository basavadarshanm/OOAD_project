package com.onlinebanking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.onlinebanking.model.Transaction;

/**
 * Service for spending analysis, categorization, and financial insights
 */
public class AnalyticsService {
    
    private static final Map<String, String> CATEGORY_KEYWORDS = new HashMap<>();
    
    static {
        CATEGORY_KEYWORDS.put("FOOD", "restaurant,food,pizza,burger,cafe,grocery,supermarket");
        CATEGORY_KEYWORDS.put("TRANSPORT", "uber,taxi,gas,parking,metro,railway,fuel");
        CATEGORY_KEYWORDS.put("UTILITIES", "electricity,water,gas,internet,phone,mobile");
        CATEGORY_KEYWORDS.put("ENTERTAINMENT", "movie,theatre,game,spotify,netflix,gaming");
        CATEGORY_KEYWORDS.put("SHOPPING", "mall,store,clothing,amazon,flipkart,online");
        CATEGORY_KEYWORDS.put("HEALTHCARE", "hospital,doctor,pharmacy,medical,clinic");
        CATEGORY_KEYWORDS.put("TRANSFER", "transfer,account,bank");
        CATEGORY_KEYWORDS.put("OTHER", "");
    }

    public AnalyticsService() {}

    /**
     * Categorize a transaction based on description
     */
    public String categorizeTransaction(String description) {
        if (description == null) return "OTHER";

        String desc = description.toLowerCase();
        for (Map.Entry<String, String> entry : CATEGORY_KEYWORDS.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            
            String[] keywords = entry.getValue().split(",");
            for (String keyword : keywords) {
                if (desc.contains(keyword.trim())) {
                    return entry.getKey();
                }
            }
        }
        return "OTHER";
    }

    /**
     * Get spending by category for a period
     */
    public Map<String, BigDecimal> getSpendingByCategory(List<Transaction> transactions, 
                                                         LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getOccurredAt() == null) continue;
            LocalDate txnDate = t.getOccurredAt().toLocalDate();
            
            if (!txnDate.isBefore(startDate) && !txnDate.isAfter(endDate)) {
                String category = categorizeTransaction(t.getDescription());
                
                if ("TRANSFER".equals(t.getType()) || "WITHDRAWAL".equals(t.getType())) {
                    spendingByCategory.put(category, 
                        spendingByCategory.getOrDefault(category, BigDecimal.ZERO)
                            .add(t.getAmount()));
                }
            }
        }

        return spendingByCategory;
    }

    /**
     * Get monthly spending trend
     */
    public Map<YearMonth, BigDecimal> getMonthlySpendinTrend(List<Transaction> transactions, int months) {
        Map<YearMonth, BigDecimal> monthlySpending = new TreeMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 0; i < months; i++) {
            YearMonth ym = YearMonth.from(now.minusMonths(i));
            monthlySpending.put(ym, BigDecimal.ZERO);
        }

        for (Transaction t : transactions) {
            if (t.getOccurredAt() != null && ("TRANSFER".equals(t.getType()) || "WITHDRAWAL".equals(t.getType()))) {
                YearMonth ym = YearMonth.from(t.getOccurredAt());
                if (monthlySpending.containsKey(ym)) {
                    monthlySpending.put(ym, monthlySpending.get(ym).add(t.getAmount()));
                }
            }
        }

        return monthlySpending;
    }

    /**
     * Calculate spending insights
     */
    public SpendingInsights getSpendingInsights(List<Transaction> transactions) {
        BigDecimal totalSpending = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal avgTransactionAmount = BigDecimal.ZERO;
        BigDecimal maxTransaction = BigDecimal.ZERO;
        int transactionCount = 0;

        for (Transaction t : transactions) {
            if ("DEPOSIT".equals(t.getType())) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if ("TRANSFER".equals(t.getType()) || "WITHDRAWAL".equals(t.getType())) {
                totalSpending = totalSpending.add(t.getAmount());
                if (t.getAmount().compareTo(maxTransaction) > 0) {
                    maxTransaction = t.getAmount();
                }
            }
            transactionCount++;
        }

        if (transactionCount > 0) {
            avgTransactionAmount = totalSpending.divide(BigDecimal.valueOf(transactionCount), 2, 
                                                        java.math.RoundingMode.HALF_UP);
        }

        BigDecimal savings = totalIncome.subtract(totalSpending);
        int savingsPercentage = 0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsPercentage = savings.multiply(BigDecimal.valueOf(100))
                .divide(totalIncome, 0, java.math.RoundingMode.HALF_UP).intValue();
        }

        String insight = generateInsight(savingsPercentage);

        return new SpendingInsights(totalSpending, totalIncome, savings, savingsPercentage,
                                    avgTransactionAmount, maxTransaction, transactionCount, insight);
    }

    /**
     * Generate personalized spending insight message
     */
    private String generateInsight(int savingsPercentage) {
        if (savingsPercentage >= 50) {
            return "Excellent! You're saving more than 50% of your income. Keep up the good work!";
        } else if (savingsPercentage >= 30) {
            return "Good! You're maintaining a healthy savings rate of " + savingsPercentage + "%.";
        } else if (savingsPercentage >= 10) {
            return "You're saving " + savingsPercentage + "%. Consider increasing your savings habits.";
        } else {
            return "Your spending is high. Consider reviewing your expenses and creating a budget.";
        }
    }

    /**
     * Compare spending with previous period
     */
    public SpendingComparison compareSpendings(List<Transaction> transactions) {
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDate previousMonthEnd = currentMonthStart.minusDays(1);

        BigDecimal currentMonthSpending = BigDecimal.ZERO;
        BigDecimal previousMonthSpending = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getOccurredAt() == null || !("TRANSFER".equals(t.getType()) || "WITHDRAWAL".equals(t.getType()))) {
                continue;
            }

            LocalDate txnDate = t.getOccurredAt().toLocalDate();

            if (!txnDate.isBefore(currentMonthStart)) {
                currentMonthSpending = currentMonthSpending.add(t.getAmount());
            } else if (!txnDate.isBefore(previousMonthStart) && !txnDate.isAfter(previousMonthEnd)) {
                previousMonthSpending = previousMonthSpending.add(t.getAmount());
            }
        }

        BigDecimal difference = currentMonthSpending.subtract(previousMonthSpending);
        int percentageChange = 0;
        if (previousMonthSpending.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = difference.multiply(BigDecimal.valueOf(100))
                .divide(previousMonthSpending, 0, java.math.RoundingMode.HALF_UP).intValue();
        }

        String trend = percentageChange > 0 ? "UP" : (percentageChange < 0 ? "DOWN" : "STABLE");

        return new SpendingComparison(currentMonthSpending, previousMonthSpending, 
                                      difference, percentageChange, trend);
    }

    /**
     * Get top spending categories
     */
    public List<CategorySpending> getTopSpendingCategories(List<Transaction> transactions, int topN) {
        Map<String, BigDecimal> categoryMap = new HashMap<>();

        for (Transaction t : transactions) {
            if ("TRANSFER".equals(t.getType()) || "WITHDRAWAL".equals(t.getType())) {
                String category = categorizeTransaction(t.getDescription());
                categoryMap.put(category, categoryMap.getOrDefault(category, BigDecimal.ZERO).add(t.getAmount()));
            }
        }

        List<CategorySpending> sortedCategories = new ArrayList<>();
        categoryMap.forEach((cat, amount) -> sortedCategories.add(new CategorySpending(cat, amount)));
        sortedCategories.sort((a, b) -> b.amount.compareTo(a.amount));

        return sortedCategories.subList(0, Math.min(topN, sortedCategories.size()));
    }

    // Inner classes for analytics data
    public static class SpendingInsights {
        public BigDecimal totalSpending;
        public BigDecimal totalIncome;
        public BigDecimal netSavings;
        public int savingsPercentage;
        public BigDecimal avgTransaction;
        public BigDecimal largestTransaction;
        public int transactionCount;
        public String insight;

        public SpendingInsights(BigDecimal totalSpending, BigDecimal totalIncome, BigDecimal savings,
                               int savingsPercentage, BigDecimal avgTransaction, BigDecimal largestTransaction,
                               int transactionCount, String insight) {
            this.totalSpending = totalSpending;
            this.totalIncome = totalIncome;
            this.netSavings = savings;
            this.savingsPercentage = savingsPercentage;
            this.avgTransaction = avgTransaction;
            this.largestTransaction = largestTransaction;
            this.transactionCount = transactionCount;
            this.insight = insight;
        }
    }

    public static class SpendingComparison {
        public BigDecimal currentMonthSpending;
        public BigDecimal previousMonthSpending;
        public BigDecimal difference;
        public int percentageChange;
        public String trend;

        public SpendingComparison(BigDecimal current, BigDecimal previous, BigDecimal diff,
                                 int percentChange, String trend) {
            this.currentMonthSpending = current;
            this.previousMonthSpending = previous;
            this.difference = diff;
            this.percentageChange = percentChange;
            this.trend = trend;
        }
    }

    public static class CategorySpending {
        public String category;
        public BigDecimal amount;

        public CategorySpending(String category, BigDecimal amount) {
            this.category = category;
            this.amount = amount;
        }
    }
}
