package calculator;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private Label display;
    private StringBuilder currentInput = new StringBuilder();
    private double firstOperand = 0;
    private String operator = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Splash Screen (Welcome Page)
        Stage splashStage = new Stage();
        Label welcomeLabel = new Label("Welcome to the Calculator!\nDeveloper Moeed and Zainab");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: #2c2c2c; -fx-padding: 20px;");

        StackPane splashRoot = new StackPane(welcomeLabel);
        splashRoot.setStyle("-fx-background-color: #1c1c1c;");
        splashRoot.setAlignment(Pos.CENTER);

        Scene splashScene = new Scene(splashRoot, 350, 200);
        splashStage.setScene(splashScene);
        splashStage.setTitle("Welcome");
        splashStage.show();

        // Welcome screen will hide after 1 second
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            splashStage.close();
            showCalculatorApp(primaryStage);
        });
        pause.play();
    }

    private void showCalculatorApp(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        // Display
        display = new Label("0");
        display.setStyle("-fx-font-size: 30px; -fx-alignment: center-right; -fx-background-color: #2c2c2c; -fx-text-fill: white; -fx-padding: 10px;");
        display.setPrefHeight(70);
        display.setMaxWidth(Double.MAX_VALUE);

        // Buttons
        String[][] buttonLabels = {
            {"C", "\u00B1", "%", "/"},
            {"7", "8", "9", "x"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", ""},
            {"0", ".", "=", null}
        };

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setPadding(new Insets(10));
        buttonGrid.setStyle("-fx-background-color: #1c1c1c;");

        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                String label = buttonLabels[row][col];
                if (label != null) {
                    Button button = createButton(label);
                    buttonGrid.add(button, col, row);
                    if (label.equals("0")) {
                        GridPane.setColumnSpan(button, 2);
                    }
                }
            }
        }

        VBox root = new VBox(display, buttonGrid);
        root.setSpacing(10);
        root.setStyle("-fx-background-color: #1c1c1c;");
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-font-size: 20px; -fx-background-color: #3e3e3e; -fx-text-fill: white; -fx-background-radius: 50%;");
        button.setPrefSize(60, 60);

        button.setOnAction(e -> {

            button.setStyle("-fx-font-size: 20px; -fx-background-color: #fa8f02; -fx-text-fill: white; -fx-background-radius: 50%;");


            handleButtonPress(label);

            new Thread(() -> {
                try {
                    Thread.sleep(200); //200ms for reseting color
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                button.setStyle("-fx-font-size: 20px; -fx-background-color: #3e3e3e; -fx-text-fill: white; -fx-background-radius: 50%;");
            }).start();
        });
        return button;
    }

    private void handleButtonPress(String label) {
        if (label.matches("[0-9\\.]") || label.equals(".")) {
            handleNumberInput(label);
        } else if (label.matches("[\\+\\-x\\/%]") || label.equals("/")) {
            handleOperator(label);
        } else if (label.equals("C")) {
            clear();
        } else if (label.equals("=")) {
            calculateResult();
        } else if (label.equals("\u00B1")) {
            toggleSign();
        }
    }

    private void handleNumberInput(String input) {
        if (operator.isEmpty()) {
            currentInput.append(input);
            updateDisplay();
        } else {
            if (currentInput.length() == 0 && input.equals(".")) {
                currentInput.append("0.");
            } else {
                currentInput.append(input);
            }
            updateDisplay();
        }
    }

    private void handleOperator(String op) {
        if (!operator.isEmpty() && currentInput.length() > 0) {
            calculateResult();
        }
        operator = op;
        firstOperand = Double.parseDouble(display.getText());
        currentInput.setLength(0);
    }

    private void clear() {
        currentInput.setLength(0);
        firstOperand = 0;
        operator = "";
        updateDisplay();
    }

    private void toggleSign() {
        if (currentInput.length() > 0) {
            double value = Double.parseDouble(currentInput.toString());
            value *= -1;
            currentInput = new StringBuilder(String.valueOf(value));
            updateDisplay();
        }
    }

    private void calculateResult() {
        if (operator.isEmpty() || currentInput.length() == 0) {
            return;
        }
        double secondOperand = Double.parseDouble(currentInput.toString());
        double result = 0;
        switch (operator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "x":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand != 0) {
                    result = firstOperand / secondOperand;
                } else {
                    display.setText("Error");
                    return;
                }
                break;
            case "%":
                result = firstOperand % secondOperand;
                break;
        }
        currentInput = new StringBuilder(String.valueOf(result));
        operator = "";
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentInput.length() > 0) {
            display.setText(currentInput.toString());
        } else {
            display.setText(String.valueOf(firstOperand));
        }
    }
}
