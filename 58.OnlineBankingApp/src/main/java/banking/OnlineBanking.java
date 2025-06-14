package banking;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class OnlineBanking extends Application {
    private Stage primaryStage;
    private Scene loginScene, registerScene, dashboardScene, depositScene, withdrawScene, transferScene, transactionsScene;
    private User currentUser;
    private DatabaseManager dbManager;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        dbManager = new DatabaseManager();

        createLoginScene();
        createRegisterScene();

        primaryStage.setTitle("OnlineBanking ");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void createLoginScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #7289DA, #424549);");

        Label title = createTitleLabel("OnlineBanking Login");
        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = createStyledPasswordField("Password");
        Button loginButton = createStyledButton("Login");
        Label registerPrompt = new Label("Don't have an account?");
        Hyperlink registerLink = createHyperlink("Register here");
        Label statusLabel = createStatusLabel();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password.");
                return;
            }
            User user = dbManager.authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                showDashboard();
                statusLabel.setText("");
                usernameField.clear();
                passwordField.clear();
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        });

        registerLink.setOnAction(e -> primaryStage.setScene(registerScene));

        layout.getChildren().addAll(title, usernameField, passwordField, loginButton, registerPrompt, registerLink, statusLabel);
        loginScene = new Scene(layout, 400, 500);
    }

    private void createRegisterScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #7289DA, #424549);");

        Label title = createTitleLabel("Register New Account");
        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = createStyledPasswordField("Password");
        PasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");
        TextField initialDepositField = createStyledTextField("Initial Deposit (min 100)");
        Button registerButton = createStyledButton("Register");
        Button backButton = createStyledButton("Back to Login");
        Label statusLabel = createStatusLabel();

        Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]{3,20}$");
        Pattern passwordPattern = Pattern.compile("^.{6,}$");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String depositText = initialDepositField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || depositText.isEmpty()) {
                statusLabel.setText("All fields are required.");
                return;
            }
            if (!usernamePattern.matcher(username).matches()) {
                statusLabel.setText("Username must be 3-20 alphanumeric characters.");
                return;
            }
            if (!passwordPattern.matcher(password).matches()) {
                statusLabel.setText("Password must be at least 6 characters.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match.");
                return;
            }
            double initialDeposit;
            try {
                initialDeposit = Double.parseDouble(depositText);
                if (initialDeposit < 100) {
                    statusLabel.setText("Initial deposit must be at least 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid deposit amount.");
                return;
            }
            if (dbManager.registerUser(username, password, initialDeposit)) {
                showAlert("Registration Successful", "Your account has been created with Account ID: " + dbManager.getAccountIdForUsername(username) + ".\nPlease log in.");
                primaryStage.setScene(loginScene);
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                initialDepositField.clear();
                statusLabel.setText("");
            } else {
                statusLabel.setText("Registration failed. Username might already exist.");
            }
        });

        backButton.setOnAction(e -> primaryStage.setScene(loginScene));

        layout.getChildren().addAll(title, usernameField, passwordField, confirmPasswordField, initialDepositField, registerButton, backButton, statusLabel);
        registerScene = new Scene(layout, 400, 600);
    }

    private void showDashboard() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #23272A;");

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        Label welcomeLabel = createLabel("Welcome, " + currentUser.getUsername() + "!", "#FFFFFF", 24, FontWeight.BOLD);
        Label accountIdLabel = createLabel("Account ID: " + currentUser.getAccountId(), "#BBBBBB", 14, FontWeight.NORMAL);
        Label balanceLabel = createLabel("Balance: PKR " + String.format("%.2f", dbManager.getAccountBalance(currentUser.getAccountId())), "#43B581", 30, FontWeight.BOLD);
        topBox.getChildren().addAll(welcomeLabel, accountIdLabel, balanceLabel);
        layout.setTop(topBox);
        BorderPane.setMargin(topBox, new Insets(0, 0, 20, 0));

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setAlignment(Pos.CENTER);

        Button depositBtn = createDashboardButton("Deposit");
        Button withdrawBtn = createDashboardButton("Withdraw");
        Button transferBtn = createDashboardButton("Transfer");
        Button historyBtn = createDashboardButton("Transactions");

        depositBtn.setOnAction(e -> showDeposit());
        withdrawBtn.setOnAction(e -> showWithdraw());
        transferBtn.setOnAction(e -> showTransfer());
        historyBtn.setOnAction(e -> showTransactions());

        buttonGrid.add(depositBtn, 0, 0);
        buttonGrid.add(withdrawBtn, 1, 0);
        buttonGrid.add(transferBtn, 0, 1);
        buttonGrid.add(historyBtn, 1, 1);
        layout.setCenter(buttonGrid);

        Button logoutBtn = createStyledButton("Logout");
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(loginScene);
        });
        layout.setBottom(logoutBtn);
        BorderPane.setAlignment(logoutBtn, Pos.CENTER);
        BorderPane.setMargin(logoutBtn, new Insets(20, 0, 0, 0));

        dashboardScene = new Scene(layout, 600, 700);
        primaryStage.setScene(dashboardScene);
    }

    private void showDeposit() {
        VBox layout = createActionLayout("Deposit Funds");
        TextField amountField = createStyledTextField("Amount to Deposit");
        Label statusLabel = createStatusLabel();
        Button depositBtn = createStyledButton("Deposit");
        Button backBtn = createStyledButton("Back to Dashboard");

        depositBtn.setOnAction(e -> {
            String amountText = amountField.getText();
            if (amountText.isEmpty()) {
                statusLabel.setText("Please enter an amount.");
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    statusLabel.setText("Amount must be positive.");
                    return;
                }
                if (dbManager.deposit(currentUser.getAccountId(), amount)) {
                    showAlert("Success", String.format("PKR %.2f deposited successfully.", amount));
                    showDashboard();
                } else {
                    statusLabel.setText("Deposit failed. Please try again.");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid amount.");
            }
        });

        backBtn.setOnAction(e -> showDashboard());
        layout.getChildren().addAll(amountField, depositBtn, backBtn, statusLabel);
        depositScene = new Scene(layout, 400, 450);
        primaryStage.setScene(depositScene);
    }

    private void showWithdraw() {
        VBox layout = createActionLayout("Withdraw Funds");
        TextField amountField = createStyledTextField("Amount to Withdraw");
        Label statusLabel = createStatusLabel();
        Button withdrawBtn = createStyledButton("Withdraw");
        Button backBtn = createStyledButton("Back to Dashboard");

        withdrawBtn.setOnAction(e -> {
            String amountText = amountField.getText();
            if (amountText.isEmpty()) {
                statusLabel.setText("Please enter an amount.");
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    statusLabel.setText("Amount must be positive.");
                    return;
                }
                double currentBalance = dbManager.getAccountBalance(currentUser.getAccountId());
                if (amount > currentBalance) {
                    statusLabel.setText("Insufficient balance. Current: PKR " + String.format("%.2f", currentBalance));
                    return;
                }
                if (dbManager.withdraw(currentUser.getAccountId(), amount)) {
                    showAlert("Success", String.format("PKR %.2f withdrawn successfully.", amount));
                    showDashboard();
                } else {
                    statusLabel.setText("Withdrawal failed. Please try again.");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid amount.");
            }
        });

        backBtn.setOnAction(e -> showDashboard());
        layout.getChildren().addAll(amountField, withdrawBtn, backBtn, statusLabel);
        withdrawScene = new Scene(layout, 400, 450);
        primaryStage.setScene(withdrawScene);
    }

    private void showTransfer() {
        VBox layout = createActionLayout("Transfer Funds");
        TextField recipientIdField = createStyledTextField("Recipient Account ID");
        TextField amountField = createStyledTextField("Amount to Transfer");
        Label statusLabel = createStatusLabel();
        Button transferBtn = createStyledButton("Transfer");
        Button backBtn = createStyledButton("Back to Dashboard");

        transferBtn.setOnAction(e -> {
            String recipientIdText = recipientIdField.getText();
            String amountText = amountField.getText();

            if (recipientIdText.isEmpty() || amountText.isEmpty()) {
                statusLabel.setText("Please fill all fields.");
                return;
            }
            try {
                int recipientId = Integer.parseInt(recipientIdText);
                if (recipientId == currentUser.getAccountId()) {
                    statusLabel.setText("Cannot transfer to your own account.");
                    return;
                }
                if (!dbManager.accountExists(recipientId)) {
                    statusLabel.setText("Recipient Account ID does not exist.");
                    return;
                }
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    statusLabel.setText("Amount must be positive.");
                    return;
                }
                double currentBalance = dbManager.getAccountBalance(currentUser.getAccountId());
                if (amount > currentBalance) {
                    statusLabel.setText("Insufficient balance. Current: PKR " + String.format("%.2f", currentBalance));
                    return;
                }
                if (dbManager.transferFunds(currentUser.getAccountId(), recipientId, amount)) {
                    showAlert("Success", String.format("PKR %.2f transferred to Account %d successfully.", amount, recipientId));
                    showDashboard();
                } else {
                    statusLabel.setText("Transfer failed. Please try again.");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid Account ID or Amount.");
            }
        });

        backBtn.setOnAction(e -> showDashboard());
        layout.getChildren().addAll(recipientIdField, amountField, transferBtn, backBtn, statusLabel);
        transferScene = new Scene(layout, 400, 500);
        primaryStage.setScene(transferScene);
    }

    private void showTransactions() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #23272A;");
        Label title = createTitleLabel("Transaction History");
        layout.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        ListView<String> transactionListView = new ListView<>();
        transactionListView.setPrefHeight(400);
        transactionListView.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF;");

        dbManager.getTransactions(currentUser.getAccountId()).forEach(transaction -> {
            transactionListView.getItems().add(
                String.format("[%s] %s: PKR %.2f - %s",
                    transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getDescription()
                )
            );
        });

        layout.setCenter(transactionListView);
        BorderPane.setMargin(transactionListView, new Insets(0, 0, 20, 0));
        Button backBtn = createStyledButton("Back to Dashboard");
        backBtn.setOnAction(e -> showDashboard());
        layout.setBottom(backBtn);
        BorderPane.setAlignment(backBtn, Pos.CENTER);

        transactionsScene = new Scene(layout, 600, 700);
        primaryStage.setScene(transactionsScene);
    }

    // --- UI Helper Methods ---
    private Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        label.setTextFill(Color.web("#FFFFFF"));
        return label;
    }
    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(40);
        field.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-border-color: #424549; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-border-color: #7289DA; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            } else {
                field.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-border-color: #424549; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            }
        });
        return field;
    }
    private PasswordField createStyledPasswordField(String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setPrefHeight(40);
        field.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-border-color: #424549; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-border-color: #7289DA; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            } else {
                field.setStyle("-fx-control-inner-background: #36393F; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-border-color: #424549; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            }
        });
        return field;
    }
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(45);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #7289DA; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10 20;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #677BC4; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10 20;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #7289DA; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10 20;"));
        return button;
    }
    private Hyperlink createHyperlink(String text) {
        Hyperlink link = new Hyperlink(text);
        link.setTextFill(Color.web("#FFFFFF"));
        link.setFont(Font.font("Arial", 14));
        link.setStyle("-fx-underline: true;");
        link.setOnMouseEntered(e -> link.setTextFill(Color.web("#43B581")));
        link.setOnMouseExited(e -> link.setTextFill(Color.web("#FFFFFF")));
        return link;
    }
    private Button createDashboardButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 80);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #424549; -fx-background-radius: 10px; -fx-border-color: #36393F; -fx-border-width: 2px; -fx-border-radius: 10px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #36393F; -fx-background-radius: 10px; -fx-border-color: #7289DA; -fx-border-width: 2px; -fx-border-radius: 10px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #424549; -fx-background-radius: 10px; -fx-border-color: #36393F; -fx-border-width: 2px; -fx-border-radius: 10px;"));
        return button;
    }
    private Label createLabel(String text, String colorHex, int fontSize, FontWeight weight) {
        Label label = new Label(text);
        label.setTextFill(Color.web(colorHex));
        label.setFont(Font.font("Arial", weight, fontSize));
        return label;
    }
    private Label createStatusLabel() {
        Label label = new Label();
        label.setTextFill(Color.RED);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return label;
    }
    private VBox createActionLayout(String titleText) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #2C2F33, #23272A);");
        Label title = createTitleLabel(titleText);
        layout.getChildren().add(title);
        return layout;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
