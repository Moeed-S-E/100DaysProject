package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class WelcomeScreen {
    private VBox root;

    public WelcomeScreen(Runnable onStart) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #333333;");

        // Title
        Label titleLabel = new Label("Welcome to Image Filter App");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: #F4F4F4;");

        // Description
        Label descLabel = new Label("Load and transform your images with filters like grayscale, hot, cold, and more!");
        descLabel.setFont(new Font("Arial", 16));
        descLabel.setStyle("-fx-text-fill: #F4F4F4;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(600);

        // Start button
        Button startButton = new Button("Start");
        startButton.setFont(new Font("Arial", 18));
        startButton.setPrefWidth(100);
        startButton.setPrefHeight(40);
        startButton.setStyle("-fx-text-fill: #F4F4F4; -fx-background-color: #555555;"); 
        startButton.setOnAction(e -> onStart.run());

        // Add components to layout
        root.getChildren().addAll(titleLabel, descLabel, startButton);
    }

    public VBox getRoot() {
        return root;
    }
}