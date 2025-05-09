package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends Application {
    private static final int WINDOW_SIZE = 500;
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = WINDOW_SIZE / GRID_SIZE;
    private static final int SPEED = 150;

    private ArrayList<Circle> snake = new ArrayList<>();
    private Circle food;
    private double snakeX = GRID_SIZE / 2, snakeY = GRID_SIZE / 2;
    private double dx = 1, dy = 0;
    private boolean gameOver = false;
    private int score = 0;
    private Text scoreText;
    private Text gameOverText;
    private Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, WINDOW_SIZE, WINDOW_SIZE);
        scene.setFill(Color.DARKGREEN);

        // grid background
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Circle cell = new Circle((x + 0.5) * CELL_SIZE, (y + 0.5) * CELL_SIZE, CELL_SIZE / 2);
                cell.setFill(Color.BLACK);
                cell.setStroke(Color.DARKGREEN);
                cell.setStrokeWidth(0.5);
                root.getChildren().add(cell);
            }
        }

        // score display
        scoreText = new Text(10, 20, "Score: 0");
        scoreText.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        scoreText.setFill(Color.LIGHTGREEN);
        root.getChildren().add(scoreText);

        // snake
        Circle head = createPixel(snakeX, snakeY, Color.YELLOW);
        snake.add(head);
        root.getChildren().add(head);

        // food
        spawnFood(root);

        // Handle key
        scene.setOnKeyPressed(event -> {
            if (!gameOver) {
                if (event.getCode() == KeyCode.UP && dy != 1) {
                    dx = 0; dy = -1;
                } else if (event.getCode() == KeyCode.DOWN && dy != -1) {
                    dx = 0; dy = 1;
                } else if (event.getCode() == KeyCode.LEFT && dx != 1) {
                    dx = -1; dy = 0;
                } else if (event.getCode() == KeyCode.RIGHT && dx != -1) {
                    dx = 1; dy = 0;
                }
            } else if (event.getCode() == KeyCode.SPACE) {
                restartGame(root);
            }
        });

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (gameOver) {
                    return;
                }

                if (now - lastUpdate >= SPEED * 1_000_000) {
                    update(root);
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();

        primaryStage.setTitle("Snake Game in Java");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Circle createPixel(double x, double y, Color color) {
        Circle pixel = new Circle((x + 0.5) * CELL_SIZE, (y + 0.5) * CELL_SIZE, CELL_SIZE / 2);
        pixel.setFill(color);
        return pixel;
    }

    private Color getSnakeColor() {
    	return Color.YELLOW;
    }

    private void spawnFood(Group root) {
        if (food != null) {
            root.getChildren().remove(food);
        }
        double x, y;
        do {
            x = random.nextInt(GRID_SIZE);
            y = random.nextInt(GRID_SIZE);
        } while (isPositionOccupied(x, y));
        food = createPixel(x, y, Color.WHITE);
        root.getChildren().add(food);
    }

    private boolean isPositionOccupied(double x, double y) {
        for (Circle segment : snake) {
            if (Math.abs(segment.getCenterX() / CELL_SIZE - 0.5 - x) < 0.1 &&
                Math.abs(segment.getCenterY() / CELL_SIZE - 0.5 - y) < 0.1) {
                return true;
            }
        }
        return false;
    }

    private void update(Group root) {
        // Update snake position
        snakeX += dx;
        snakeY += dy;

        // Check boundaries
        if (snakeX < 0 || snakeX >= GRID_SIZE || snakeY < 0 || snakeY >= GRID_SIZE) {
            endGame(root);
            return;
        }

        // Check self-collision
        if (isPositionOccupied(snakeX, snakeY)) {
            endGame(root);
            return;
        }

        // Create new head with size-based color
        Circle newHead = createPixel(snakeX, snakeY, getSnakeColor());
        snake.add(0, newHead);
        root.getChildren().add(newHead);

        // Check food collision
        boolean ateFood = Math.abs(snakeX - (food.getCenterX() / CELL_SIZE - 0.5)) < 0.1 &&
                         Math.abs(snakeY - (food.getCenterY() / CELL_SIZE - 0.5)) < 0.1;

        if (ateFood) {
            // Increment score and update display
            score += 10;
            scoreText.setText("Score: " + score);
            spawnFood(root);
            // Update snake color after eating (size increases)
            for (Circle segment : snake) {
                segment.setFill(getSnakeColor());
            }
        } else {
            // Remove tail if no food was eaten
            Circle tail = snake.remove(snake.size() - 1);
            root.getChildren().remove(tail);
        }
    }

    private void endGame(Group root) {
        gameOver = true;
        gameOverText = new Text(WINDOW_SIZE / 4, WINDOW_SIZE / 2, "Game Over\nScore: " + score + "\nPress SPACE to Restart");
        gameOverText.setFont(Font.font("Monospace",FontWeight.EXTRA_BOLD, 20));
        gameOverText.setFill(Color.LIGHTGREEN);
        root.getChildren().add(gameOverText);
    }

    private void restartGame(Group root) {
        // Clear snake
        root.getChildren().removeAll(snake);
        snake.clear();

        // Reset variables
        snakeX = GRID_SIZE / 2;
        snakeY = GRID_SIZE / 2;
        dx = 1;
        dy = 0;
        score = 0;
        gameOver = false;

        // Reset score 
        scoreText.setText("Score: 0");

        // Remove game over text
        if (gameOverText != null) {
            root.getChildren().remove(gameOverText);
            gameOverText = null;
        }

        // Reinitialize snake
        Circle head = createPixel(snakeX, snakeY, Color.WHITE); // Size 1: White
        snake.add(head);
        root.getChildren().add(head);

        // Respawn food
        spawnFood(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}