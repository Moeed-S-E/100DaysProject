package app;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.util.*;

public class OutRun extends Application {

    static final int WIDTH = 1024;
    static final int HEIGHT = 768;
    static final int ROAD_W = 2000;
    static final int SEG_L = 200;
    static final float CAM_D = 0.84f;
    static final float PLAYER_BOUND = 2.5f; // How far player can move left/right

    enum GameState { LOADING, MENU, RUNNING, PAUSED, EXIT }

    static class Line {
        float x, y, z; // 3D center
        float X, Y, W; // Screen coords
        float curve, spriteX, clip, scale;
        Image sprite;

        Line() {
            spriteX = curve = x = y = z = 0;
        }

        void project(float camX, float camY, float camZ) {
            scale = CAM_D / (z - camZ);
            X = (1 + scale * (x - camX)) * WIDTH / 2.0f;
            Y = (1 - scale * (y - camY)) * HEIGHT / 2.0f;
            W = scale * ROAD_W * WIDTH / 2.0f;
        }

        void drawSprite(GraphicsContext gc) {
            if (sprite == null) return;
            double w = sprite.getWidth();
            double h = sprite.getHeight();

            double destX = X + scale * spriteX * WIDTH / 2.0;
            double destY = Y + 4;
            double destW = w * W / 266.0;
            double destH = h * W / 266.0;

            destX += destW * spriteX;
            destY += destH * (-1);

            if (clip > 0) {
                double clipH = destY + destH - clip;
                if (clipH < 0) clipH = 0;
                if (clipH >= destH) return;
            }
            gc.drawImage(sprite, destX, destY, destW, destH);
        }
    }

    private List<Line> lines = new ArrayList<>();
    private Image[] objects = new Image[8];
    private Image bgImage;
    private Image playerCar;

    // Key state tracking
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    // Game state variables
    private GameState gameState = GameState.LOADING;
    private int score = 0;
    private boolean resourcesLoaded = false;

    // UI elements
    private Scene loadingScene, menuScene, gameScene, pauseScene, exitScene;
    private Label scoreLabel = new Label("Score: 0");
    private Label pauseLabel = new Label("Game Paused\nPress P to Resume\nESC for Menu");
    private AnimationTimer gameLoop;

    // Game logic variables
    private int pos = 0;
    private float playerX = 0;
    private int H = 1500;
    private int speed = 0;

