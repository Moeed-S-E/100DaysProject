package app;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private List<Bricks> bricksList = new ArrayList<>();
    private Boolean gameOver = false;
    private Boolean won = false;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Stylesheet not found. Ensure styles.css is in src/main/resources/");
        }

        // Create bricks
        double brickWidth = 83;
        double brickHeight = 30;
        int rows = 5;
        int columns = 8;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double x = j * (brickWidth + 5) + 50;
                double y = i * (brickHeight + 5) + 50;
                Bricks brick = new Bricks(x, y, brickWidth, brickHeight, 1, Color.ORANGE);
                bricksList.add(brick);
                root.getChildren().add(brick.getBrickShape());
            }
        }

        // Ball and platform
        Ball ball = new Ball(400, 300, 15, 3, -3, Color.RED);
        Platform platform = new Platform(350, 500, 150, 20, Color.BLUE);
        root.getChildren().addAll(platform.getPlatformShape(), ball.getBallShape());

        // Handle keyboard events
        scene.setOnKeyPressed(event -> {
            double moveDistance = 30;
            if (event.getCode() == KeyCode.LEFT) {
                platform.moveLeft(moveDistance);
            } else if (event.getCode() == KeyCode.RIGHT) {
                platform.moveRight(moveDistance, WIDTH);
            }
        });

        // Animation loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver || won) {
                    this.stop();
                    String message = gameOver ? "Game Over" : "You Win!";
                    Label label = new Label(message);
                    label.setPrefSize(150, 45);
                    label.setStyle("-fx-font-size: 28px; -fx-text-fill: red;");
                    label.setLayoutX(WIDTH / 2.0 - 50);
                    label.setLayoutY(HEIGHT / 2.0 - 50);
                    root.getChildren().add(label);

                    Button restartButton = new Button("Restart");
                    restartButton.setStyle(
                            "-fx-font-size: 28px; -fx-background-color: #FFA500; -fx-text-fill: white;");
                    restartButton.setPrefSize(150, 45);
                    restartButton.setLayoutX(WIDTH / 2.0 - 50);
                    restartButton.setLayoutY(HEIGHT / 2.0);
                    restartButton.setOnAction(e -> {
                        try {
                            restartGame(primaryStage);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    root.getChildren().add(restartButton);
                    return;
                }

                // Update ball position
                ball.updatePosition();

                // Boundary collisions
                ball.checkBoundaryCollision(WIDTH, HEIGHT);

                // Game over when the ball falls below the platform
                if (ball.getY() + ball.getRadius() >= HEIGHT) {
                    gameOver = true;
                }

                // Ball-platform collision
                if (ball.getY() + ball.getRadius() >= platform.getY()
                        && ball.getX() >= platform.getX()
                        && ball.getX() <= platform.getX() + platform.getWidth()
                        && ball.getDy() > 0) {
                    ball.setDy(-ball.getDy());
                    double hitPos = (ball.getX() - platform.getX()) / platform.getWidth();
                    ball.setDx((hitPos - 0.5) * 6);
                }

                // Ball-brick collision
                for (Bricks brick : bricksList) {
                    if (brick.isActive() && ball.getBallShape().getBoundsInParent().intersects(
                            brick.getBrickShape().getBoundsInParent())) {
                        brick.hit();
                        ball.setDy(-ball.getDy());
                        break;
                    }
                }

                // Check if all bricks are destroyed
                if (bricksList.stream().noneMatch(Bricks::isActive)) {
                    won = true;
                }
            }
        };

        gameLoop.start();

        primaryStage.setTitle("Brick Breaker | Java");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    private void restartGame(Stage primaryStage) throws Exception {
        bricksList.clear();
        gameOver = false;
        won = false;
        start(primaryStage);
    }
}
