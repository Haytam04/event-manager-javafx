package com.example.eventmanager.controller;

import com.example.eventmanager.entity.Category;
import com.example.eventmanager.entity.Event;
import com.example.eventmanager.service.CategoryService;
import com.example.eventmanager.service.EventService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;


public class EventFormController {
    @FXML private TextField txtTitle, txtLocation, txtImageUrl;
    @FXML private Spinner<Integer> spinMaxParticipants;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Category> comboCategory;

    private EventService eventService = new EventService();
    private CategoryService categoryService = new CategoryService();
    private Event currentEvent; // Null if adding new, not null if editing

    @FXML
    public void initialize() {
        // Initialize spinner with 1 to 1000 range
        spinMaxParticipants.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10));
        loadCategories();
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        comboCategory.setItems(FXCollections.observableArrayList(categories));
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        if (event != null) {
            txtTitle.setText(event.getTitle());
            txtLocation.setText(event.getLocation());
            txtImageUrl.setText(event.getImageUrl());
            spinMaxParticipants.getValueFactory().setValue(event.getMaxParticipants());
            datePicker.setValue(event.getDate().toLocalDate());

            if (event.getCategory() != null) {
                comboCategory.setValue(event.getCategory());
            }
        }
    }

    @FXML
    private void handleSelectImage() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Event Image");

        // Filters to show only images
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        java.io.File selectedFile = fileChooser.showOpenDialog(txtTitle.getScene().getWindow());

        if (selectedFile != null) {
            // This converts the local path (C:\...) to a format JavaFX Image can read (file:/C:/...)
            txtImageUrl.setText(selectedFile.toURI().toString());
        }
    }

    @FXML
    private void handleSave() {
        if (currentEvent == null) currentEvent = new Event();

        // VALIDATION: Ensure a category is selected
        if (comboCategory.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a category!");
            alert.show();
            return;
        }

        currentEvent.setTitle(txtTitle.getText());
        currentEvent.setLocation(txtLocation.getText());
        currentEvent.setImageUrl(txtImageUrl.getText());
        currentEvent.setMaxParticipants(spinMaxParticipants.getValue());
        currentEvent.setDate(datePicker.getValue().atStartOfDay());
        currentEvent.setCategory(comboCategory.getValue());

        eventService.saveEvent(currentEvent);
        closeWindow();
    }

    @FXML private void handleCancel() { closeWindow(); }
    private void closeWindow() { ((Stage) txtTitle.getScene().getWindow()).close(); }

}