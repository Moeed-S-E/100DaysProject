package application.ui;

import application.constants.UIConstants;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIComponentFactory {
    
    public static Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, UIConstants.SECTION_FONT_SIZE));
        title.setTextFill(Color.web(UIConstants.DARK_GREEN));
        return title;
    }

    public static TextField createModernTextField(String prompt, String icon) {
        TextField field = new TextField();
        field.setPromptText(icon + " " + prompt);
        field.setStyle(String.format(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: %s;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);",
            UIConstants.MEDIUM_GREEN));
        return field;
    }

    public static Button createModernButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, UIConstants.NORMAL_FONT_SIZE));
        button.setTextFill(Color.WHITE);
        button.setPrefHeight(45);
        
        button.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, derive(%s, -15%%));" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);",
            color, color));

        addButtonHoverEffects(button, color);
        return button;
    }
    
    private static void addButtonHoverEffects(Button button, String color) {
        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom, derive(%s, 10%%), derive(%s, -5%%));" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 6);",
            color, color)));

        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, derive(%s, -15%%));" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);",
            color, color)));
    }
}
