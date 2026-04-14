package com.onlinebanking.controller;

import com.onlinebanking.model.User;
import com.onlinebanking.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private AuthService authService = new AuthService();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }
        
        try {
            User user = authService.login(username, password);
            if (user != null) {
                openDashboard(user);
            } else {
                errorLabel.setText("Invalid credentials");
            }
        } catch (Exception e) {
            errorLabel.setText("Login error: " + e.getMessage());
        }
    }

    @FXML
    protected void handleRegister() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setScene(scene);
    }

    private void openDashboard(User user) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        DashboardController controller = new DashboardController(user);
        loader.setController(controller);
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setScene(scene);
    }
}
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        stage.setScene(scene);
        stage.setTitle("Premium Banking - Create Account");
    }

    private void navigateToDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        loader.setControllerFactory(param -> ApplicationContext.getInstance().getControllerFactory().apply(param));
        Scene scene = new Scene(loader.load(), 1200, 700);
        
        // Add CSS stylesheet
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        DashboardController controller = loader.getController();
        controller.setCurrentUser(user);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Premium Banking - Dashboard");
    }

    public void setInfoMessage(String message) {
        clearMessages();
        infoLabel.setText(message);
    }

    private void clearMessages() {
        errorLabel.setText("");
        infoLabel.setText("");
    }
}
