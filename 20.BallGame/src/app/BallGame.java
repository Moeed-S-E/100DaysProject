package app;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BallGame extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BALL_RADIUS = 20;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_VELOCITY = -12;
    private static final double BASE_OBSTACLE_SPEED = -5;
    private static final double SPEED_INCREMENT = -1;
    private static final int OBSTACLE_WIDTH = 30;
    private static final int MIN_OBSTACLE_HEIGHT = 30;
    private static final int MAX_OBSTACLE_HEIGHT = 80;
    private static final double BASE_SPAWN_INTERVAL = 2.0; // Base average spawn interval in seconds
    private static final int GROUND_Y = HEIGHT - BALL_RADIUS;

    private double ballY = GROUND_Y;
    private double ballVelocityY = 0;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private double score = 0;
    private boolean gameOver = false;
    private boolean jumpRequested = false;
    private long lastObstacleTime = 0;
    private Random random = new Random();
    private double currentObstacleSpeed = BASE_OBSTACLE_SPEED;
    private int lastSpeedUpScore = 0;
    private Image obstacleImage = null;

    private class Obstacle {
        double x;
        double y;
        double radius;

        Obstacle(double x) {
            this.x = x;
            double height = MIN_OBSTACLE_HEIGHT + random.nextDouble() * (MAX_OBSTACLE_HEIGHT - MIN_OBSTACLE_HEIGHT);
            this.radius = height / 2;
            this.y = GROUND_Y - height;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Attempt to load obstacle image
        try {
            obstacleImage = new Image("file:obs.png");
        } catch (Exception e) {
            System.out.println("Failed to load obs.png, using rectangle fallback");
        }

        // Handle input
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && ballY >= GROUND_Y - 1 && !gameOver) {
                jumpRequested = true;
            }
            if (event.getCode() == KeyCode.SPACE && gameOver) {
                resetGame();
            }
        });

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame(now);
                renderGame(gc);
            }
        };
        timer.start();

        primaryStage.setTitle("Ball and Obstacles Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void resetGame() {
        ballY = GROUND_Y;
        ballVelocityY = 0;
        obstacles.clear();
        score = 0;
        gameOver = false;
        jumpRequested = false;
        lastObstacleTime = 0;
        currentObstacleSpeed = BASE_OBSTACLE_SPEED;
        lastSpeedUpScore = 0;
    }

    private void updateGame(long now) {
        if (gameOver) return;

        // Update ball position
        if (jumpRequested) {
            ballVelocityY = JUMP_VELOCITY;
            jumpRequested = false;
        }
        ballVelocityY += GRAVITY;
        ballY += ballVelocityY;

        // Keep ball on ground
        if (ballY > GROUND_Y) {
            ballY = GROUND_Y;
            ballVelocityY = 0;
        }

        // Calculate spawn interval based on speed (faster speed = shorter interval)
        double speedRatio = Math.abs(currentObstacleSpeed) / Math.abs(BASE_OBSTACLE_SPEED);
        double spawnInterval = BASE_SPAWN_INTERVAL / speedRatio; 
        double minInterval = spawnInterval * 0.75; 
        double maxInterval = spawnInterval * 1.25;
        double currentInterval = minInterval + random.nextDouble() * (maxInterval - minInterval);

        // Spawn obstacles
        if (now - lastObstacleTime > currentInterval * 1_000_000_000) {
            obstacles.add(new Obstacle(WIDTH));
            lastObstacleTime = now;
        }

        // Increase speed every 10 score points
        if ((int) score / 10 > lastSpeedUpScore) {
            currentObstacleSpeed += SPEED_INCREMENT;
            lastSpeedUpScore = (int) score / 10;
        }

        // Update obstacles
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obs = iterator.next();
            obs.x += currentObstacleSpeed;

            // Check collision (circle-to-circle)
            double ballCenterX = WIDTH / 4;
            double ballCenterY = ballY - BALL_RADIUS;
            double obsCenterX = obs.x + obs.radius;
            double obsCenterY = obs.y + obs.radius;
            double distance = Math.sqrt(Math.pow(ballCenterX - obsCenterX, 2) + Math.pow(ballCenterY - obsCenterY, 2));

            if (distance < BALL_RADIUS + obs.radius) {
                gameOver = true;
            }

            // Remove obstacles that are off-screen
            if (obs.x + obs.radius * 2 < 0) {
                iterator.remove();
            }
        }

        // Update score
        if (!gameOver) {
            score += 0.1;
        }
    }

    private void renderGame(GraphicsContext gc) {
        // Clear canvas
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // ground
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        // ball
        gc.setFill(Color.RED);
        gc.fillOval(WIDTH / 4 - BALL_RADIUS, ballY - BALL_RADIUS * 2, BALL_RADIUS * 2, BALL_RADIUS * 2);

        // obstacles
        for (Obstacle obs : obstacles) {
            if (obstacleImage != null && !obstacleImage.isError()) {
                gc.drawImage(obstacleImage, obs.x, obs.y, obs.radius * 2, obs.radius * 2);
            } else {
                gc.setFill(Color.GREEN);
                gc.fillRect(obs.x, obs.y, OBSTACLE_WIDTH, obs.radius * 2);
            }
        }

        // score
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + (int) score, 10, 20);


        if (gameOver) {
            gc.setFill(Color.BLACK);
            gc.fillText("Game Over! Score: " + (int) score, WIDTH / 2 - 50, HEIGHT / 2);
            gc.fillText("Press Space to Replay", WIDTH / 2 - 50, HEIGHT / 2 + 20);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}