package com.onlinebanking.controller;

import com.onlinebanking.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    private AuthService authService = new AuthService();

    @FXML
    private TextField usernameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    @FXML
    protected void handleRegister() {
        String username = usernameField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords don't match");
            return;
        }

        if (password.length() < 4) {
            messageLabel.setText("Password must be at least 4 characters");
            return;
        }

        try {
            if (authService.register(username, password, email, name)) {
                messageLabel.setText("Registration successful!");
                goToLogin();
            } else {
                messageLabel.setText("Username already exists");
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void goToLogin() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setScene(scene);
    }
}
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1000);
                    goToLogin("Registration successful. Please log in.");
                } catch (IOException | InterruptedException ignored) {}
            });
        } catch (IllegalArgumentException | IllegalStateException e) {
            messageLabel.setStyle("-fx-text-fill: red");
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    protected void handleBackToLogin(ActionEvent event) throws IOException {
        goToLogin(null);
    }

    private void goToLogin(String infoMessage) throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(param -> ApplicationContext.getInstance().getControllerFactory().apply(param));
        Scene scene = new Scene(loader.load(), 600, 500);
        
        // Add CSS stylesheet
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        stage.setScene(scene);
        stage.setTitle("Premium Banking System - Secure Digital Banking");
        LoginController controller = loader.getController();
        if (infoMessage != null) {
            controller.setInfoMessage(infoMessage);
        }
    }
}
