package com.example.eventmanager.controller;

import com.example.eventmanager.entity.Category;
import com.example.eventmanager.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class CategoryController {
    @FXML private TextField txtCategoryName;
    @FXML private ListView<Category> listCategories;

    private CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() {
        refreshList();

        // Add a listener to handle selection changes
        listCategories.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // When a category is clicked, put its name into the TextField
                txtCategoryName.setText(newValue.getName());
            } else {
                // Optional: Clear the TextField if selection is cleared
                txtCategoryName.clear();
            }
        });
    }

    private void refreshList() {
        listCategories.setItems(FXCollections.observableArrayList(categoryService.getAllCategories()));
    }

    @FXML
    private void handleAdd() {
        String name = txtCategoryName.getText();
        if (name != null && !name.isEmpty()) {
            categoryService.saveCategory(name);
            txtCategoryName.clear();
            refreshList();
        }
    }

    @FXML
    private void handleUpdate() {
        Category selected = listCategories.getSelectionModel().getSelectedItem();
        String newName = txtCategoryName.getText();
        if (selected != null && newName != null && !newName.isEmpty()) {
            categoryService.updateCategory(selected, newName);
            txtCategoryName.clear();
            refreshList();
        }
    }

    @FXML
    private void handleDelete() {
        Category selected = listCategories.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    categoryService.deleteCategory(selected);
                    refreshList();
                }
            });
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Parent dashboard = FXMLLoader.load(getClass().getResource("/fxml/MainDashboard.fxml"));
        Stage stage = (Stage) txtCategoryName.getScene().getWindow();
        stage.setScene(new Scene(dashboard));
    }
}