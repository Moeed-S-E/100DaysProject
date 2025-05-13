import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class MainApp extends Application {

    private int targetNumber; 
    private int attempts; 

    @Override
    public void start(Stage primaryStage) {

        Random random = new Random();
        targetNumber = random.nextInt(100) + 1;
        attempts = 0;

        
        Label titleLabel = new Label("Guess the Number (1 to 100)");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;-fx-text-fill: #FFF;");

        Label feedbackLabel = new Label("Enter your guess below:");
        feedbackLabel.setStyle("-fx-font-size: 14px;-fx-text-fill: #FFF;");

        TextField inputField = new TextField();
        inputField.setPromptText("Enter your guess");
        inputField.setMaxWidth(200);

        Button guessButton = new Button("Guess");
        Button resetButton = new Button("Reset");
        guessButton.setPrefWidth(80);
        guessButton.setPrefHeight(35);
        resetButton.setPrefWidth(80);
        resetButton.setPrefHeight(35);
        guessButton.setStyle("-fx-font-size: 18px;-fx-text-fill: #FFF;-fx-background-color: #FF5733;");
        resetButton.setStyle("-fx-font-size: 18px;-fx-text-fill: #FFF;-fx-background-color: #FF5733;");

        Label attemptsLabel = new Label("Attempts: 0");
        attemptsLabel.setStyle("-fx-font-size: 14px;-fx-text-fill: #FFF;");

        
        VBox vbox = new VBox(10, titleLabel, feedbackLabel, inputField, guessButton, resetButton, attemptsLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #333;-fx-text-fill: #FFF;");

        
        guessButton.setOnAction(event -> {
            String input = inputField.getText();
            try {
                int guess = Integer.parseInt(input);
                attempts++;
                if (guess < targetNumber) {
                    feedbackLabel.setText("Too low! Try again.");
                } else if (guess > targetNumber) {
                    feedbackLabel.setText("Too high! Try again.");
                } else {
                    feedbackLabel.setText("Correct! You guessed it in " + attempts + " attempts.");
                }
                attemptsLabel.setText("Attempts: " + attempts);
            } catch (NumberFormatException e) {
                feedbackLabel.setText("Invalid input! Please enter a number.");
            }
            inputField.clear();
        });

        resetButton.setOnAction(event -> {
            targetNumber = random.nextInt(100) + 1;
            attempts = 0;
            feedbackLabel.setText("Game reset. Enter your guess below:");
            attemptsLabel.setText("Attempts: 0");
            inputField.clear();
        });

        
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Number Guessing Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
