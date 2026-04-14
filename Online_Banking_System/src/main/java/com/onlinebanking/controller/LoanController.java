package com.onlinebanking.controller;

import java.math.BigDecimal;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import com.onlinebanking.model.Loan;
import com.onlinebanking.service.LoanService;

/**
 * Controller for Loan Management UI
 */
public class LoanController {
    @FXML private Label pageTitle;
    @FXML private TabPane loanTabPane;

    private LoanService loanService;
    private long currentUserId = 1; // In production, get from session

    public LoanController() {
        this.loanService = new LoanService();
    }

    @FXML
    public void initialize() {
        // Load user's loans when UI initializes
        loadLoans();
    }

    private void loadLoans() {
        try {
            List<Loan> loans = loanService.getLoansByUser(currentUserId);
            System.out.println("Loaded " + loans.size() + " loans");
            // Update UI with loans
        } catch (Exception e) {
            showError("Failed to load loans", e.getMessage());
        }
    }

    @FXML
    protected void onApplyLoan() {
        // Open loan application dialog
        showLoanApplicationDialog();
    }

    private void showLoanApplicationDialog() {
        Dialog<LoanApplicationData> dialog = new Dialog<>();
        dialog.setTitle("Apply for Loan");
        dialog.setHeaderText("Fill in loan details");

        // Create form fields
        ComboBox<String> loanTypeCombo = new ComboBox<>();
        loanTypeCombo.getItems().addAll("PERSONAL", "HOME", "AUTO", "EDUCATION");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter loan amount");
        
        Spinner<Integer> termSpinner = new Spinner<>(6, 360, 60, 6);
        termSpinner.setPrefWidth(100);

        TextField purposeField = new TextField();
        purposeField.setPromptText("Purpose of loan");

        // Add to dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Loan Type:"), 0, 0);
        grid.add(loanTypeCombo, 1, 0);
        grid.add(new Label("Amount (₹):"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Tenure (months):"), 0, 2);
        grid.add(termSpinner, 1, 2);
        grid.add(new Label("Purpose:"), 0, 3);
        grid.add(purposeField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (loanTypeCombo.getValue() != null && !amountField.getText().isEmpty()) {
                try {
                    Loan loan = loanService.applyForLoan(
                        currentUserId,
                        1, // accountId - would come from session
                        loanTypeCombo.getValue(),
                        new BigDecimal(amountField.getText()),
                        BigDecimal.valueOf(9.5), // Default interest rate
                        termSpinner.getValue(),
                        purposeField.getText()
                    );
                    showSuccess("Loan application submitted successfully!",
                               "Loan ID: " + loan.getLoanId() + "\n" +
                               "Monthly EMI: ₹" + loan.getMonthlyEmi());
                    loadLoans();
                } catch (Exception e) {
                    showError("Failed to apply for loan", e.getMessage());
                }
            }
        });
    }

    @FXML
    protected void onPayEmi(long loanId) {
        Loan loan = loanService.getLoan(loanId);
        if (loan != null) {
            // Open payment dialog
            Dialog<BigDecimal> dialog = new Dialog<>();
            dialog.setTitle("Pay EMI");
            dialog.setHeaderText("EMI Payment");

            Label infoLabel = new Label("EMI Amount: ₹" + loan.getMonthlyEmi() + 
                                        "\nRemaining Balance: ₹" + loan.getRemainingBalance());
            
            TextField amountField = new TextField(loan.getMonthlyEmi().toString());
            amountField.setPrefWidth(150);

            VBox content = new VBox(10);
            content.getChildren().addAll(infoLabel, new Label("Amount to pay:"), amountField);
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(result -> {
                try {
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    if (loanService.recordEmiPayment(loanId, amount)) {
                        showSuccess("Payment Successful", "EMI paid successfully!");
                        loadLoans();
                    } else {
                        showError("Failed to pay EMI", "Please try again");
                    }
                } catch (Exception e) {
                    showError("Invalid amount", e.getMessage());
                }
            });
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

    // Inner class for loan application data
    private static class LoanApplicationData {
        String loanType;
        BigDecimal amount;
        int term;
        String purpose;
    }
}
