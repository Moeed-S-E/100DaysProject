package app;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Board extends Canvas implements Runnable {
    private AnimationTimer timer;
    private Sprite upgrade;
    private Player player;
    public static final int FLOOR = Game.HEIGHT - 27;
    public static int[] floor = new int[Game.WIDTH + 1];
    public static boolean paused = true;
    public static int score = 0;
    public static Board board;

    public Board() {
        super(Game.WIDTH, Game.HEIGHT);
        board = this;
        initBoard();
    }

    private void initBoard() {
        setFocusTraversable(true);
        player = new Player("red-car.png");
        upgrade = new Sprite("upgrade.png");
        upgrade.destroy();
        genFloor(player.getX() - 100);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                run();
                draw();
            }
        };
    }

    public void start() {
        timer.start();
    }

    private void draw() {
        GraphicsContext g = getGraphicsContext2D();
        g.setFill(Color.web("#87CEEB"));
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        if (paused) {
            drawPauseScreen(g);
        } else {
            drawGame(g);
        }
    }

    private void textSize(GraphicsContext g, double size) {
        g.setFont(new Font(g.getFont().getName(), size));
    }

    private void drawString(GraphicsContext g, String text, int x, int y) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            g.fillText(lines[i], x, y);
            y += g.getFont().getSize();
        }
    }

    private void drawPauseScreen(GraphicsContext g) {
        g.setFill(Color.WHITE);
        textSize(g, 12);
        g.fillText("Score: " + score, 20, 40);
        g.fillText("Fuel: " + player.getFuel() + "%", 20, 60);
        g.fillText("High Score: " + Game.highScore, 20, 80);

        textSize(g, 30);
        player.stats(g, 20, 150, 30);

        g.fillText("Controls:", 20, 330);
        textSize(g, 20);
        drawString(g, "Left Arrow: Move Backwards\n" +
                      "Right Arrow: Move Forward\n" +
                      "Esc: Pause/Unpause", 20, 350);

    }

    private void drawGame(GraphicsContext g) {
        g.setFill(Color.web("#8B4513"));
        for (int x = 0; x < Game.WIDTH; x++) {
            g.fillRect(x, floor[x], 1, Game.HEIGHT - floor[x]);
        }

        g.setFill(Color.web("#32CD32"));
        for (int x = 0; x < Game.WIDTH; x++) {
            g.fillRect(x, floor[x] - 5, 1, 5);
        }

        g.setStroke(Color.web("#8B4513"));
        g.setLineWidth(2);
        for (int x = 0; x < Game.WIDTH; x++) {
            g.strokeLine(x, floor[x], x + 1, floor[x + 1]);
        }

        upgrade.draw(g, upgrade.getX() - player.getX() + player.getWidth() + upgrade.getWidth() / 2, upgrade.getY());
        g.setFill(Color.WHITE);
        g.fillText("Score: " + score, 20, 40);
        g.fillText("Fuel: " + player.getFuel() + "%", 20, 60);
        g.fillText("High Score: " + Game.highScore, 20, 80);

        player.draw(g);
    }

    public void handleKeyPressed(KeyEvent e) {
        KeyCode key = e.getCode();
        if (key == KeyCode.ESCAPE) {
            paused = !paused;
            GameMenu.pauseButton.setText(paused ? "Unpause" : "Pause");
        } else if (key == KeyCode.LEFT) {
            Game.keys.add(KeyCode.LEFT);
        } else if (key == KeyCode.RIGHT) {
            Game.keys.add(KeyCode.RIGHT);
        } else if (key == KeyCode.UP) {
            Game.keys.add(KeyCode.UP);
            System.out.println("Up Arrow pressed");
        }
    }

    public void handleKeyReleased(KeyEvent e) {
        KeyCode key = e.getCode();
        if (key == KeyCode.LEFT) {
            Game.keys.remove(KeyCode.LEFT);
        } else if (key == KeyCode.RIGHT) {
            Game.keys.remove(KeyCode.RIGHT);
        } else if (key == KeyCode.UP) {
            Game.keys.remove(KeyCode.UP);
            player.allowJump();
            System.out.println("Up Arrow released");
        }
    }

    private void genFloor(int x) {
        double baseSmoothness = 250.0;
        double detailSmoothness = 50.0;
        double baseHeight = 180.0;
        double detailHeight = 30.0;
        double rshift = 0.0128975331423;
        for (int i = 0; i < floor.length; i++) {
            double posX = (x + i) / baseSmoothness + rshift;
            double base = ImprovedNoise.noise(posX, 0, 0) * baseHeight;
            double detail = ImprovedNoise.noise(posX * 5, 0, 0) * detailHeight;
            floor[i] = FLOOR - (int)(base + detail) - (int)(baseHeight / 2);
        }
    }

    private void step() {
        if (paused) return;

        int prevX = player.getX();
        player.update();
        if (prevX != player.getX()) {
            genFloor(player.getX() - 100);
            if (Math.random() < 0.0001 * (player.getX() - prevX) && player.getX() - 1000 > upgrade.getX()) {
                int upgradeX = player.getX() + Game.WIDTH;
                int upgradeIndex = Math.min(Math.max(upgradeX, 0), Board.floor.length - 1);
                int upgradeY = Board.floor[upgradeIndex] - upgrade.h - 10; // Place just above road
                upgrade.set(upgradeX, upgradeY);
                System.out.println("Upgrade spawned at x=" + upgradeX + ", y=" + upgradeY);
            }
        }

        if (upgrade.colliding(player)) {
            String up = player.upgrade();
            upgrade.destroy();
            System.out.println("Upgraded " + up);
        }

        score = player.getX() / 50;

        if (player.brokenDown()) {
            upgrade.destroy();
            Game.highScore = Math.max(Game.highScore, score);
            player.restart();
            genFloor(player.getX() - 100);
        }
    }

    public void newGame() {
        player = new Player("red-car.png");
        upgrade.destroy();
        genFloor(player.getX() - 100);
        Game.highScore = 0;
    }

    @Override
    public void run() {
        step();
    }
}