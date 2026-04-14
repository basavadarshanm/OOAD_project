package com.onlinebanking.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.onlinebanking.model.BillPayment;
import com.onlinebanking.service.BillPaymentService;
import com.onlinebanking.service.BillPaymentService.BillerInfo;

/**
 * Controller for Bill Payment Management UI
 */
public class BillPaymentController {
    @FXML private Label pageTitle;
    @FXML private TabPane billTabPane;

    private BillPaymentService billService;
    private long currentAccountId = 1; // In production, get from session

    public BillPaymentController() {
        this.billService = new BillPaymentService();
    }

    @FXML
    public void initialize() {
        // Load user's bills when UI initializes
        loadBills();
    }

    private void loadBills() {
        try {
            List<BillPayment> pendingBills = billService.getPendingBills(currentAccountId);
            List<BillPayment> overdueBills = billService.getOverdueBills(currentAccountId);
            System.out.println("Loaded " + pendingBills.size() + " pending bills");
            System.out.println("Loaded " + overdueBills.size() + " overdue bills");
            // Update UI with bills
        } catch (Exception e) {
            showError("Failed to load bills", e.getMessage());
        }
    }

    @FXML
    protected void onAddBill() {
        // Open bill addition dialog
        showAddBillDialog();
    }

    private void showAddBillDialog() {
        Dialog<BillPaymentData> dialog = new Dialog<>();
        dialog.setTitle("Add Bill Payment");
        dialog.setHeaderText("Add a new bill to track");

        // Create form fields
        ComboBox<BillerInfo> billerCombo = new ComboBox<>();
        List<BillerInfo> billers = billService.getAvailableBillers();
        billerCombo.getItems().addAll(billers);
        billerCombo.setPrefWidth(200);

        TextField consumerNumberField = new TextField();
        consumerNumberField.setPromptText("Consumer/Account Number");
        
        TextField billAmountField = new TextField();
        billAmountField.setPromptText("Bill Amount");
        
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        // Add to dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Biller:"), 0, 0);
        grid.add(billerCombo, 1, 0);
        grid.add(new Label("Consumer Number:"), 0, 1);
        grid.add(consumerNumberField, 1, 1);
        grid.add(new Label("Bill Amount (₹):"), 0, 2);
        grid.add(billAmountField, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (billerCombo.getValue() != null && !billAmountField.getText().isEmpty()) {
                try {
                    BillerInfo biller = billerCombo.getValue();
                    BillPayment bill = billService.addBill(
                        currentAccountId,
                        biller.billerName,
                        biller.category,
                        consumerNumberField.getText(),
                        new BigDecimal(billAmountField.getText()),
                        dueDatePicker.getValue()
                    );
                    showSuccess("Bill added successfully!");
                    loadBills();
                } catch (Exception e) {
                    showError("Failed to add bill", e.getMessage());
                }
            }
        });
    }

    @FXML
    protected void onPayBill(long billId) {
        BillPayment bill = billService.getBill(billId);
        if (bill != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Pay Bill");
            alert.setHeaderText("Confirm Bill Payment");
            alert.setContentText("Bill Amount: ₹" + bill.getBillAmount() + 
                                 "\nBiller: " + bill.getBillerName() + 
                                 "\n\nProceed with payment?");
            
            if (alert.showAndWait().isPresent() && alert.getResult() == ButtonType.OK) {
                try {
                    String referenceNumber = "REF-" + System.currentTimeMillis();
                    billService.payBill(billId, referenceNumber);
                    showSuccess("Bill paid successfully!",
                               "Reference Number: " + referenceNumber);
                    loadBills();
                } catch (Exception e) {
                    showError("Payment failed", e.getMessage());
                }
            }
        }
    }

    @FXML
    protected void onSetReminder(long billId) {
        BillPayment bill = billService.getBill(billId);
        if (bill != null) {
            showSuccess("Reminder Set", 
                       "You will be reminded about this bill 2 days before due date.");
        }
    }

    private void showSuccess(String message) {
        showSuccess("Success", message);
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

    // Inner class for bill payment data
    private static class BillPaymentData {
        String billerName;
        String category;
        String consumerNumber;
        BigDecimal amount;
        LocalDate dueDate;
    }
}
