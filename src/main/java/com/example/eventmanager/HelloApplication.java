package com.example.eventmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // The path must match your resources folder structure exactly
        URL fxmlLocation = getClass().getResource("/fxml/LoginView.fxml");

        if (fxmlLocation == null) {
            System.out.println("Current classpath location search: /fxml/LoginView.fxml");
            throw new RuntimeException("Error: FXML file not found at the specified path!");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(),500,400);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setTitle("Event Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