    @Override
    public void start(Stage stage) {
        // LOADING SCENE
        VBox loadingBox = new VBox(20, new Label("Loading..."));
        loadingBox.setAlignment(Pos.CENTER);
        loadingScene = new Scene(loadingBox, WIDTH, HEIGHT);

        // MENU SCENE
        Label title = new Label("OutRun");
        title.setFont(Font.font(48));
        Button startBtn = new Button("Start Game");
        Button exitBtn = new Button("Exit");
        VBox menuBox = new VBox(30, title, startBtn, exitBtn);
        menuBox.setAlignment(Pos.CENTER);
        menuScene = new Scene(menuBox, WIDTH, HEIGHT);

        // PAUSE SCENE
        pauseLabel.setFont(Font.font(36));
        VBox pauseBox = new VBox(30, pauseLabel);
        pauseBox.setAlignment(Pos.CENTER);
        pauseScene = new Scene(pauseBox, WIDTH, HEIGHT);

        // EXIT SCENE
        Label exitMsg = new Label("Thanks for playing!\nPress ENTER to return to Menu or ESC to Exit.");
        exitMsg.setFont(Font.font(28));
        VBox exitBox = new VBox(30, exitMsg);
        exitBox.setAlignment(Pos.CENTER);
        exitScene = new Scene(exitBox, WIDTH, HEIGHT);

        // GAME SCENE
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        scoreLabel.setFont(Font.font(24));
        scoreLabel.setTextFill(Color.YELLOW);
        HBox hud = new HBox(30, scoreLabel);
        hud.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        hud.setPrefHeight(40);
        hud.setAlignment(Pos.CENTER_LEFT);
        VBox gameLayout = new VBox(hud, canvas);
        gameScene = new Scene(gameLayout, WIDTH, HEIGHT);

        // Handle menu buttons
        startBtn.setOnAction(e -> {
            resetGame();
            stage.setScene(gameScene);
            gameState = GameState.RUNNING;
            canvas.requestFocus();
        });
        exitBtn.setOnAction(e -> {
            gameState = GameState.EXIT;
            stage.setScene(exitScene);
        });

        // PAUSE/RESUME/EXIT
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P && gameState == GameState.RUNNING) {
                gameState = GameState.PAUSED;
                stage.setScene(pauseScene);
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                gameState = GameState.MENU;
                stage.setScene(menuScene);
            }
            pressedKeys.add(e.getCode());
        });
        gameScene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        pauseScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                gameState = GameState.RUNNING;
                stage.setScene(gameScene);
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                gameState = GameState.MENU;
                stage.setScene(menuScene);
            }
        });

        exitScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                gameState = GameState.MENU;
                stage.setScene(menuScene);
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        // Show loading scene
        stage.setScene(loadingScene);
        stage.setTitle("OutRun");
        stage.show();

        // Load resources asynchronously
        new Thread(() -> {
            loadResources();
            Platform.runLater(() -> {
                resourcesLoaded = true;
                stage.setScene(menuScene);
                gameState = GameState.MENU;
            });
        }).start();

        // Main game loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameState != GameState.RUNNING) return;

                // Input (add left/right movement)
                speed = 0;
                if (pressedKeys.contains(KeyCode.UP)) speed = 200;
                if (pressedKeys.contains(KeyCode.DOWN)) speed = -200;
                if (pressedKeys.contains(KeyCode.RIGHT)) playerX += 0.5f;
                if (pressedKeys.contains(KeyCode.LEFT)) playerX -= 0.5f;
                if (pressedKeys.contains(KeyCode.W)) H += 100;
                if (pressedKeys.contains(KeyCode.S)) H -= 100;

                pos += speed;
                int N = lines.size();
                while (pos >= N * SEG_L) pos -= N * SEG_L;
                while (pos < 0) pos += N * SEG_L;

                // Drawing
                gc.setFill(Color.rgb(105, 205, 4));
                gc.fillRect(0, 0, WIDTH, HEIGHT);

                // Draw background
                gc.drawImage(bgImage, 0, 0, WIDTH, HEIGHT);

                int startPos = pos / SEG_L;
                int camH = (int) lines.get(startPos).y + H;
                int maxy = HEIGHT;
                float x = 0, dx = 0;

                // Draw road
                for (int n = startPos; n < startPos + 300; n++) {
                    Line l = lines.get(n % N);
                    l.project(playerX * ROAD_W, camH, pos);
                    x += dx;
                    dx += l.curve;
                    l.clip = maxy;
                    if (l.Y >= maxy) continue;
                    maxy = (int) l.Y;

                    Color grass = (n / 3) % 2 == 0 ? Color.rgb(16, 200, 16) : Color.rgb(0, 154, 0);
                    Color rumble = (n / 3) % 2 == 0 ? Color.WHITE : Color.BLACK;
                    Color road = (n / 3) % 2 == 0 ? Color.rgb(107, 107, 107) : Color.rgb(105, 105, 105);

                    Line p = lines.get((n - 1) % N);
                    drawQuad(gc, grass, 0, p.Y, WIDTH, 0, l.Y, WIDTH);
                    drawQuad(gc, rumble, p.X, p.Y, p.W * 1.2f, l.X, l.Y, l.W * 1.2f);
                    drawQuad(gc, road, p.X, p.Y, p.W, l.X, l.Y, l.W);
                }

                for (int n = startPos + 300; n > startPos; n--) {
                    lines.get(n % N).drawSprite(gc);
                }

                if (playerCar != null) {
                    Line bottomLine = lines.get((startPos + 299) % N);
                    double carW = playerCar.getWidth();
                    double carH = playerCar.getHeight();
                    double scale = bottomLine.W / 266.0;
                    double carScaleMultiplier = 5.0;
                    double carDrawW = carW * scale * carScaleMultiplier;
                    double carDrawH = carH * scale * carScaleMultiplier;


                    double maxPlayerX = ((bottomLine.W - carDrawW / 2.0) / (ROAD_W / 2.0));
                    playerX = (float) Math.max(-maxPlayerX, Math.min(maxPlayerX, playerX));

                    
                    double carX = (WIDTH / 2.0) + (playerX * ROAD_W * scale / 2.0) - (carDrawW / 2.0);
                    double carY = HEIGHT - carDrawH - 20; 
                    gc.drawImage(playerCar, carX, carY, carDrawW, carDrawH);
                }

                
                if (speed > 0) score += speed / 10;
                scoreLabel.setText("Score: " + score);
            }
        };

        gameLoop.start();
    }

    private void resetGame() {
        pos = 0;
        playerX = 0;
        H = 1500;
        score = 0;
        pressedKeys.clear();
    }

    private void loadResources() {
        
        for (int i = 1; i <= 7; i++) {
            objects[i] = new Image(getClass().getResourceAsStream("/images/" + i + ".png"));
        }
        bgImage = new Image(getClass().getResourceAsStream("/images/bg.png"));
        playerCar = new Image(getClass().getResourceAsStream("/images/car.png"));

        
        lines.clear();
        for (int i = 0; i < 1600; i++) {
            Line line = new Line();
            line.z = i * SEG_L;
            if (i > 300 && i < 700) line.curve = 0.5f;
            if (i > 1100) line.curve = -0.7f;
            if (i < 300 && i % 20 == 0) { line.spriteX = -2.5f; line.sprite = objects[5]; }
            if (i % 17 == 0) { line.spriteX = 2.0f; line.sprite = objects[6]; }
            if (i > 300 && i % 20 == 0) { line.spriteX = -0.7f; line.sprite = objects[4]; }
            if (i > 800 && i % 20 == 0) { line.spriteX = -1.2f; line.sprite = objects[1]; }
            if (i == 400) { line.spriteX = -1.2f; line.sprite = objects[7]; }
            if (i > 750) line.y = (float) (Math.sin(i / 30.0) * 1500);

            lines.add(line);
        }
    }

    private void drawQuad(GraphicsContext gc, Color color, double x1, double y1, double w1, double x2, double y2, double w2) {
        gc.setFill(color);
        gc.fillPolygon(
                new double[]{x1 - w1, x2 - w2, x2 + w2, x1 + w1},
                new double[]{y1, y2, y2, y1},
                4
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
