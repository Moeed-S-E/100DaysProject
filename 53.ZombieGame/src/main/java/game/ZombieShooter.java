package game;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.util.*;

public class ZombieShooter extends Application {

    static final int WIDTH = 1280;
    static final int HEIGHT = 720;

    Canvas canvas;
    GraphicsContext gc;

    // Player state
    double px = WIDTH / 2, py = HEIGHT / 2;
    double psize = 20;
    double pmx = 0, pmy = 0;
    double speed = 3;
    int lives = 3;
    int zombiesKilled = 0;
    int score = 0;
    double mouseX = WIDTH / 2, mouseY = HEIGHT / 2;
    boolean firing = false;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Zombie> zombies = new ArrayList<>();
    boolean gameover = false;

    // For input and restart
    Set<KeyCode> keys = new HashSet<>();

    // For loading/menu/game panes
    StackPane splashPane;
    VBox menuPane;
    StackPane mainPane;

    // Difficulty
    enum Difficulty { EASY, NORMAL, HARD }
    Difficulty difficulty = Difficulty.NORMAL;
    double zombieSpeedMin = 0.4, zombieSpeedMax = 0.7;

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Splash/loading screen (GTA style)
        splashPane = new StackPane();
        splashPane.setStyle("-fx-background-color: black;");
        Text splashText = new Text("Zombie Game\nLoading...");
        splashText.setFill(Color.YELLOW);
        splashText.setTextAlignment(TextAlignment.CENTER);
        splashText.setStyle("-fx-font-size: 54px; -fx-font-family: 'Impact'; -fx-effect: dropshadow(gaussian, yellow, 8, 0, 0, 2);");
        splashPane.getChildren().add(splashText);

        // Menu pane for difficulty selection
        menuPane = new VBox(20);
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        Text menuTitle = new Text("Select Difficulty");
        menuTitle.setFill(Color.LIME);
        menuTitle.setFont(Font.font("Impact", FontWeight.BOLD, 48));
        Button easyBtn = new Button("Easy");
        Button normalBtn = new Button("Normal");
        Button hardBtn = new Button("Hard");
        styleMenuButton(easyBtn);
        styleMenuButton(normalBtn);
        styleMenuButton(hardBtn);
        menuPane.getChildren().addAll(menuTitle, easyBtn, normalBtn, hardBtn);
        menuPane.setVisible(false);

        // Main game pane
        mainPane = new StackPane(canvas);
        mainPane.setVisible(false);

