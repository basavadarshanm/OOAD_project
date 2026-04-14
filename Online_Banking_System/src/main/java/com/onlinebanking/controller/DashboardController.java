package com.onlinebanking.controller;

import java.util.List;

import com.onlinebanking.model.Account;
import com.onlinebanking.model.Transaction;
import com.onlinebanking.model.User;
import com.onlinebanking.service.AccountService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DashboardController {
    private AccountService accountService = new AccountService();
    private User currentUser;
    private Account selectedAccount;

    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<Account> accountComboBox;
    @FXML
    private Label balanceLabel;
    @FXML
    private ListView<Transaction> transactionsList;
    @FXML
    private TextField amountField;
    @FXML
    private Label messageLabel;

    public DashboardController(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        try {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
            loadAccounts();
        } catch (Exception e) {
            messageLabel.setText("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadAccounts() throws Exception {
        List<Account> accounts = accountService.getUserAccounts(currentUser.getId());
        accountComboBox.setItems(FXCollections.observableArrayList(accounts));
        
        if (!accounts.isEmpty()) {
            accountComboBox.getSelectionModel().select(0);
            onAccountSelected();
        }
    }

    @FXML
    private void onAccountSelected() {
        selectedAccount = accountComboBox.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
            loadTransactions();
        }
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactions = accountService.getTransactions(selectedAccount.getId());
            transactionsList.setItems(FXCollections.observableArrayList(transactions));
        } catch (Exception e) {
            messageLabel.setText("Error loading transactions: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            accountService.deposit(selectedAccount.getId(), amount);
            selectedAccount.deposit(amount);
            balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
            amountField.clear();
            loadTransactions();
            messageLabel.setText("Deposit successful");
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (accountService.withdraw(selectedAccount.getId(), amount)) {
                selectedAccount.withdraw(amount);
                balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
                amountField.clear();
                loadTransactions();
                messageLabel.setText("Withdrawal successful");
            } else {
                messageLabel.setText("Insufficient balance");
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() throws Exception {
        // Navigate back to login
    }
}
    @FXML
    private TextField transferDescriptionField;

    @FXML
    private TextField billerField;
    @FXML
    private TextField billAmountField;

    @FXML
    private TextField beneficiaryNameField;
    @FXML
    private TextField beneficiaryAccountField;
    @FXML
    private TextField beneficiaryBankField;

    public DashboardController(AccountService accountService, TransferService transferService,
                               BeneficiaryService beneficiaryService, BillPayService billPayService) {
        this.accountService = accountService;
        this.transferService = transferService;
        this.beneficiaryService = beneficiaryService;
        this.billPayService = billPayService;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        loadAccounts();
    }

    @FXML
    protected void handleRefresh() {
        loadAccounts();
        showSuccess("Refreshed successfully");
    }

    @FXML
    protected void handleLogout() throws IOException {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(param -> ApplicationContext.getInstance().getControllerFactory().apply(param));
        Scene scene = new Scene(loader.load(), 480, 320);
        stage.setScene(scene);
        stage.setTitle("Online Banking");
    }

    /**
     * Transfer money from selected account to target account number
     */
    @FXML
    protected void handleTransfer() {
        if (selectedAccount == null) {
            showError("Select an account first");
            return;
        }
        
        String toAccountNumber = transferToField.getText();
        if (toAccountNumber == null || toAccountNumber.isBlank()) {
            showError("Enter target account number");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(transferAmountField.getText());
            String description = transferDescriptionField.getText();
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero");
                return;
            }

            // Find target account
            Optional<Account> targetOpt = transferService.findAccountByNumber(toAccountNumber);
            if (targetOpt.isEmpty()) {
                showError("Target account not found");
                return;
            }

            Account targetAccount = targetOpt.get();
            accountService.transferMoney(selectedAccount.getId(), targetAccount.getId(), amount, description);
            
            showSuccess("Transfer of $" + amount + " successful!");
            loadAccounts();
            transferAmountField.clear();
            transferToField.clear();
            transferDescriptionField.clear();
        } catch (NumberFormatException e) {
            showError("Invalid amount. Enter a valid number.");
        } catch (Account.InsufficientBalanceException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Transfer failed: " + e.getMessage());
        }
    }

    /**
     * Pay bill using selected account
     */
    @FXML
    protected void handleBillPay() {
        if (selectedAccount == null) {
            showError("Select an account first");
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(billAmountField.getText());
            String biller = billerField.getText();
            
            if (biller == null || biller.isBlank()) {
                showError("Enter biller name");
                return;
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero");
                return;
            }

            accountService.withdrawMoney(selectedAccount.getId(), amount, "Bill Payment: " + biller);
            showSuccess("Bill payment of $" + amount + " to " + biller + " successful!");
            loadAccounts();
            billAmountField.clear();
            billerField.clear();
        } catch (NumberFormatException e) {
            showError("Invalid amount");
        } catch (Account.InsufficientBalanceException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Bill pay failed: " + e.getMessage());
        }
    }

    /**
     * Add beneficiary for faster future transfers
     */
    @FXML
    protected void handleAddBeneficiary() {
        try {
            String name = beneficiaryNameField.getText();
            String account = beneficiaryAccountField.getText();
            String bank = beneficiaryBankField.getText();

            if (name == null || name.isBlank() || account == null || account.isBlank() || bank == null || bank.isBlank()) {
                showError("All beneficiary fields are required");
                return;
            }

            Beneficiary beneficiary = new Beneficiary(0, currentUser.getId(), name, account, bank);
            beneficiaryService.add(beneficiary);
            showSuccess("Beneficiary added successfully");
            beneficiaryNameField.clear();
            beneficiaryAccountField.clear();
            beneficiaryBankField.clear();
        } catch (Exception e) {
            showError("Add beneficiary failed: " + e.getMessage());
        }
    }

    private void loadAccounts() {
        List<Account> accounts = accountService.getAccounts(currentUser.getId());
        
        // Setup ComboBox with accounts
        accountComboBox.setItems(FXCollections.observableArrayList(accounts));
        
        // Custom cell factory to display account info in dropdown
        accountComboBox.setCellFactory(param -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - " + account.getAccountType() + 
                           " ($" + account.getBalance() + ")");
                }
            }
        });
        
        if (!accounts.isEmpty()) {
            // Set the display for selected item
            accountComboBox.setButtonCell(new ListCell<Account>() {
                @Override
                protected void updateItem(Account account, boolean empty) {
                    super.updateItem(account, empty);
                    if (empty || account == null) {
                        setText(null);
                    } else {
                        setText(account.getAccountNumber() + " - " + account.getAccountType());
                    }
                }
            });
            
            accountComboBox.getSelectionModel().selectFirst();
            handleAccountSelection();
        }
    }

    @FXML
    protected void handleAccountSelection() {
        Account selected = accountComboBox.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedAccount = selected;
            updateAccountDetails();
            loadTransactions(selected.getId());
            updateBalanceLabel();
        }
    }

    private void updateAccountDetails() {
        if (selectedAccount != null) {
            accountNumberLabel.setText(selectedAccount.getAccountNumber());
            accountTypeLabel.setText(selectedAccount.getAccountType());
            accountBalanceLabel.setText("$" + String.format("%.2f", selectedAccount.getBalance()));
            accountStatusLabel.setText(selectedAccount.getStatus());
        }
    }

    private void loadTransactions(long accountId) {
        List<Transaction> txs = accountService.getRecentTransactions(accountId);
        transactionsList.setItems(FXCollections.observableArrayList(txs));
        transactionsList.refresh();
    }

    private void updateBalanceLabel() {
        if (selectedAccount != null) {
            balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c");
        statusLabel.setText("❌ " + message);
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #27ae60");
        statusLabel.setText("✓ " + message);
    }
}
