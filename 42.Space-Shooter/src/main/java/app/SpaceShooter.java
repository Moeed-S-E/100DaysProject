package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;

public class SpaceShooter {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Shooter");
            frame.setSize(640, 640);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            // Use CardLayout to switch between panels
            CardLayout cardLayout = new CardLayout();
            JPanel container = new JPanel(cardLayout);
            GamePanel gamePanel = new GamePanel();
            GameOverPanel gameOverPanel = new GameOverPanel(frame, gamePanel, cardLayout, container);
            container.add(gamePanel, "Game");
            container.add(gameOverPanel, "GameOver");
            gamePanel.setCardLayout(cardLayout, container);
            frame.add(container);

            frame.setVisible(true);
            gamePanel.startGameLoop();
        });
    }
}

// Utility class to track key states globally
class Keyboard {
    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    static {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            synchronized (Keyboard.class) {
                if (event.getID() == KeyEvent.KEY_PRESSED) {
                    pressedKeys.put(event.getKeyCode(), true);
                } else if (event.getID() == KeyEvent.KEY_RELEASED) {
                    pressedKeys.put(event.getKeyCode(), false);
                }
                return false;
            }
        });
    }

    public static boolean isKeyPressed(int keyCode) {
        return pressedKeys.getOrDefault(keyCode, false);
    }
}

class GamePanel extends JPanel {
    private boolean inMenu = true;
    private boolean isShooting = false;
    private int score = 0;
    private int lives = 3;
    private Player player;
    private Bullet bullet;
    private List<Enemy> enemies;
    private List<Boolean> disappearEnemies;
    private List<Long> respawnTimes;
    private long respawnDelay = 1000; // 1 second initially
    private BufferedImage backgroundImage;
    private Font neonFont;
    private Timer gameLoopTimer;
    private static final int NUM_ENEMIES = 5;
    private static final float PLAYER_SPEED = 200.0f;
    private static final float BULLET_SPEED = 400.0f;
    private static final float ENEMY_SPEED = 120.0f;
    private CardLayout cardLayout;
    private JPanel container;

    public GamePanel() {
        setBackground(Color.BLACK);
        loadAssets();
        initializeGame();
        setupInputHandlers();
    }

    public void setCardLayout(CardLayout cardLayout, JPanel container) {
        this.cardLayout = cardLayout;
        this.container = container;
    }

