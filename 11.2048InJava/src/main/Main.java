package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Random;

public class Main extends Application {
    private static final int SIZE = 4;
    private static final int TILE_SIZE = 100;
    private int[][] board = new int[SIZE][SIZE];
    private GridPane gridPane = new GridPane();
    private Random random = new Random();
    private boolean hasMoved = false;

    @Override
    public void start(Stage primaryStage) {
        initializeBoard();
        Scene scene = new Scene(gridPane, SIZE * TILE_SIZE, SIZE * TILE_SIZE);
        scene.setOnKeyPressed(event -> {
            hasMoved = false;
            if (event.getCode() == KeyCode.UP) {
                moveUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                moveDown();
            } else if (event.getCode() == KeyCode.LEFT) {
                moveLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                moveRight();
            }
            if (hasMoved) {
                addNewTile();
                updateBoard();
                if (isGameOver()) {
                    showGameOverDialog(primaryStage);
                }
            }
        });

        primaryStage.setTitle("2048 Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        updateBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
        addNewTile();
        addNewTile();
    }

    private void resetGame() {
        initializeBoard();
        updateBoard();
    }

    private boolean isGameOver() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (j < SIZE - 1 && board[i][j] == board[i][j + 1]) {
                    return false;
                }
                if (i < SIZE - 1 && board[i][j] == board[i + 1][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showGameOverDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("No more moves possible!");
        alert.setContentText("Would you like to restart the game?");
        ButtonType restartButton = new ButtonType("Restart");
        ButtonType exitButton = new ButtonType("Exit");
        alert.getButtonTypes().setAll(restartButton, exitButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == restartButton) {
                resetGame();
            } else if (response == exitButton) {
                primaryStage.close();
            }
        });
    }

    private void addNewTile() {
        int emptyCount = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCount++;
                }
            }
        }
        if (emptyCount == 0) return;

        int newTile = random.nextInt(emptyCount) + 1;
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    count++;
                    if (count == newTile) {
                        board[i][j] = random.nextDouble() < 0.9 ? 2 : 4;
                        return;
                    }
                }
            }
        }
    }

    private void updateBoard() {
        gridPane.getChildren().clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                StackPane tile = new StackPane();
                Rectangle rect = new Rectangle(TILE_SIZE - 5, TILE_SIZE - 5);
                rect.setFill(getTileColor(board[i][j]));
                rect.setArcWidth(10);
                rect.setArcHeight(10);
                Text text = new Text(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                text.setFont(Font.font("Arial", 24));
                text.setFill(Color.BLACK);
                tile.getChildren().addAll(rect, text);
                gridPane.add(tile, j, i);
            }
        }
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 0: return Color.LIGHTGRAY;
            case 2: return Color.rgb(238, 228, 218);
            case 4: return Color.rgb(237, 224, 200);
            case 8: return Color.rgb(242, 177, 121);
            case 16: return Color.rgb(245, 149, 99);
            case 32: return Color.rgb(246, 124, 95);
            case 64: return Color.rgb(246, 94, 59);
            case 128: return Color.rgb(237, 207, 114);
            case 256: return Color.rgb(237, 204, 97);
            case 512: return Color.rgb(237, 200, 80);
            case 1024: return Color.rgb(237, 197, 63);
            case 2048: return Color.rgb(237, 194, 46);
            default: return Color.rgb(238, 228, 218);
        }
    }

    private void moveLeft() {
        for (int i = 0; i < SIZE; i++) {
            int[] newRow = new int[SIZE];
            int pos = 0;
            int lastMerged = -1;
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    if (pos > 0 && newRow[pos - 1] == board[i][j] && lastMerged != pos - 1) {
                        newRow[pos - 1] *= 2;
                        lastMerged = pos - 1;
                        hasMoved = true;
                    } else {
                        newRow[pos] = board[i][j];
                        pos++;
                        if (board[i][j] != newRow[pos - 1]) hasMoved = true;
                    }
                }
            }
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != newRow[j]) hasMoved = true;
                board[i][j] = newRow[j];
            }
        }
    }

    private void moveRight() {
        for (int i = 0; i < SIZE; i++) {
            int[] newRow = new int[SIZE];
            int pos = SIZE - 1;
            int lastMerged = SIZE;
            for (int j = SIZE - 1; j >= 0; j--) {
                if (board[i][j] != 0) {
                    if (pos < SIZE - 1 && newRow[pos + 1] == board[i][j] && lastMerged != pos + 1) {
                        newRow[pos + 1] *= 2;
                        lastMerged = pos + 1;
                        hasMoved = true;
                    } else {
                        newRow[pos] = board[i][j];
                        pos--;
                        if (board[i][j] != newRow[pos + 1]) hasMoved = true;
                    }
                }
            }
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != newRow[j]) hasMoved = true;
                board[i][j] = newRow[j];
            }
        }
    }

    private void moveUp() {
        for (int j = 0; j < SIZE; j++) {
            int[] newCol = new int[SIZE];
            int pos = 0;
            int lastMerged = -1;
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != 0) {
                    if (pos > 0 && newCol[pos - 1] == board[i][j] && lastMerged != pos - 1) {
                        newCol[pos - 1] *= 2;
                        lastMerged = pos - 1;
                        hasMoved = true;
                    } else {
                        newCol[pos] = board[i][j];
                        pos++;
                        if (board[i][j] != newCol[pos - 1]) hasMoved = true;              }
                }
            }
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != newCol[i]) hasMoved = true;
                board[i][j] = newCol[i];
            }
        }
    }

    private void moveDown() {
        for (int j = 0; j < SIZE; j++) {
            int[] newCol = new int[SIZE];
            int pos = SIZE - 1;
            int lastMerged = SIZE;
            for (int i = SIZE - 1; i >= 0; i--) {
                if (board[i][j] != 0) {
                    if (pos < SIZE - 1 && newCol[pos + 1] == board[i][j] && lastMerged != pos + 1) {
                        newCol[pos + 1] *= 2;
                        lastMerged = pos + 1;
                        hasMoved = true;
                    } else {
                        newCol[pos] = board[i][j];
                        pos--;
                        if (board[i][j] != newCol[pos + 1]) hasMoved = true;
                    }
                }
            }
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != newCol[i]) hasMoved = true;
                board[i][j] = newCol[i];
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}