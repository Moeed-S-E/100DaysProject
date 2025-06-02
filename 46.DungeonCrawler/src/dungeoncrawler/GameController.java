package dungeoncrawler;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.*;

public class GameController {
    @FXML private GridPane gameGrid;
    @FXML private Label statusLabel;

    private static final int SIZE = 10;
    private GameCell[][] cells = new GameCell[SIZE][SIZE];
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Treasure> treasures = new ArrayList<>();
    private int score = 0;
    private boolean gameOver = false;
    private DungeonCrawler mainApp;

    public void setMainApp(DungeonCrawler app) {
        this.mainApp = app;
    }
    @FXML
    public void initialize() {
        // Build grid
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                GameCell cell = new GameCell();
                cells[y][x] = cell;
                gameGrid.add(cell, x, y);
            }
        }
        // Place player
        player = new Player(0, 0);
        cells[0][0].setEntity(player);

        // Place treasures
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            int tx, ty;
            do {
                tx = rand.nextInt(SIZE);
                ty = rand.nextInt(SIZE);
            } while (!cells[ty][tx].isEmpty());
            Treasure t = new Treasure(tx, ty);
            treasures.add(t);
            cells[ty][tx].setEntity(t);
        }

        // Place enemies
        for (int i = 0; i < 3; i++) {
            int ex, ey;
            do {
                ex = rand.nextInt(SIZE);
                ey = rand.nextInt(SIZE);
            } while (!cells[ey][ex].isEmpty());
            Enemy e = new Enemy(ex, ey);
            enemies.add(e);
            cells[ey][ex].setEntity(e);
        }

        updateStatus();

        // Handle key events
//        gameGrid.getScene().setOnKeyPressed(e -> handleKey(e.getCode()));
        
        gameGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> handleKey(e.getCode()));
            }
        });

        
        // If scene is not ready yet, add listener
        gameGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(ev -> handleKey(ev.getCode()));
            }
        });

        // Enemy movement timer
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.7), e -> moveEnemies()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleKey(KeyCode code) {
        if (gameOver) return;
        int dx = 0, dy = 0;
        switch (code) {
            case UP: dy = -1; break;
            case DOWN: dy = 1; break;
            case LEFT: dx = -1; break;
            case RIGHT: dx = 1; break;
            default: return;
        }
        int nx = player.x + dx;
        int ny = player.y + dy;
        if (nx < 0 || ny < 0 || nx >= SIZE || ny >= SIZE) return;
        if (cells[ny][nx].getEntity() instanceof Enemy) {
            gameOver("You were caught by an enemy!");
            return;
        }
        if (cells[ny][nx].getEntity() instanceof Treasure) {
            score += 10;
            treasures.remove(cells[ny][nx].getEntity());
            if (treasures.isEmpty()) {
                winGame();
                return;
            }
        }
        cells[player.y][player.x].setEntity(null);
        player.x = nx; player.y = ny;
        cells[ny][nx].setEntity(player);
        updateStatus();
    }

    private void moveEnemies() {
        if (gameOver) return;
        Random rand = new Random();
        for (Enemy e : enemies) {
            int[] dx = {0, 1, 0, -1}, dy = {-1, 0, 1, 0};
            List<Integer> dirs = Arrays.asList(0, 1, 2, 3);
            Collections.shuffle(dirs);
            for (int dir : dirs) {
                int nx = e.x + dx[dir], ny = e.y + dy[dir];
                if (nx >= 0 && ny >= 0 && nx < SIZE && ny < SIZE && cells[ny][nx].isEmpty()) {
                    cells[e.y][e.x].setEntity(null);
                    e.x = nx; e.y = ny;
                    cells[ny][nx].setEntity(e);
                    break;
                }
            }
            // Check collision with player
            if (e.x == player.x && e.y == player.y) {
                gameOver("You were caught by an enemy!");
                return;
            }
        }
    }

    private void updateStatus() {
        statusLabel.setText("Score: " + score + " | Treasures left: " + treasures.size());
    }

    private void gameOver(String msg) {
        gameOver = true;
        statusLabel.setText(msg + " Final Score: " + score);
    }

  
    private void winGame() {
        gameOver = true;
        statusLabel.setText("You win! Final Score: " + score);
        // Show win scene after a short delay
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(1000); // Optional: small pause before showing win screen
                mainApp.showWinScene(score);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