    private void loadAssets() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/background.png"));
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null;
        }
        try {
            neonFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/neon.otf")).deriveFont(50f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(neonFont);
        } catch (Exception e) {
            System.err.println("Error loading neon font, using fallback: " + e.getMessage());
            neonFont = new Font("Arial", Font.BOLD, 50);
        }
    }

    private void initializeGame() {
        player = new Player(getWidth() / 2 - 25, 510, "/rocket.png");
        bullet = new Bullet(-10, -10);
        enemies = new ArrayList<>();
        disappearEnemies = new ArrayList<>();
        respawnTimes = new ArrayList<>();
        int totalEnemyWidth = NUM_ENEMIES * 50 + (NUM_ENEMIES - 1) * 20;
        int startX = (getWidth() - totalEnemyWidth) / 2;
        for (int i = 0; i < NUM_ENEMIES; i++) {
            enemies.add(new Enemy(startX + i * 70, 50, "/red_enemy.png", "/blue_enemy.png"));
            disappearEnemies.add(false);
            respawnTimes.add(0L);
        }
        score = 0;
        lives = 3;
        respawnDelay = 1000;
        isShooting = false;
        inMenu = true;
    }

    public void resetGame() {
        initializeGame();
        startGameLoop();
        requestFocusInWindow();
    }

    private void setupInputHandlers() {
        // Add MouseListener to regain focus on click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                if (inMenu) {
                    Point p = e.getPoint();
                    int startGameX = getWidth() / 2 - 60;
                    int exitX = getWidth() / 2 - 30;
                    if (p.x >= startGameX && p.x <= startGameX + 120 && p.y >= 300 && p.y <= 330) {
                        inMenu = false;
                    } else if (p.x >= exitX && p.x <= exitX + 60 && p.y >= 350 && p.y <= 370) {
                        System.exit(0);
                    }
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    public void startGameLoop() {
        if (gameLoopTimer != null && gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
        gameLoopTimer = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        gameLoopTimer.start();
    }

    private void updateGame() {
        if (!inMenu) {
            long currentTime = System.currentTimeMillis();
            float deltaTime = 0.016f;

            if (score >= 100 && respawnDelay == 1000) {
                respawnDelay = 500;
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_LEFT)) {
                player.move(-PLAYER_SPEED * deltaTime, 0);
            }
            if (Keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) {
                player.move(PLAYER_SPEED * deltaTime, 0);
            }

            // Handle shooting with Keyboard class
            if (Keyboard.isKeyPressed(KeyEvent.VK_SPACE) && !isShooting) {
                isShooting = true;
                bullet.setPosition(player.getX() + player.getWidth() / 2, player.getY());
                System.out.println("Spacebar pressed, bullet fired at x=" + bullet.getX() + ", y=" + bullet.getY());
            }

            if (isShooting) {
                bullet.move(0, -BULLET_SPEED * deltaTime);
                if (bullet.getY() < 0) {
                    isShooting = false;
                    System.out.println("Bullet off-screen, isShooting reset");
                }
                for (int i = 0; i < NUM_ENEMIES; i++) {
                    if (!disappearEnemies.get(i) && bullet.intersects(enemies.get(i))) {
                        disappearEnemies.set(i, true);
                        respawnTimes.set(i, currentTime);
                        isShooting = false;
                        score += 5;
                        System.out.println("Enemy hit, score=" + score);
                    }
                }
            }

            for (int i = 0; i < NUM_ENEMIES; i++) {
                if (!disappearEnemies.get(i)) {
                    // Check for collision with player
                    if (player.intersects(enemies.get(i))) {
                        lives--;
                        disappearEnemies.set(i, true);
                        respawnTimes.set(i, currentTime);
                        player.setPosition(getWidth() / 2 - player.getWidth() / 2, 510);
                        System.out.println("Player-enemy collision, lives=" + lives);
                        if (lives <= 0) {
                            gameLoopTimer.stop();
                            for (Component comp : container.getComponents()) {
                                if (comp instanceof GameOverPanel) {
                                    ((GameOverPanel) comp).updateScore(score);
                                }
                            }
                            cardLayout.show(container, "GameOver");
                        }
                    }
                    // Check if enemy reaches player area (y >= 500)
                    else if (enemies.get(i).getY() + enemies.get(i).getHeight() >= 500) {
                        lives--;
                        disappearEnemies.set(i, true);
                        respawnTimes.set(i, currentTime);
                        player.setPosition(getWidth() / 2 - player.getWidth() / 2, 510);
                        System.out.println("Enemy reached player area, lives=" + lives);
                        if (lives <= 0) {
                            gameLoopTimer.stop();
                            for (Component comp : container.getComponents()) {
                                if (comp instanceof GameOverPanel) {
                                    ((GameOverPanel) comp).updateScore(score);
                                }
                            }
                            cardLayout.show(container, "GameOver");
                        }
                    }
                }
            }

            for (int i = 0; i < NUM_ENEMIES; i++) {
                if (!disappearEnemies.get(i)) {
                    enemies.get(i).move(0, ENEMY_SPEED * deltaTime);
                } else if (currentTime - respawnTimes.get(i) > respawnDelay) {
                    int totalEnemyWidth = NUM_ENEMIES * 50 + (NUM_ENEMIES - 1) * 20;
                    int startX = (getWidth() - totalEnemyWidth) / 2;
                    enemies.set(i, new Enemy(startX + i * 70, 50, "/red_enemy.png", "/blue_enemy.png"));
                    disappearEnemies.set(i, false);
                }
            }
        }
    }

    public int getScore() {
        return score;
    }

    public Font getNeonFont() {
        return neonFont;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (inMenu) {
            drawMenu(g2d);
        } else {
            drawGame(g2d);
        }
    }

    private void drawMenu(Graphics2D g2d) {
        g2d.setColor(new Color(0x0075FF));
        g2d.setFont(neonFont.deriveFont(50f));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "Space Shooter";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 250);

        g2d.setFont(neonFont.deriveFont(30f));
        fm = g2d.getFontMetrics();
        String startGame = "Start Game";
        String exit = "Exit";
        int startGameX = (getWidth() - fm.stringWidth(startGame)) / 2;
        int exitX = (getWidth() - fm.stringWidth(exit)) / 2;
        g2d.drawString(startGame, startGameX, 320);
        g2d.drawString(exit, exitX, 370);
    }

    private void drawGame(Graphics2D g2d) {
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        player.draw(g2d);
        if (isShooting) {
            bullet.draw(g2d);
        }
        for (int i = 0; i < NUM_ENEMIES; i++) {
            if (!disappearEnemies.get(i)) {
                enemies.get(i).draw(g2d);
            }
        }
        g2d.setColor(new Color(0x0075FF));
        g2d.setFont(neonFont.deriveFont(20f));
        FontMetrics fm = g2d.getFontMetrics();
        String scoreText = "Score: " + score;
        int scoreX = (getWidth() - fm.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, scoreX, 30);
        String livesText = "Lives: " + lives;
        int livesX = (getWidth() - fm.stringWidth(livesText)) / 2;
        g2d.drawString(livesText, livesX, 60);
    }
}

