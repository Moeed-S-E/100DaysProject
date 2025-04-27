package app;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TicTacToe extends Application {
    private boolean isXTurn = true;
    private Button[][] buttons = new Button[3][3];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tic Tac Toe");
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #2c3e50; -fx-padding: 10; -fx-hgap: 10; -fx-vgap: 10;");

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button button = new Button("");
                button.setMinSize(100, 100);
                button.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50;");
                int r = row, c = col;
                button.setOnAction(e -> handleMove(button, r, c));
                buttons[row][col] = button;
                grid.add(button, col, row);
            }
        }

        Scene scene = new Scene(grid, 340, 340);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleMove(Button button, int row, int col) {
        if (!button.getText().isEmpty()) return;

        button.setText(isXTurn ? "X" : "O");
        button.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-background-color: " + (isXTurn ? "#3498db" : "#e74c3c") + "; -fx-text-fill: white;");

        if (checkWin(row, col)) {
            showAlert((isXTurn ? "X" : "O") + " wins!");
            resetBoard();
        } else if (isBoardFull()) {
            showAlert("It's a draw!");
            resetBoard();
        } else {
            isXTurn = !isXTurn;
        }
    }

    private boolean checkWin(int row, int col) {
        String player = buttons[row][col].getText();
        return checkLine(player, buttons[row][0], buttons[row][1], buttons[row][2]) ||
               checkLine(player, buttons[0][col], buttons[1][col], buttons[2][col]) ||
               checkLine(player, buttons[0][0], buttons[1][1], buttons[2][2]) ||
               checkLine(player, buttons[0][2], buttons[1][1], buttons[2][0]);
    }

    private boolean checkLine(String player, Button... line) {
        for (Button button : line) {
            if (!button.getText().equals(player)) return false;
        }
        return true;
    }

    private boolean isBoardFull() {
        for (Button[] row : buttons) {
            for (Button button : row) {
                if (button.getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetBoard() {
        for (Button[] row : buttons) {
            for (Button button : row) {
                button.setText("");
                button.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50;");
            }
        }
        isXTurn = true;
    }
}