        StackPane root = new StackPane(splashPane, menuPane, mainPane);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Key input
        scene.setOnKeyPressed(e -> {
            keys.add(e.getCode());
            if (mainPane.isVisible() && gameover && e.getCode() == KeyCode.ENTER) {
                restartGame();
            }
        });
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));

        // Mouse input
        scene.setOnMouseMoved(e -> { mouseX = e.getX(); mouseY = e.getY(); });
        scene.setOnMousePressed(e -> firing = true);
        scene.setOnMouseReleased(e -> firing = false);

        // Window resize
        stage.widthProperty().addListener((obs, oldVal, newVal) -> canvas.setWidth(newVal.doubleValue()));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> canvas.setHeight(newVal.doubleValue()));

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                if (mainPane.isVisible()) {
                    if (last == 0) last = now;
                    double dt = (now - last) / 1e9;
                    last = now;
                    update(keys, dt);
                    render();
                }
            }
        };
        timer.start();

        stage.setTitle("Zombie Game");
        stage.setScene(scene);
        stage.show();

        // Show splash, then fade out and show menu
        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), splashPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2.0));
        fade.setOnFinished(evt -> {
            splashPane.setVisible(false);
            menuPane.setVisible(true);
        });
        fade.play();

        // Difficulty button actions
        easyBtn.setOnAction(e -> {
            difficulty = Difficulty.EASY;
            zombieSpeedMin = 0.2; zombieSpeedMax = 0.35;
            menuPane.setVisible(false);
            mainPane.setVisible(true);
            restartGame();
        });
        normalBtn.setOnAction(e -> {
            difficulty = Difficulty.NORMAL;
            zombieSpeedMin = 0.4; zombieSpeedMax = 0.7;
            menuPane.setVisible(false);
            mainPane.setVisible(true);
            restartGame();
        });
        hardBtn.setOnAction(e -> {
            difficulty = Difficulty.HARD;
            zombieSpeedMin = 0.8; zombieSpeedMax = 1.2;
            menuPane.setVisible(false);
            mainPane.setVisible(true);
            restartGame();
        });
    }

    void styleMenuButton(Button btn) {
        btn.setPrefWidth(220);
        btn.setPrefHeight(60);
        btn.setFont(Font.font("Impact", FontWeight.BOLD, 32));
        btn.setStyle("-fx-background-color: #222; -fx-text-fill: #fff; -fx-background-radius: 10;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #444; -fx-text-fill: #fff; -fx-background-radius: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #222; -fx-text-fill: #fff; -fx-background-radius: 10;"));
    }

    void restartGame() {
        px = WIDTH / 2;
        py = HEIGHT / 2;
        lives = 3;
        zombiesKilled = 0;
        score = 0;
        bullets.clear();
        zombies.clear();
        for (int i = 0; i < 10; i++) zombies.add(new Zombie());
        gameover = false;
        keys.clear();
    }

    void update(Set<KeyCode> keys, double dt) {
        if (gameover) return;

        // Movement
        pmx = 0; pmy = 0;
        if (keys.contains(KeyCode.A) || keys.contains(KeyCode.LEFT)) pmx = -1;
        if (keys.contains(KeyCode.D) || keys.contains(KeyCode.RIGHT)) pmx = 1;
        if (keys.contains(KeyCode.W) || keys.contains(KeyCode.UP)) pmy = -1;
        if (keys.contains(KeyCode.S) || keys.contains(KeyCode.DOWN)) pmy = 1;
        double len = Math.hypot(pmx, pmy);
        if (len > 0) { pmx /= len; pmy /= len; }
        px += pmx * speed;
        py += pmy * speed;

        // Shooting
        if (firing && bullets.size() < 10) {
            double angle = Math.atan2(mouseY - py, mouseX - px);
            bullets.add(new Bullet(px + Math.cos(angle) * psize, py + Math.sin(angle) * psize, angle));
        }

        // Update bullets
        for (Bullet b : bullets) b.update();
        bullets.removeIf(b -> b.x < 0 || b.x > canvas.getWidth() || b.y < 0 || b.y > canvas.getHeight() || b.hit);

        // Update zombies
        for (Zombie z : zombies) z.update();
        // Bullet-zombie collision
        for (Bullet b : bullets) {
            for (Zombie z : zombies) {
                if (!b.hit && z.alive && Math.hypot(b.x - z.x, b.y - z.y) < z.size) {
                    b.hit = true;
                    z.alive = false;
                    zombiesKilled++;
                    score += 10; // 10 points per kill
                }
            }
        }
        // Respawn dead zombies
        for (int i = 0; i < zombies.size(); i++) {
            if (!zombies.get(i).alive) zombies.set(i, new Zombie());
        }
        // Zombie-player collision
        for (Zombie z : zombies) {
            if (z.alive && Math.hypot(px - z.x, py - z.y) < z.size + psize) {
                lives--;
                if (lives < 0) gameover = true;
                z.alive = false;
            }
        }
    }

    void render() {
        // Background: radial gradient
        Stop[] stops = new Stop[] { new Stop(0, Color.rgb(150,200,150)), new Stop(1, Color.rgb(50,100,50)) };
        RadialGradient bg = new RadialGradient(0, 0, canvas.getWidth()/2, canvas.getHeight()/2, Math.max(canvas.getWidth(), canvas.getHeight())/1.5, false, CycleMethod.NO_CYCLE, stops);
        gc.setFill(bg);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw grass (optional, for effect)
        gc.setStroke(Color.rgb(0,80,0));
        for (int i = 0; i < 100; i++) {
            double x = Math.random() * canvas.getWidth();
            double y = Math.random() * canvas.getHeight();
            gc.strokeLine(x, y, x + Math.random()*6 - 3, y - Math.random()*10);
        }

        // Draw player
        gc.setFill(Color.SANDYBROWN);
        gc.fillOval(px-psize, py-psize, psize*2, psize*2);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(px-psize, py-psize, psize*2, psize*2);

        // Draw gun
        double angle = Math.atan2(mouseY - py, mouseX - px);
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(8);
        gc.strokeLine(px, py, px + Math.cos(angle)*psize*1.5, py + Math.sin(angle)*psize*1.5);

        // Draw bullets
        gc.setFill(Color.BLACK);
        for (Bullet b : bullets)
            gc.fillOval(b.x-5, b.y-5, 10, 10);

        // Draw zombies
        for (Zombie z : zombies) {
            if (z.alive) {
                gc.setFill(Color.DARKGREEN);
                gc.fillOval(z.x-z.size, z.y-z.size, z.size*2, z.size*2);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(z.x-z.size, z.y-z.size, z.size*2, z.size*2);
            }
        }

        // HUD
        gc.setFill(Color.RED);
        for (int i = 0; i < lives; i++)
            gc.fillOval(20 + i*30, 20, 20, 20);

        gc.setFill(Color.WHEAT);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText("Zombies killed: " + zombiesKilled, 20, 60);
        gc.fillText("Score: " + score, 20, 90);

        // Crosshair
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeOval(mouseX-5, mouseY-5, 10, 10);

        // Game over
        if (gameover) {
            gc.setFill(Color.rgb(0,0,0,0.5));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Impact", FontWeight.BOLD, 60));
            gc.fillText("YOU DIED! " + zombiesKilled + " Zombies Killed", canvas.getWidth()/2-300, canvas.getHeight()/2);
            gc.setFont(Font.font("Impact", FontWeight.BOLD, 30));
            gc.fillText("Score: " + score, canvas.getWidth()/2-60, canvas.getHeight()/2 + 40);
            gc.fillText("Press ENTER to restart", canvas.getWidth()/2-150, canvas.getHeight()/2 + 100);
        }
    }

    class Bullet {
        double x, y, angle;
        double speed = 10;
        boolean hit = false;
        Bullet(double x, double y, double angle) {
            this.x = x; this.y = y; this.angle = angle;
        }
        void update() {
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        }
    }

    class Zombie {
        double x, y, size, speed;
        boolean alive = true;
        Zombie() {
            size = 20 + Math.random()*10;
            // Spawn at random edge
            if (Math.random() < 0.5) {
                x = Math.random() < 0.5 ? -size : canvas.getWidth()+size;
                y = Math.random() * canvas.getHeight();
            } else {
                y = Math.random() < 0.5 ? -size : canvas.getHeight()+size;
                x = Math.random() * canvas.getWidth();
            }
            speed = zombieSpeedMin + Math.random()*(zombieSpeedMax-zombieSpeedMin); // Difficulty-dependent speed
        }
        void update() {
            if (!alive) return;
            double dx = px - x, dy = py - y;
            double dist = Math.hypot(dx, dy);
            if (dist > 0) {
                x += dx / dist * speed;
                y += dy / dist * speed;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