class GameOverPanel extends JPanel {
    private Font neonFont;
    private int finalScore;
    private JLabel scoreLabel;

    public GameOverPanel(JFrame parentFrame, GamePanel gamePanel, CardLayout cardLayout, JPanel container) {
        setBackground(Color.BLACK);
        neonFont = gamePanel.getNeonFont();
        finalScore = gamePanel.getScore();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Create "Game Over" label
        JLabel gameOverLabel = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLabel.setFont(neonFont.deriveFont(50f));
        gameOverLabel.setForeground(new Color(0x0075FF));
        add(gameOverLabel, gbc);

        // Create score label
        scoreLabel = new JLabel("Final Score: " + finalScore, SwingConstants.CENTER);
        scoreLabel.setFont(neonFont.deriveFont(30f));
        scoreLabel.setForeground(new Color(0x0075FF));
        add(scoreLabel, gbc);

        // Create rerun button
        JButton rerunButton = new JButton("Rerun");
        rerunButton.setFont(neonFont.deriveFont(25f));
        rerunButton.setForeground(new Color(0x0075FF));
        rerunButton.setBackground(null);
        rerunButton.setBorder(null);
        rerunButton.setFocusable(false);
        rerunButton.addActionListener(e -> {
            gamePanel.resetGame();
            cardLayout.show(container, "Game");
            gamePanel.requestFocusInWindow(); // Ensure focus after rerun
        });
        add(rerunButton, gbc);

        // Create exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(neonFont.deriveFont(25f));
        exitButton.setForeground(new Color(0x0075FF));
        exitButton.setBackground(null);
        exitButton.setBorder(null);
        exitButton.setFocusable(false);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton, gbc);
    }

    public void updateScore(int score) {
        this.finalScore = score;
        scoreLabel.setText("Final Score: " + finalScore);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
}

class Player {
    private float x, y;
    private BufferedImage image;
    private int width, height;

    public Player(float x, float y, String imagePath) {
        this.x = x;
        this.y = y;
        try {
            image = ImageIO.read(getClass().getResource(imagePath));
            width = image.getWidth() / 10;
            height = image.getHeight() / 10;
        } catch (Exception e) {
            System.err.println("Error loading player image: " + e.getMessage());
            image = null;
            width = 50;
            height = 50;
        }
    }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
        if (x < 0) x = 0;
        if (x > 640 - width) x = 640 - width;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int) x, (int) y, width, height);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean intersects(Enemy other) {
        return x < other.getX() + other.getWidth() &&
               x + width > other.getX() &&
               y < other.getY() + other.getHeight() &&
               y + height > other.getY();
    }
}

class Bullet {
    private float x, y;
    private static final int RADIUS = 5;

    public Bullet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int) x - RADIUS, (int) y - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean intersects(Enemy other) {
        return x < other.getX() + other.getWidth() &&
               x + RADIUS * 2 > other.getX() &&
               y < other.getY() + other.getHeight() &&
               y + RADIUS * 2 > other.getY();
    }
}

class Enemy {
    private float x, y;
    private BufferedImage image;
    private int width, height;
    private Color fallbackColor;
    private static final Random random = new Random();

    public Enemy(float x, float y, String redImagePath, String blueImagePath) {
        this.x = x;
        this.y = y;
        boolean useRed = random.nextBoolean();
        String imagePath = useRed ? redImagePath : blueImagePath;
        try {
            image = ImageIO.read(getClass().getResource(imagePath));
            width = image.getWidth() / 10;
            height = image.getHeight() / 10;
            fallbackColor = useRed ? Color.RED : Color.BLUE;
        } catch (Exception e) {
            System.err.println("Error loading enemy image: " + e.getMessage());
            image = null;
            width = 50;
            height = 50;
            fallbackColor = useRed ? Color.RED : Color.BLUE;
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            g2d.setColor(fallbackColor);
            g2d.fillRect((int) x, (int) y, width, height);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}