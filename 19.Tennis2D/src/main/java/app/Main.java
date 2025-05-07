package app;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;


    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 80;
    private static final int PADDLE_SPEED = 10;
    private static final double AI_PADDLE_SPEED_MULTIPLIER = 0.75; 


    private static final int BALL_RADIUS = 8;


    private static final int NET_WIDTH = 4;
    private static final int NET_SEGMENT_HEIGHT = 15;
    private static final int NET_GAP_HEIGHT = 10;


    private static final double GRAVITY = 0.2;
    private static final double INITIAL_BALL_SPEED_X = 4.5; 
    private static final double INITIAL_BALL_SPEED_Y_SERVE = -7;
    private static final double PADDLE_HIT_VERTICAL_BOUNCE = -6.5;
    private static final double AI_POWER_SHOT_SPEED_MULTIPLIER = 1.6; 
    private static final double AI_POWER_SHOT_VERTICAL_MULTIPLIER = 1.1; 

    // Game elements
    private Rectangle paddle1; // Player User
    private Rectangle paddle2; // Player Computer
    private Circle ball;
    private Pane gameRoot;
    private VBox layout;

    // Score variables
    private int scorePlayer1 = 0;
    private int scoreComputer = 0;
    private Text scoreTextPlayer1;
    private Text scoreTextComputer;

    // Ball movement variables
    private double ballDX = INITIAL_BALL_SPEED_X;
    private double ballDY = INITIAL_BALL_SPEED_Y_SERVE;

    // Player 1 paddle movement flags
    private boolean movePaddle1Up = false;
    private boolean movePaddle1Down = false;

    // Game state
    private boolean ballInPlay = false; // Start with ball not in play, require serve
    private boolean aiPowerShotCharged = false;
    private Text infoText;

    @Override
    public void start(Stage stage) {
        layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        gameRoot = new Pane();
        gameRoot.setPrefSize(WIDTH, HEIGHT);
        gameRoot.setStyle("-fx-background-color: black;");

        // Score displays
        scoreTextPlayer1 = new Text("Player 1: 0");
        scoreTextPlayer1.setFont(Font.font("Monospace", 24));
        scoreTextPlayer1.setFill(Color.WHITE);

        scoreTextComputer = new Text("Computer: 0"); // Changed from Player 2
        scoreTextComputer.setFont(Font.font("Monospace", 24));
        scoreTextComputer.setFill(Color.WHITE);

        javafx.scene.layout.HBox scoreDisplayBox = new javafx.scene.layout.HBox(50);
        scoreDisplayBox.setAlignment(Pos.CENTER);
        scoreDisplayBox.getChildren().addAll(scoreTextPlayer1, scoreTextComputer);
        scoreDisplayBox.setPadding(new javafx.geometry.Insets(10));

        // Info text display
        infoText = new Text("Press SPACE to Serve");
        infoText.setFont(Font.font("Monospace", 18));
        infoText.setFill(Color.LIGHTGRAY);
        infoText.setVisible(true); 

        //score display
        layout.getChildren().addAll(scoreDisplayBox, infoText, gameRoot);
        VBox.setMargin(infoText, new javafx.geometry.Insets(5,0,5,0));


        // paddles
        paddle1 = new Rectangle(30, (HEIGHT / 2.0) - (PADDLE_HEIGHT / 2.0), PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle1.setFill(Color.WHITE);

        paddle2 = new Rectangle(WIDTH - 30 - PADDLE_WIDTH, (HEIGHT / 2.0) - (PADDLE_HEIGHT / 2.0), PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle2.setFill(Color.LIGHTBLUE); // AI paddle a different color

        // ball
        ball = new Circle(WIDTH / 2.0, HEIGHT / 2.0, BALL_RADIUS);
        ball.setFill(Color.WHITE);
        ball.setVisible(false); // Ball is initially not visible until served

        // Net
        drawNet();

        gameRoot.getChildren().addAll(paddle1, paddle2, ball);

        Scene scene = new Scene(layout, WIDTH, HEIGHT + 90); 

        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> gameLoop()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setTitle("Tennis for Two");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        
    }

    private void drawNet() {
        for (int i = 0; i < HEIGHT; i += (NET_SEGMENT_HEIGHT + NET_GAP_HEIGHT)) {
            Line segment = new Line(WIDTH / 2.0, i, WIDTH / 2.0, i + NET_SEGMENT_HEIGHT);
            segment.setStroke(Color.WHITE);
            segment.setStrokeWidth(NET_WIDTH);
            gameRoot.getChildren().add(segment);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.W) movePaddle1Up = true;
        if (event.getCode() == KeyCode.S) movePaddle1Down = true;

        if (event.getCode() == KeyCode.SPACE) {
            if (!ballInPlay) {
                ballInPlay = true;
                ball.setVisible(true);
                infoText.setVisible(false);
                if (scorePlayer1 == 0 && scoreComputer == 0) {
                    resetBall(true); // Player 1 serves first
                } else {
                     resetBall(ballDX < 0);
                }
            } else {

                if (!aiPowerShotCharged) {
                    aiPowerShotCharged = true;
                    infoText.setText("Power Shot Charged!");
                    infoText.setVisible(true);
                    // System.out.println("AI Power Shot Charged!");
                }
            }
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.W) movePaddle1Up = false;
        if (event.getCode() == KeyCode.S) movePaddle1Down = false;
    }

    private void aiPaddleControl() {
        
        double aiPaddleEffectiveSpeed = PADDLE_SPEED * AI_PADDLE_SPEED_MULTIPLIER;
        double paddle2CenterY = paddle2.getY() + PADDLE_HEIGHT / 2.0;

        
        if (ballDX > 0 || ball.getCenterX() > WIDTH / 2.0) {
            double deadZone = PADDLE_HEIGHT * 0.1; 

            if (ball.getCenterY() < paddle2CenterY - deadZone) { 
                if (paddle2.getY() > 0) {
                    paddle2.setY(paddle2.getY() - aiPaddleEffectiveSpeed);
                }
            } else if (ball.getCenterY() > paddle2CenterY + deadZone) { 
                if (paddle2.getY() < HEIGHT - PADDLE_HEIGHT) {
                    paddle2.setY(paddle2.getY() + aiPaddleEffectiveSpeed);
                }
            }
        } else {
            
            double centerPosition = (HEIGHT - PADDLE_HEIGHT) / 2.0;
            if (paddle2.getY() < centerPosition - aiPaddleEffectiveSpeed) {
                paddle2.setY(paddle2.getY() + aiPaddleEffectiveSpeed / 2); 
            } else if (paddle2.getY() > centerPosition + aiPaddleEffectiveSpeed) {
                paddle2.setY(paddle2.getY() - aiPaddleEffectiveSpeed / 2);
            }
        }
    }


    private void gameLoop() {
        if (!ballInPlay) {
            
            if (!infoText.getText().equals("Power Shot Charged!")) {
                 infoText.setText("Press SPACE to Serve");
                 infoText.setVisible(true);
                 ball.setVisible(false);
            }
            return;
        }

        // Move player's paddle
        if (movePaddle1Up && paddle1.getY() > 0) {
            paddle1.setY(paddle1.getY() - PADDLE_SPEED);
        }
        if (movePaddle1Down && paddle1.getY() < HEIGHT - PADDLE_HEIGHT) {
            paddle1.setY(paddle1.getY() + PADDLE_SPEED);
        }

        // AI controls its paddle
        aiPaddleControl();

        // Apply gravity
        ballDY += GRAVITY;

        // Move ball
        ball.setCenterX(ball.getCenterX() + ballDX);
        ball.setCenterY(ball.getCenterY() + ballDY);

        // Ball collision with top/bottom walls
        if (ball.getCenterY() + BALL_RADIUS >= HEIGHT) {
            ball.setCenterY(HEIGHT - BALL_RADIUS);
            ballDY *= -0.75;
            if (Math.abs(ballDY) < GRAVITY * 1.5) ballDY = PADDLE_HIT_VERTICAL_BOUNCE / 2.5;
        } else if (ball.getCenterY() - BALL_RADIUS <= 0) {
            ball.setCenterY(BALL_RADIUS);
            ballDY *= -0.75;
        }

        // Ball collision with paddles
        boolean hitPaddle1 = ball.getBoundsInParent().intersects(paddle1.getBoundsInParent()) && ballDX < 0;
        boolean hitPaddle2 = ball.getBoundsInParent().intersects(paddle2.getBoundsInParent()) && ballDX > 0;

        if (hitPaddle1) { // Player 1 hits
            ball.setCenterX(paddle1.getX() + PADDLE_WIDTH + BALL_RADIUS + 0.1);
            ballDX = Math.abs(INITIAL_BALL_SPEED_X);
            ballDY = PADDLE_HIT_VERTICAL_BOUNCE;
            
            if (infoText.getText().equals("AI Power Shot Charged!")) { 
                aiPowerShotCharged = false;
                infoText.setVisible(false);
            }

        } else if (hitPaddle2) { // AI hits
            ball.setCenterX(paddle2.getX() - BALL_RADIUS - 0.1);
            if (aiPowerShotCharged) {
                ballDX = -Math.abs(INITIAL_BALL_SPEED_X * AI_POWER_SHOT_SPEED_MULTIPLIER);
                ballDY = PADDLE_HIT_VERTICAL_BOUNCE * AI_POWER_SHOT_VERTICAL_MULTIPLIER;
                aiPowerShotCharged = false;
                infoText.setVisible(false); 
                // System.out.println("AI Power Shot Used!");
            } else {
                ballDX = -Math.abs(INITIAL_BALL_SPEED_X);
                ballDY = PADDLE_HIT_VERTICAL_BOUNCE;
            }
        
        }


        if (ball.getCenterX() - BALL_RADIUS < 0) {
            scoreComputer++;
            updateScoreDisplay();
            ballInPlay = false;
            
        } else if (ball.getCenterX() + BALL_RADIUS > WIDTH) {
            scorePlayer1++;
            updateScoreDisplay();
            ballInPlay = false;
            
        }
    }

    private void updateScoreDisplay() {
        scoreTextPlayer1.setText("Player 1: " + scorePlayer1);
        scoreTextComputer.setText("Computer: " + scoreComputer);
    }

   
    private void resetBall(boolean player1Serves) {
        ball.setCenterX(WIDTH / 2.0);
        ball.setCenterY(HEIGHT / 3.0 + (Math.random() * HEIGHT / 3.0)); 

        if (player1Serves) {
            ballDX = Math.abs(INITIAL_BALL_SPEED_X);
        } else {
            ballDX = -Math.abs(INITIAL_BALL_SPEED_X);
        }
        ballDY = INITIAL_BALL_SPEED_Y_SERVE;
        ballInPlay = true; 
        ball.setVisible(true);
        infoText.setVisible(false); 
    }

    public static void main(String[] args) {
        launch(args);
    }
}

