package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Controller {
    private static final String WELCOME_FXML = "Welcome.fxml";
    private static final String LOGIN_FXML = "Login.fxml";
    private static final String QUICK_WITHDRAW_FXML = "QuickWithdraw.fxml";
    private static final String WITHDRAW_FXML = "WithdrawPage.fxml";
    private static final String TRANSACTIONS_FXML = "Transactions.fxml";
    private static final String MAIN_MENU_FXML = "WithdrawMain.fxml";

    private Stage stage;
    private Scene scene;

    @FXML
    private PasswordField pinField;

    @FXML
    private TextField amountField;

    @FXML
    private Label balanceLabel;

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            if (fxmlFile.equals(TRANSACTIONS_FXML)) {
                Controller controller = loader.getController();
                controller.updateBalance();
                System.out.println("Switching to Transactions, balance: $" + Account.getInstance().getBalance());
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load page: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void WelcomePage(ActionEvent event) {
        switchScene(event, WELCOME_FXML);
    }

    public void PinPage(ActionEvent event) {
        switchScene(event, LOGIN_FXML);
    }

    public void validatePin(ActionEvent event) {
        String pin = pinField.getText();
        if (Account.getInstance().validatePin(pin)) {
            switchScene(event, MAIN_MENU_FXML);
        } else {
            showAlert("Error", "Invalid PIN. Please try again.");
            pinField.clear();
        }
    }

    public void switchToQuickWithdrawPage(ActionEvent event) {
        switchScene(event, QUICK_WITHDRAW_FXML);
    }

    public void switchToWithdrawPage(ActionEvent event) {
        switchScene(event, WITHDRAW_FXML);
    }

    public void processWithdrawal(ActionEvent event) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            Account account = Account.getInstance();
            if (account.withdraw(amount)) {
                TransactionManager.getInstance().addTransaction(amount, account.getBalance());
                showAlert("Success", "Withdrawal of $" + amount + " successful!");
                System.out.println("Custom withdrawal: $" + amount + ", New balance: $" + account.getBalance());
                switchScene(event, TRANSACTIONS_FXML);
            } else {
                showAlert("Error", "Withdrawal failed: Amount must be a multiple of 500 and within balance.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input: Please enter a valid number.");
        }
        amountField.clear();
    }

    public void subBalance(ActionEvent event) {
        Button button = (Button) event.getSource();
        double amount = Double.parseDouble(button.getText());
        Account account = Account.getInstance();
        if (account.withdraw(amount)) {
            TransactionManager.getInstance().addTransaction(amount, account.getBalance());
            showAlert("Success", "Withdrawal of $" + amount + " successful!");
            System.out.println("Quick withdrawal: $" + amount + ", New balance: $" + account.getBalance());
            switchScene(event, TRANSACTIONS_FXML);
        } else {
            showAlert("Error", "Withdrawal failed: Insufficient funds or invalid amount.");
        }
    }

    public void switchToTranscationsPage(ActionEvent event) {
        switchScene(event, TRANSACTIONS_FXML);
    }

    public void switchToMainMenu(ActionEvent event) {
        switchScene(event, MAIN_MENU_FXML);
    }

    public void updateBalance() {
        Platform.runLater(() -> {
            if (balanceLabel != null) {
                balanceLabel.setText("Current Balance: $" + Account.getInstance().getBalance());
                System.out.println("Updating balance label to: $" + Account.getInstance().getBalance());
            } else {
                System.out.println("Error: balanceLabel is null in updateBalance");
            }
        });
    }

    public void downloadCsv(ActionEvent event) {
        File csvFile = TransactionManager.getInstance().getCsvFile();
        if (csvFile == null) {
            showAlert("Error", "No transaction file available.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction CSV");
        fileChooser.setInitialFileName("transactions.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File destination = fileChooser.showSaveDialog(stage);

        if (destination != null) {
            try {
                Files.copy(csvFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert("Success", "CSV file saved to: " + destination.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to save CSV file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void printTransactions(ActionEvent event) {
        TransactionManager.getInstance().printTransactions();
        showAlert("Success", "Transaction history printed to console.");
    }
}

class Account {
    private static Account instance = null;
    private double balance = 100000; // Initial balance
    private String pin = "1234"; // Default PIN

    private Account() {}

    public static Account getInstance() {
        if (instance == null) {
            instance = new Account();
        }
        return instance;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance && amount % 500 == 0) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount + ", Remaining balance: $" + balance);
            return true;
        }
        System.out.println("Withdrawal failed: Amount $" + amount + " invalid or insufficient funds");
        return false;
    }

    public double getBalance() {
        return balance;
    }

    public boolean validatePin(String inputPin) {
        return pin.equals(inputPin);
    }
}