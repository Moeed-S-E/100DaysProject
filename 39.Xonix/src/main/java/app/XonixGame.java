package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

public class XonixGame extends Application {
    private static final int M = 25;
    private static final int N = 40;
    private static final int TS = 18; // Tile size
    private static int[][] grid = new int[M][N];
    private static boolean game = true;
    private static int x = 0, y = 0, dx = 0, dy = 0;
    private static double timer = 0, delay = 0.07;
    private static double lastTime;
    private static double enemyRotation = 0; 
    private static int score = 0; // Added score variable
    private static int totalArea; // Track total playable area for percentage

    static class Enemy {
        int x, y, dx, dy;

        Enemy() {
            x = y = 300;
            dx = 4 - (int)(Math.random() * 8);
            dy = 4 - (int)(Math.random() * 8);
        }

        void move() {
            x += dx;
            if (x / TS >= 0 && x / TS < N && y / TS >= 0 && y / TS < M && grid[y / TS][x / TS] == 1) {
                dx = -dx;
                x += dx;
            }
            y += dy;
            if (x / TS >= 0 && x / TS < N && y / TS >= 0 && y / TS < M && grid[y / TS][x / TS] == 1) {
                dy = -dy;
                y += dy;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Image tilesImage = new Image(getClass().getResourceAsStream("/images/tiles.png"));
        Image gameoverImage = new Image(getClass().getResourceAsStream("/images/gameover.png"));
        Image enemyImage = new Image(getClass().getResourceAsStream("/images/enemy.png"));

        Canvas canvas = new Canvas(N * TS, M * TS);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);

        Scene scene = new Scene(root, N * TS, M * TS);

        // Initialize grid and count total playable area
        totalArea = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (i == 0 || j == 0 || i == M - 1 || j == N - 1) {
                    grid[i][j] = 1;
                } else {
                    totalArea++; // Count non-border tiles
                }
            }
        }

