package app;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    private GameConfig gameConfig = new GameConfig();
    private Image background, gameOverImg, icon, restart, title;
    private Font scoreFont;
    private Bird bird;
    private Floor floor;
    private List<Pipe> pipes;
    private int score;
    private boolean gameStarted, gameOver;
    private AnimationTimer timer;
    private long lastFrameTime;
    private int frameCount;

    @Override
    public void start(Stage stage) throws Exception {
        // Load assets
        background = new Image("images/background.png");
        gameOverImg = new Image("images/game_over.png");
        icon = new Image("images/icon.png");
        restart = new Image("images/restart.png");
        title = new Image("images/title.png");
        scoreFont = new Font("Arial",40);

        // Initialize game objects
        bird = new Bird(gameConfig.WIDTH / 3, gameConfig.HEIGHT / 2,
                new Image("images/bird1.png"), new Image("images/bird2.png"), new Image("images/bird3.png"), gameConfig);
        floor = new Floor(gameConfig.HEIGHT, new Image("images/floor.png"));
        pipes = new ArrayList<>();
        score = 0;
        gameStarted = false;
        gameOver = false;
        frameCount = 0;

        Canvas canvas = new Canvas(gameConfig.WIDTH, gameConfig.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, gameConfig.WIDTH, gameConfig.HEIGHT);

        // Input event handling
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (!gameStarted) {
                    gameStarted = true;
                }
                if (!gameOver) {
                    bird.jump();
                }
            }
            if (event.getCode() == KeyCode.R && gameOver) {
                resetGame();
            }
        });

        // Mouse click handling for restart image
        scene.setOnMouseClicked(event -> {
            if (gameOver) {
                double restartX = gameConfig.WIDTH / 2 - restart.getWidth() / 2;
                double restartY = gameConfig.HEIGHT / 2 + gameOverImg.getHeight() / 2 + 20;
                double mouseX = event.getX();
                double mouseY = event.getY();
                if (mouseX >= restartX && mouseX <= restartX + restart.getWidth() &&
                    mouseY >= restartY && mouseY <= restartY + restart.getHeight()) {
                    resetGame();
                }
            }
        });

        // Main game loop
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }
                double delta = (now - lastFrameTime) / 1e9;
                lastFrameTime = now;
                update(delta);
                render(gc);
            }
        };
        timer.start();

        stage.setTitle("Flappy Bird");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void render(GraphicsContext gc) {
        gc.drawImage(background, 0, 0, gameConfig.WIDTH, gameConfig.HEIGHT);
        for (Pipe pipe : pipes) {
            pipe.render(gc);
        }
        floor.render(gc);
        bird.render(gc);

        gc.setFont(scoreFont);
        gc.setFill(Color.WHITE);
        if (!gameStarted) {
            gc.drawImage(title, gameConfig.WIDTH / 2 - title.getWidth() / 2, gameConfig.HEIGHT / 4);
        } else {
            gc.fillText(String.valueOf(score), gameConfig.WIDTH / 2, 100);
        }

        if (gameOver) {
            gc.drawImage(gameOverImg, gameConfig.WIDTH / 2 - gameOverImg.getWidth() / 2, gameConfig.HEIGHT / 2 - gameOverImg.getHeight() / 2);
            gc.drawImage(restart, gameConfig.WIDTH / 2 - restart.getWidth() / 2, gameConfig.HEIGHT / 2 + gameOverImg.getHeight() / 2 + 20);
        }
    }

    private void update(double delta) {
        if (!gameStarted || gameOver) {
            return;
        }
        frameCount++;
        bird.update(delta, gameConfig);
        floor.update(delta, gameConfig);

        // Spawn pipes
        if (frameCount % 100 == 0) {
            pipes.add(new Pipe(gameConfig.WIDTH, new Image("images/up_pipe.png"), new Image("images/down_pipe.png"), gameConfig));
        }

        // Update pipes and collision detection
        for (Pipe pipe : new ArrayList<>(pipes)) {
            pipe.update(delta, gameConfig);
            if (pipe.isOffScreen()) {
                pipes.remove(pipe);
                continue;
            }
            if (bird.collidesWith(pipe)) {
                gameOver = true;
            }
            if (!pipe.isScored() && pipe.hasPassed(bird.getX())) {
                score++;
                pipe.setScored(true);
            }
        }

        // Check ground collisions
        if (bird.getY() + bird.getHeight() * 0.6 > gameConfig.HEIGHT - floor.getHeight() || bird.getY() < 0) {
            gameOver = true;
        }
    }

    private void resetGame() {
        bird = new Bird(gameConfig.WIDTH / 3, gameConfig.HEIGHT / 2,
                new Image("images/bird1.png"), new Image("images/bird2.png"), new Image("images/bird3.png"), gameConfig);
        floor = new Floor(gameConfig.HEIGHT, new Image("images/floor.png"));
        pipes.clear();
        score = 0;
        gameStarted = false;
        gameOver = false;
        frameCount = 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}