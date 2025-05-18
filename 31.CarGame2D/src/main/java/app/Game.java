package app;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Game extends Application {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int CAR_WIDTH = 50;
    private static final int CAR_HEIGHT = 80;
    private static final int ROAD_LANE_COUNT = 4;
    private static final int ROAD_LANE_WIDTH = WIDTH / ROAD_LANE_COUNT;
    private static final int MAX_OBSTACLES = 8;
    private static final int MIN_OBSTACLES = 5;

    private double carX = (ROAD_LANE_WIDTH - CAR_WIDTH) / 2 + ROAD_LANE_WIDTH;
    private double carY = HEIGHT - 150;
    private ArrayList<Car> obstacles;
    private int score;
    private boolean gameOver;
    private Random random;
    private int speed;
    private int obstacleSpawnRate;
    private int obstaclesSpawned;
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;
    private boolean difficultyIncreased;
    private boolean difficultyIncreased2;
    private Image userCar;
    private Image[] obstacleCars;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        initializeGame();

        // Load images
        userCar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car1.png")));
        obstacleCars = new Image[]{
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car2.png"))),
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car3.png"))),
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car4.png"))),
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car5.png"))),
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car6.png"))),
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/car7.png")))
        };

        
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            }
            if (event.getCode() == KeyCode.UP) {
                moveUp = true;
            }
            if (event.getCode() == KeyCode.DOWN) {
                moveDown = true;
            }
            if (event.getCode() == KeyCode.R && gameOver) {
                initializeGame();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                moveLeft = false;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                moveRight = false;
            }
            if (event.getCode() == KeyCode.UP) {
                moveUp = false;
            }
            if (event.getCode() == KeyCode.DOWN) {
                moveDown = false;
            }
        });

        stage.setTitle("2D Car Game - JavaFX");
        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };
        timer.start();
    }

    private void initializeGame() {
        obstacles = new ArrayList<>();
        score = 0;
        gameOver = false;
        random = new Random();
        speed = 5;
        obstacleSpawnRate = 20;
        obstaclesSpawned = 0;
        moveLeft = false;
        moveRight = false;
        moveUp = false;
        moveDown = false;
        difficultyIncreased = false;
        difficultyIncreased2 = false;
        carX = (ROAD_LANE_WIDTH - CAR_WIDTH) / 2 + ROAD_LANE_WIDTH;
        carY = HEIGHT - 150;
    }

    private void update() {
        if (!gameOver) {
            
            if (moveLeft && carX >= 0) {
                carX -= 5;
            }
            if (moveRight && carX <= WIDTH - CAR_WIDTH) {
                carX += 5;
            }
            if (moveUp && carY >= 0) {
                carY -= 5;
            }
            if (moveDown && carY <= HEIGHT - CAR_HEIGHT) {
                carY += 5;
            }

            
            for (int i = 0; i < obstacles.size(); i++) {
                Car obs = obstacles.get(i);
                obs.y += speed;
                if (obs.y > HEIGHT) {
                    obstacles.remove(i);
                    i--;
                }
            }

            
            if ((random.nextInt(obstacleSpawnRate) == 0 || obstacles.size() < MIN_OBSTACLES) && obstacles.size() < MAX_OBSTACLES) {
                // Ensure no overlap with the last obstacle
                if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).y > CAR_HEIGHT * 2.5) {
                    int lane = random.nextInt(ROAD_LANE_COUNT);
                    double obsX = lane * ROAD_LANE_WIDTH + (ROAD_LANE_WIDTH - CAR_WIDTH) / 2;
                    Image obsImage = obstacleCars[random.nextInt(obstacleCars.length)];
                    obstacles.add(new Car(obsX, -CAR_HEIGHT, obsImage));
                    obstaclesSpawned++;
                }
            }

            
            for (Car obs : obstacles) {
                if (carX < obs.x + CAR_WIDTH && carX + CAR_WIDTH > obs.x && carY < obs.y + CAR_HEIGHT && carY + CAR_HEIGHT > obs.y) {
                    gameOver = true;
                }
            }


            score++;


            if (score >= 200 && !difficultyIncreased) {
                speed *= 2; 
                obstacleSpawnRate = Math.max(obstacleSpawnRate / 2, 1); 
                difficultyIncreased = true;
            }

            
            if (score >= 1000 && !difficultyIncreased2) {
                speed = 15; 
                obstacleSpawnRate = Math.max(obstacleSpawnRate / 2, 1); 
                difficultyIncreased2 = true;
            }
        }
    }

    private void draw(GraphicsContext gc) {
        
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        
        gc.setFill(Color.rgb(98, 112, 117));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        for (int i = 1; i < ROAD_LANE_COUNT; i++) {
            double laneX = i * ROAD_LANE_WIDTH;
            for (int j = 0; j < HEIGHT; j += 40) {
                gc.strokeLine(laneX, j, laneX, j + 20);
            }
        }

        
        gc.drawImage(userCar, carX, carY, CAR_WIDTH, CAR_HEIGHT);

        // Draw obstacles
        for (Car obs : obstacles) {
            gc.drawImage(obs.image, obs.x, obs.y, CAR_WIDTH, CAR_HEIGHT);
        }

        
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 30);


        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("Game Over!", WIDTH / 2 - 50, HEIGHT / 2);
            gc.fillText("Score: " + score, WIDTH / 2 - 40, HEIGHT / 2 + 20);
            gc.fillText("Press R to Replay", WIDTH / 2 - 50, HEIGHT / 2 + 40);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private static class Car {
        double x, y;
        Image image;

        Car(double x, double y, Image image) {
            this.x = x;
            this.y = y;
            this.image = image;
        }
    }
}