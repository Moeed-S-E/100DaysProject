package game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class MinesweeperGame extends Application {
    private static final int W = 32; // Tile width/height in pixels
    private static final int GRID_SIZE = 10; // 10x10 grid
    private static final int WINDOW_SIZE = W * GRID_SIZE; // 320x320 window
    private final int[][] grid = new int[GRID_SIZE + 2][GRID_SIZE + 2]; // Game logic grid
    private final int[][] sgrid = new int[GRID_SIZE + 2][GRID_SIZE + 2]; // Display grid
    private Image tiles;
    private GraphicsContext gc; // Store GraphicsContext for reuse

    @Override
    public void start(Stage primaryStage) {
        // Set up JavaFX scene
        Canvas canvas = new Canvas(WINDOW_SIZE, WINDOW_SIZE);
        gc = canvas.getGraphicsContext2D();

        // Load tiles image
        tiles = new Image(getClass().getResource("/images/tiles.jpg").toExternalForm());

        // Initialize game
        resetGame();

        // Handle mouse events
        canvas.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / W) + 1;
            int y = (int) (event.getY() / W) + 1;
            if (x >= 1 && x <= GRID_SIZE && y >= 1 && y <= GRID_SIZE) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    sgrid[x][y] = grid[x][y]; // Reveal cell
                    if (grid[x][y] == 9) { // Mine revealed
                        showGameOverDialog(primaryStage);
                    }
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    sgrid[x][y] = 11; // Flag cell
                }
                drawBoard(x, y);
            }
        });

        // Initial draw
        drawBoard(0, 0);

        // Set up scene and stage
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, WINDOW_SIZE, WINDOW_SIZE, Color.WHITE);
        primaryStage.setTitle("Minesweeper!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void resetGame() {
        Random rand = new Random();

        // Initialize grids
        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                sgrid[i][j] = 10; // Unrevealed cell
                grid[i][j] = (rand.nextInt(5) == 0) ? 9 : 0; // ~20% chance of mine
            }
        }

        // Calculate adjacent mines
        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                if (grid[i][j] == 9) continue;
                int n = 0;
                if (grid[i + 1][j] == 9) n++;
                if (grid[i][j + 1] == 9) n++;
                if (grid[i - 1][j] == 9) n++;
                if (grid[i][j - 1] == 9) n++;
                if (grid[i + 1][j + 1] == 9) n++;
                if (grid[i - 1][j - 1] == 9) n++;
                if (grid[i - 1][j + 1] == 9) n++;
                if (grid[i + 1][j - 1] == 9) n++;
                grid[i][j] = n;
            }
        }
    }

    private void showGameOverDialog(Stage primaryStage) {
        // Reveal all mines
        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                sgrid[i][j] = grid[i][j];
            }
        }
        drawBoard(0, 0); // Redraw to show all mines

        // Show game over dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("You hit a mine!");
        alert.setContentText("Would you like to play again?");

        ButtonType restartButton = new ButtonType("Restart");
        ButtonType exitButton = new ButtonType("Exit");
        alert.getButtonTypes().setAll(restartButton, exitButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == restartButton) {
                resetGame();
                drawBoard(0, 0);
            } else if (response == exitButton) {
                primaryStage.close();
            }
        });
    }

    private void drawBoard(int mouseX, int mouseY) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);

        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                // Draw tile from sprite sheet
                int tileIndex = sgrid[i][j];
                gc.drawImage(tiles, tileIndex * W, 0, W, W, (i - 1) * W, (j - 1) * W, W, W);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}