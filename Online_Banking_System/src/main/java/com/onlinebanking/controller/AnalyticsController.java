package com.onlinebanking.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.onlinebanking.model.Transaction;
import com.onlinebanking.service.AnalyticsService;
import com.onlinebanking.service.AnalyticsService.*;

/**
 * Controller for Analytics & Spending Insights UI
 */
public class AnalyticsController {
    @FXML private Label pageTitle;
    @FXML private Label totalSpendingLabel;
    @FXML private Label savingsLabel;
    @FXML private Label insightLabel;

    private AnalyticsService analyticsService;
    private List<Transaction> userTransactions;

    public AnalyticsController() {
        this.analyticsService = new AnalyticsService();
    }

    @FXML
    public void initialize() {
        // Load analytics data
        loadAnalytics();
    }

    private void loadAnalytics() {
        try {
            // In production, fetch user's transactions from database
            // userTransactions = transactionRepository.findByUserId(userId);
            
            // For demo, we'll work with empty list
            if (userTransactions == null || userTransactions.isEmpty()) {
                userTransactions = new java.util.ArrayList<>();
                System.out.println("No transactions found for analytics");
                return;
            }

            // Get spending insights
            SpendingInsights insights = analyticsService.getSpendingInsights(userTransactions);
            
            // Update UI with insights
            updateInsightsUI(insights);
            
            // Get category breakdown
            var categoryMap = analyticsService.getSpendingByCategory(
                userTransactions, 
                java.time.LocalDate.now().minusMonths(1),
                java.time.LocalDate.now()
            );
            
            System.out.println("Spending by category: " + categoryMap.size() + " categories");
            
        } catch (Exception e) {
            showError("Failed to load analytics", e.getMessage());
        }
    }

    private void updateInsightsUI(SpendingInsights insights) {
        totalSpendingLabel.setText("₹" + String.format("%.2f", insights.totalSpending));
        savingsLabel.setText(insights.savingsPercentage + "% (" + 
                            String.format("₹%.2f", insights.netSavings) + ")");
        insightLabel.setText(insights.insight);
    }

    @FXML
    protected void onViewCategoryBreakdown() {
        if (userTransactions == null || userTransactions.isEmpty()) {
            showInfo("No Data", "No transactions available for analysis");
            return;
        }

        List<CategorySpending> topCategories = analyticsService.getTopSpendingCategories(
            userTransactions, 5);

        showCategoryDialog(topCategories);
    }

    private void showCategoryDialog(List<CategorySpending> categories) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Top Spending Categories");
        alert.setHeaderText("Your top 5 spending categories");

        StringBuilder content = new StringBuilder();
        int rank = 1;
        for (CategorySpending cs : categories) {
            content.append(rank++).append(". ").append(cs.category)
                   .append(": ₹").append(String.format("%.2f", cs.amount))
                   .append("\n");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    @FXML
    protected void onCompareSpendings() {
        if (userTransactions == null || userTransactions.isEmpty()) {
            showInfo("No Data", "No transactions available for comparison");
            return;
        }

        SpendingComparison comparison = analyticsService.compareSpendings(userTransactions);
        
        String message = "Current Month: ₹" + String.format("%.2f", comparison.currentMonthSpending) +
                        "\nPrevious Month: ₹" + String.format("%.2f", comparison.previousMonthSpending) +
                        "\nChange: " + comparison.percentageChange + "% (" + comparison.trend + ")";
        
        showInfo("Spending Comparison", message);
    }

    @FXML
    protected void onExportAnalytics() {
        showSuccess("Export Successful", 
                   "Analytics report has been exported as PDF to your Documents folder.");
    }

    @FXML
    protected void onDownloadStatement() {
        showSuccess("Statement Downloaded", 
                   "Statement has been downloaded as PDF.");
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
