package com.onlinebanking.controller;

import com.onlinebanking.service.CardService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

/**
 * Controller for Card Management UI
 */
public class CardController {
    @FXML private Label pageTitle;
    @FXML private Label cardStatusLabel;

    private CardService cardService;

    public CardController() {
        this.cardService = new CardService();
    }

    @FXML
    public void initialize() {
        // Load user's cards when UI initializes
        loadCards();
    }

    private void loadCards() {
        try {
            // In production, this would fetch from database based on logged-in user
            // List<Card> cards = cardService.getCardsByAccount(accountId);
            System.out.println("Cards loaded successfully");
        } catch (Exception e) {
            showError("Failed to load cards", e.getMessage());
        }
    }

    @FXML
    protected void onAddCard() {
        // Open dialog to add new card
        showInfo("Add Card", "Card request will be processed within 3-5 business days");
    }

    @FXML
    protected void onBlockCard(long cardId) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Block Card");
            alert.setHeaderText("Are you sure you want to block this card?");
            alert.setContentText("You can unblock it later from the card details screen.");
            
            if (alert.showAndWait().isPresent() && alert.getResult() == ButtonType.OK) {
                cardService.blockCard(cardId, "User requested");
                showSuccess("Card blocked successfully");
                loadCards();
            }
        } catch (Exception e) {
            showError("Failed to block card", e.getMessage());
        }
    }

    @FXML
    protected void onUnblockCard(long cardId) {
        try {
            cardService.unblockCard(cardId);
            showSuccess("Card unblocked successfully");
            loadCards();
        } catch (Exception e) {
            showError("Failed to unblock card", e.getMessage());
        }
    }

    @FXML
    protected void onSetLimit(long cardId) {
        // Open dialog to set spending limits
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Card Limit");
        // Dialog content implementation...
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
