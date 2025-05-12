package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.security.SecureRandom;

public class PasswordGenerator extends Application {

    private SecureRandom random = new SecureRandom();

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Password Generator");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label lengthLabel = new Label("Length:");
        lengthLabel.setStyle("-fx-text-fill: #ffffff;");

        Spinner<Integer> lengthSpinner = new Spinner<>(8, 32, 12);
        lengthSpinner.setStyle("-fx-background-color: #555555; -fx-text-fill: #ffffff;");
        lengthSpinner.setPrefWidth(300);
        lengthSpinner.setPadding(new Insets(10));

        CheckBox includeUppercase = createDarkCheckBox("Include Uppercase");
        CheckBox includeLowercase = createDarkCheckBox("Include Lowercase");
        CheckBox includeDigits = createDarkCheckBox("Include Digits");
        CheckBox includeSpecial = createDarkCheckBox("Include Special Characters");

        Button generateButton = new Button("Generate Password");
        generateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        generateButton.setPrefWidth(300);
        generateButton.setPadding(new Insets(10));

        TextField passwordField = new TextField();
        passwordField.setEditable(false);
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-color: #555555; -fx-text-fill: #ffffff;");
        passwordField.setPadding(new Insets(10));

        VBox optionsBox = new VBox(10, lengthLabel, lengthSpinner, includeUppercase, includeLowercase, includeDigits, includeSpecial);
        optionsBox.setPadding(new Insets(10));

        VBox root = new VBox(20, titleLabel, optionsBox, generateButton, passwordField);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #333333;");
        root.setPrefSize(400, 300);

        generateButton.setOnAction(e -> {
            int length = lengthSpinner.getValue();
            boolean useUppercase = includeUppercase.isSelected();
            boolean useLowercase = includeLowercase.isSelected();
            boolean useDigits = includeDigits.isSelected();
            boolean useSpecial = includeSpecial.isSelected();

            String password = generatePassword(length, useUppercase, useLowercase, useDigits, useSpecial);
            passwordField.setText(password);
        });

        Scene scene = new Scene(root);
        primaryStage.setTitle("Password Generator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private CheckBox createDarkCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setStyle("-fx-text-fill: #ffffff;");
        return checkBox;
    }

    private String generatePassword(int length, boolean useUppercase, boolean useLowercase, boolean useDigits, boolean useSpecial) {
        if (!useUppercase && !useLowercase && !useDigits && !useSpecial) {
            return "Select at least one character type!";
        }

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+[]{};:,.<>?/";

        StringBuilder charPool = new StringBuilder();
        if (useUppercase) charPool.append(upper);
        if (useLowercase) charPool.append(lower);
        if (useDigits) charPool.append(digits);
        if (useSpecial) charPool.append(special);

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charPool.length());
            password.append(charPool.charAt(index));
        }
        return password.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
