package com.example.eventmanager.controller;

import com.example.eventmanager.entity.Event;
import com.example.eventmanager.service.EventService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class EventCardController {
    @FXML private ImageView imgEvent;
    @FXML private Label lblTitle, lblLocation, lblParticipants, lbCategory;

    private Event event;
    private EventService eventService = new EventService();
    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        // Add double-click listener to the entire card
        imgEvent.getParent().setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) { // Double-click
                openParticipantPage();
            }
        });
    }

    public void setData(Event event, DashboardController dashboardController) {
        this.event = event;
        this.dashboardController = dashboardController;

        lblTitle.setText(event.getTitle());
        lblLocation.setText(event.getLocation());
        lbCategory.setText(event.getCategory().toString());
        lblParticipants.setText("Max: " + event.getMaxParticipants());

        try {
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                imgEvent.setImage(new Image(event.getImageUrl(), true));
            }
        } catch (Exception e) {
            // Fallback image if URL is broken
        }
    }

    @FXML
    private void onDelete() {
        // Create the confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("Are you sure you want to delete this event?");
        alert.setContentText("Event: " + event.getTitle() + "\nThis action cannot be undone.");

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // Check if the user clicked "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eventService.deleteEvent(event);
            dashboardController.refreshEvents(); // Trigger dashboard UI update
        }
    }

    @FXML
    private void onEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventForm.fxml"));
            Parent root = loader.load();

            EventFormController controller = loader.getController();
            controller.setEvent(this.event); // Pass the current event to the form

            Stage stage = new Stage();
            stage.setTitle("Edit Event: " + event.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the list after editing
            dashboardController.refreshEvents();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openParticipantPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ParticipantView.fxml"));
            Parent root = loader.load();

            ParticipantController controller = loader.getController();
            controller.setSelectedEvent(this.event); // Pass the event to participant controller

            Stage stage = new Stage();
            stage.setTitle("Participants - " + event.getTitle());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Cannot open participant page: " + e.getMessage());
            alert.show();
        }
    }
}