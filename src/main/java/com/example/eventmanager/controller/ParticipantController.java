package com.example.eventmanager.controller;

import com.example.eventmanager.entity.Event;
import com.example.eventmanager.entity.Participant;
import com.example.eventmanager.service.ParticipantService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ParticipantController {

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private ListView<Participant> participantListView;

    @FXML
    private Label maxParticipantsLabel; // Add this in FXML

    @FXML
    private Button addUpdateButton; // Add this in FXML

    private Event selectedEvent;
    private final ParticipantService participantService = new ParticipantService();
    private Participant selectedParticipant;



    public void setSelectedEvent(Event event) {
        this.selectedEvent = event;
        updateUI();
    }

    private void updateUI() {
        if (selectedEvent != null) {
            refreshParticipantList();
            updateMaxParticipantsInfo();
        }
    }

    private void updateMaxParticipantsInfo() {
        if (selectedEvent != null) {
            int currentCount = participantService.getParticipantsByEvent(selectedEvent.getId()).size();
            int maxParticipants = selectedEvent.getMaxParticipants();

            // Update label
            if (maxParticipantsLabel != null) {
                maxParticipantsLabel.setText(currentCount + "/" + maxParticipants + " participants");

                // Change color if limit reached
                if (currentCount >= maxParticipants) {
                    maxParticipantsLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                } else {
                    maxParticipantsLabel.setStyle("-fx-text-fill: #4CAF50;");
                }
            }

            // Disable add button if limit reached
            if (addUpdateButton != null && selectedParticipant == null) {
                if (currentCount >= maxParticipants) {
                    addUpdateButton.setDisable(true);
                    addUpdateButton.setText("Event Full");
                    addUpdateButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white;");
                } else {
                    addUpdateButton.setDisable(false);
                    addUpdateButton.setText("Add Participant");
                    addUpdateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                }
            }
        }
    }

    @FXML
    public void initialize() {
        // Set up participant list view
        participantListView.setCellFactory(lv -> new ParticipantListCell());

        // Handle participant selection
        participantListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedParticipant = newVal;
            if (newVal != null) {
                // Fill form with selected participant's data for editing
                name.setText(newVal.getFullName());
                email.setText(newVal.getEmail());

                // Enable edit mode
                if (addUpdateButton != null) {
                    addUpdateButton.setText("Update Participant");
                    addUpdateButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
                    addUpdateButton.setDisable(false); // Always enable for editing
                }
            } else {
                // Reset to add mode
                if (addUpdateButton != null) {
                    addUpdateButton.setText("Add Participant");
                    addUpdateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    updateMaxParticipantsInfo(); // Check if can add
                }
            }
        });
    }

    @FXML
    private void handleAddParticipant() {
        if (selectedEvent == null) {
            showError("No Event Selected", "Please select an event first");
            return;
        }

        String participantName = name.getText().trim();
        String participantEmail = email.getText().trim();

        if (participantName.isEmpty() || participantEmail.isEmpty()) {
            showError("Invalid Input", "Name and email are required");
            return;
        }

        // Check if we're adding new
        if (selectedParticipant == null) {
            // CHECK MAX PARTICIPANTS BEFORE ADDING
            int currentCount = participantService.getParticipantsByEvent(selectedEvent.getId()).size();
            int maxParticipants = selectedEvent.getMaxParticipants();

            if (currentCount >= maxParticipants) {
                showError("Event Full",
                        "Cannot add more participants. Event has reached its maximum capacity of " +
                                maxParticipants + " participants.\n" +
                                "Current participants: " + currentCount);
                return;
            }
        }

        // Check if we're adding new or updating existing
        if (selectedParticipant != null) {
            // Update existing participant
            boolean updated = updateParticipant(selectedParticipant.getId(), participantName, participantEmail);
            if (updated) {
                showSuccess("Participant updated successfully!");
                selectedParticipant = null; // Reset selection
            } else {
                showError("Error", "Failed to update participant");
            }
        } else {
            // Add new participant
            boolean added = participantService.add(participantName, participantEmail, selectedEvent.getId());

            if (added) {
                showSuccess("Participant added successfully!");
            } else {
                showError("Error", "Failed to add participant. Participant may already be registered.");
            }
        }

        clearForm();
        refreshParticipantList();
        updateMaxParticipantsInfo();
    }

    @FXML
    private void handleDeleteParticipant() {
        if (selectedParticipant == null) {
            showError("No Selection", "Please select a participant to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Participant");
        confirmAlert.setContentText("Are you sure you want to delete participant: " +
                selectedParticipant.getFullName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = participantService.delete(selectedParticipant.getId());
            if (deleted) {
                showSuccess("Participant deleted successfully!");
                selectedParticipant = null;
                clearForm();
                refreshParticipantList();
                updateMaxParticipantsInfo();
            } else {
                showError("Error", "Failed to delete participant");
            }
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
        participantListView.getSelectionModel().clearSelection();
        selectedParticipant = null;
        updateMaxParticipantsInfo();
    }

    private boolean updateParticipant(Long participantId, String newName, String newEmail) {
        // Get the participant
        Participant participant = participantService.getParticipantById(participantId);
        if (participant == null) {
            return false;
        }

        // Update fields
        participant.setFullName(newName);
        participant.setEmail(newEmail);

        // Save changes
        return participantService.update(participant);
    }

    private void clearForm() {
        name.clear();
        email.clear();
    }

    private void refreshParticipantList() {
        participantListView.getItems().clear();

        if (selectedEvent != null) {
            var participants = participantService.getParticipantsByEvent(selectedEvent.getId());
            participantListView.getItems().addAll(participants);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private class ParticipantListCell extends javafx.scene.control.ListCell<Participant> {
        private final HBox hbox = new HBox(10);
        private final Label nameLabel = new Label();
        private final Label emailLabel = new Label();
        private final Button editButton = new Button("Edit");
        private final Button deleteButton = new Button("Delete");

        public ParticipantListCell() {
            super();

            // Configure HBox
            hbox.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

            // Set up labels
            nameLabel.setStyle("-fx-font-weight: bold;");
            emailLabel.setStyle("-fx-text-fill: gray;");

            // Make labels expand
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            HBox.setHgrow(emailLabel, Priority.ALWAYS);

            // Configure buttons
            editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            editButton.setOnAction(e -> {
                Participant participant = getItem();
                if (participant != null) {
                    // Select this participant for editing
                    participantListView.getSelectionModel().select(participant);
                }
            });

            deleteButton.setOnAction(e -> {
                Participant participant = getItem();
                if (participant != null) {
                    selectedParticipant = participant;
                    handleDeleteParticipant();
                }
            });

            hbox.getChildren().addAll(nameLabel, emailLabel, editButton, deleteButton);
        }

        @Override
        protected void updateItem(Participant participant, boolean empty) {
            super.updateItem(participant, empty);

            if (empty || participant == null) {
                setText(null);
                setGraphic(null);
            } else {
                nameLabel.setText(participant.getFullName());
                emailLabel.setText(participant.getEmail());
                setGraphic(hbox);
                setText(null);
            }
        }

    }
}