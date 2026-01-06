package com.example.eventmanager.controller;

import com.example.eventmanager.dao.EventDAO;
import com.example.eventmanager.entity.Event;
import com.example.eventmanager.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DashboardController {
    @FXML private FlowPane eventFlowPane;
    @FXML private Label lblWelcome;

    private EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        lblWelcome.setText("Welcome, " + SessionManager.getCurrentUser().getUsername());
        refreshEvents();
    }

    public void refreshEvents() {
        eventFlowPane.getChildren().clear();
        List<Event> events = eventDAO.findByUser(SessionManager.getCurrentUser());

        for (Event event : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventCard.fxml"));
                VBox card = loader.load();

                // Get the card's controller to set data
                EventCardController controller = loader.getController();
                controller.setData(event, this); // Pass 'this' to allow refresh after delete

                eventFlowPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        // 1. Create a confirmation alert [cite: 5, 13]
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Log Out of EventHub");
        alert.setContentText("Are you sure you want to log out?");

        // 2. Show the dialog and capture the user's choice [cite: 5, 13]
        Optional<ButtonType> result = alert.showAndWait();

        // 3. Only proceed if the user clicks OK [cite: 5, 13]
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SessionManager.logout(); // Clear the current session
        }

        SessionManager.logout();
        Parent loginView = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loginView));
        stage.show();
    }

    @FXML
    private void handleAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventForm.fxml"));
            Parent root = loader.load();

            // Optional: Get controller if you wanted to initialize it as "Add Mode"
            EventFormController controller = loader.getController();
            controller.setEvent(null); // Explicitly null for a new event

            Stage stage = new Stage();
            stage.setTitle("Add New Event");
            stage.setScene(new Scene(root));

            // This makes the dashboard wait until the form is closed
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the cards after the window is closed to show the new event
            refreshEvents();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageCategories(ActionEvent event) throws IOException {
        Parent categoryView = FXMLLoader.load(getClass().getResource("/fxml/CategoryManagement.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(categoryView));
    }
}