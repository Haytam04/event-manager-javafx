package com.example.eventmanager.controller;

import com.example.eventmanager.entity.User;
import com.example.eventmanager.service.UserService;
import com.example.eventmanager.utils.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    private UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            lblMessage.setText("Please fill all fields.");
            return;
        }

        User authenticatedUser = userService.login(user, pass);

        if (authenticatedUser != null) {
            SessionManager.setCurrentUser(authenticatedUser);
            navigateToDashboard(event);
        } else {
            lblMessage.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleSignUp() {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            lblMessage.setText("Fields cannot be empty.");
            return;
        }

        boolean success = userService.register(txtUsername.getText(), txtPassword.getText());
        if (success) {
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Account created! You can now login.");
        } else {
            lblMessage.setText("Username already exists.");
        }
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/fxml/MainDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(dashboard, 1000, 700);

            stage.setScene(scene);
            stage.setTitle("Event Management - Dashboard");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}