        // Initialize enemies
        int enemyCount = 4;
        Enemy[] enemies = new Enemy[10];
        for (int i = 0; i < enemyCount; i++) {
            enemies[i] = new Enemy();
        }

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                resetGame();
            }
            if (event.getCode() == KeyCode.R && !game) { // Rerun option
                resetGame();
            }
            if (game) {
                if (event.getCode() == KeyCode.LEFT) { dx = -1; dy = 0; }
                if (event.getCode() == KeyCode.RIGHT) { dx = 1; dy = 0; }
                if (event.getCode() == KeyCode.UP) { dx = 0; dy = -1; }
                if (event.getCode() == KeyCode.DOWN) { dx = 0; dy = 1; }
            }
        });

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double currentTime = currentNanoTime / 1_000_000_000.0;
                if (lastTime == 0) lastTime = currentTime;
                double deltaTime = currentTime - lastTime;
                lastTime = currentTime;
                timer += deltaTime;
                enemyRotation += 10 * deltaTime;

                if (game) {
                    if (timer > delay) {
                        x += dx;
                        y += dy;

                        if (x < 0) x = 0;
                        if (x > N - 1) x = N - 1;
                        if (y < 0) y = 0;
                        if (y > M - 1) y = M - 1;

                        if (grid[y][x] == 2) game = false;
                        if (grid[y][x] == 0) grid[y][x] = 2;
                        timer = 0;
                    }

                    for (int i = 0; i < enemyCount; i++) {
                        enemies[i].move();
                    }

                    if (grid[y][x] == 1) {
                        dx = dy = 0;
                        // Count area before filling
                        int capturedArea = countCapturedArea(enemies);
                        for (int i = 0; i < enemyCount; i++) {
                            drop(enemies[i].y / TS, enemies[i].x / TS);
                        }
                        for (int i = 0; i < M; i++) {
                            for (int j = 0; j < N; j++) {
                                if (grid[i][j] == -1) grid[i][j] = 0;
                                else grid[i][j] = 1;
                            }
                        }
                        score += capturedArea; // Add captured area to score
                    }

                    for (int i = 0; i < enemyCount; i++) {
                        if (x / TS >= 0 && x / TS < N && y / TS >= 0 && y / TS < M && grid[enemies[i].y / TS][enemies[i].x / TS] == 2) game = false;
                    }
                }

                // Render
                gc.clearRect(0, 0, N * TS, M * TS);

                // Draw grid
                for (int i = 0; i < M; i++) {
                    for (int j = 0; j < N; j++) {
                        if (grid[i][j] == 0) continue;
                        if (grid[i][j] == 1) {
                            gc.drawImage(tilesImage, 0, 0, TS, TS, j * TS, i * TS, TS, TS);
                        }
                        if (grid[i][j] == 2) {
                            gc.drawImage(tilesImage, 54, 0, TS, TS, j * TS, i * TS, TS, TS);
                        }
                    }
                }

                // Draw player
                gc.drawImage(tilesImage, 36, 0, TS, TS, x * TS, y * TS, TS, TS);

                // Draw enemies
                for (int i = 0; i < enemyCount; i++) {
                    gc.save();
                    gc.translate(enemies[i].x, enemies[i].y);
                    gc.rotate(enemyRotation);
                    gc.drawImage(enemyImage, -TS / 2, -TS / 2, TS, TS);
                    gc.restore();
                }

                // Draw score
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("Arial", 20));
                gc.fillText("Score: " + score, 10, 20);
                double percentage = (double) score / totalArea * 100;
                gc.fillText(String.format("Area: %.1f%%", percentage), 10, 45);

                // Draw game over and rerun instruction
                if (!game) {
                    gc.drawImage(gameoverImage, 100, 100);
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("Arial", 24));
                    gc.fillText("Press R to Restart", 230, 300);
                }
            }
        };
        gameLoop.start();

        primaryStage.setTitle("Xonix Game!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static void drop(int y, int x) {
        if (y < 0 || y >= M || x < 0 || x >= N) return;
        if (grid[y][x] == 0) grid[y][x] = -1;
        if (y - 1 >= 0 && grid[y - 1][x] == 0) drop(y - 1, x);
        if (y + 1 < M && grid[y + 1][x] == 0) drop(y + 1, x);
        if (x - 1 >= 0 && grid[y][x - 1] == 0) drop(y, x - 1);
        if (x + 1 < N && grid[y][x + 1] == 0) drop(y, x + 1);
    }

    private void resetGame() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (i == 0 || j == 0 || i == M - 1 || j == N - 1) {
                    grid[i][j] = 1;
                } else {
                    grid[i][j] = 0;
                }
            }
        }
        x = 0;
        y = 0;
        dx = 0;
        dy = 0;
        score = 0;
        game = true;
    }

    private int countCapturedArea(Enemy[] enemies) {
        int[][] tempGrid = new int[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                tempGrid[i][j] = grid[i][j];
            }
        }
        int captured = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (tempGrid[i][j] == 0) {
                    boolean containsEnemy = false;
                    for (Enemy enemy : enemies) {
                        if (enemy != null && i == enemy.y / TS && j == enemy.x / TS) {
                            containsEnemy = true;
                            break;
                        }
                    }
                    if (!containsEnemy) {
                        floodFill(tempGrid, i, j);
                        captured += countFilled(tempGrid);
                        // Reset tempGrid for next iteration
                        for (int k = 0; k < M; k++) {
                            for (int l = 0; l < N; l++) {
                                if (tempGrid[k][l] == -1) tempGrid[k][l] = 0;
                            }
                        }
                    }
                }
            }
        }
        return captured;
    }

    private void floodFill(int[][] grid, int y, int x) {
        if (y < 0 || y >= M || x < 0 || x >= N || grid[y][x] != 0) return;
        grid[y][x] = -1;
        floodFill(grid, y - 1, x);
        floodFill(grid, y + 1, x);
        floodFill(grid, y, x - 1);
        floodFill(grid, y, x + 1);
    }

    private int countFilled(int[][] grid) {
        int count = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] == -1) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        launch(args);
    }
}