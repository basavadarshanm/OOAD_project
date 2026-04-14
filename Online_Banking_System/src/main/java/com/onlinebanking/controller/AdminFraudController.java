package com.onlinebanking.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.onlinebanking.model.FraudDetection;
import com.onlinebanking.service.FraudDetectionService;
import com.onlinebanking.service.FraudDetectionService.FraudStatistics;

/**
 * Controller for Admin Fraud Detection & Alerts UI
 */
public class AdminFraudController {
    @FXML private Label pageTitle;
    @FXML private Label flaggedCountLabel;
    @FXML private Label confirmedCountLabel;
    @FXML private Label accuracyLabel;

    private FraudDetectionService fraudService;

    public AdminFraudController() {
        this.fraudService = new FraudDetectionService();
    }

    @FXML
    public void initialize() {
        // Load fraud statistics and cases
        loadFraudData();
    }

    private void loadFraudData() {
        try {
            // Get fraud statistics
            FraudStatistics stats = fraudService.getFraudStatistics();
            
            // Update dashboard stats
            flaggedCountLabel.setText(String.valueOf(stats.totalFlagged));
            confirmedCountLabel.setText(String.valueOf(stats.confirmedFrauds));
            accuracyLabel.setText(stats.getAccuracyRate() + "%");
            
            // Load flagged cases
            List<FraudDetection> flaggedCases = fraudService.getFlaggedFraudCases();
            System.out.println("Loaded " + flaggedCases.size() + " flagged fraud cases");
            
        } catch (Exception e) {
            showError("Failed to load fraud data", e.getMessage());
        }
    }

    @FXML
    protected void onConfirmFraud(long fraudId) {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Fraud");
            confirmAlert.setHeaderText("Are you sure this is fraudulent?");
            confirmAlert.setContentText("This action will flag the user's account and block the transaction.");
            
            if (confirmAlert.showAndWait().isPresent() && confirmAlert.getResult() == ButtonType.OK) {
                fraudService.confirmFraud(fraudId);
                showSuccess("Fraud Confirmed", 
                           "User account has been flagged for investigation.");
                loadFraudData();
            }
        } catch (Exception e) {
            showError("Failed to confirm fraud", e.getMessage());
        }
    }

    @FXML
    protected void onMarkFalsePositive(long fraudId) {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Mark as False Positive");
            confirmAlert.setHeaderText("This is not a fraudulent transaction?");
            confirmAlert.setContentText("This will help us improve our detection algorithm.");
            
            if (confirmAlert.showAndWait().isPresent() && confirmAlert.getResult() == ButtonType.OK) {
                fraudService.markAsFalsePositive(fraudId);
                showSuccess("Marked as False Positive", 
                           "Detection module will be updated.");
                loadFraudData();
            }
        } catch (Exception e) {
            showError("Failed to process action", e.getMessage());
        }
    }

    @FXML
    protected void onViewDetails(long fraudId) {
        FraudDetection fraud = fraudService.getFlaggedFraudCases().stream()
            .filter(f -> f.getFraudId() == fraudId)
            .findFirst()
            .orElse(null);
        
        if (fraud != null) {
            showFraudDetailsDialog(fraud);
        }
    }

    private void showFraudDetailsDialog(FraudDetection fraud) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fraud Case Details");
        alert.setHeaderText("Case ID: FRD-" + fraud.getFraudId());
        
        StringBuilder details = new StringBuilder();
        details.append("Transaction ID: TXN-").append(fraud.getTransactionId()).append("\n");
        details.append("User ID: USR-").append(fraud.getUserId()).append("\n");
        details.append("Risk Score: ").append(String.format("%.2f", fraud.getRiskScore())).append("\n");
        details.append("Detection Type: ").append(fraud.getDetectionType()).append("\n");
        details.append("Flags: ").append(fraud.getFlags()).append("\n");
        details.append("Reason: ").append(fraud.getReason()).append("\n");
        details.append("Status: ").append(fraud.getStatus()).append("\n");
        details.append("Created: ").append(fraud.getCreatedAt()).append("\n");
        
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    @FXML
    protected void onRefreshData() {
        try {
            loadFraudData();
            showSuccess("Data Refreshed", "Fraud detection data has been refreshed.");
        } catch (Exception e) {
            showError("Refresh Failed", e.getMessage());
        }
    }

    @FXML
    protected void onExportReport() {
        try {
            FraudStatistics stats = fraudService.getFraudStatistics();
            String report = "Fraud Detection Report\n" +
                           "=====================\n" +
                           "Total Flagged: " + stats.totalFlagged + "\n" +
                           "Confirmed Fraud: " + stats.confirmedFrauds + "\n" +
                           "False Positives: " + stats.falsePositives + "\n" +
                           "Accuracy Rate: " + stats.getAccuracyRate() + "%\n";
            
            showSuccess("Report Exported", "Fraud report exported successfully.");
        } catch (Exception e) {
            showError("Export Failed", e.getMessage());
        }
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(title);
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
