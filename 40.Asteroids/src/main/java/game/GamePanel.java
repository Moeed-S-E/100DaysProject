package game;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener {
    private List<Entity> entities;
    private Player player;
    private BufferedImage background;
    private javax.swing.Timer gameTimer;
    private Animation sExplosion, sRock, sRockSmall, sBullet, sPlayer, sPlayerGo, sExplosionShip;
    private int score;
    private static final int W = 1200, H = 800;

    public GamePanel() {
        entities = new ArrayList<>();
        score = 0; // Initialize score
        Random rand = new Random();
        // Load images with null checks
        BufferedImage t1 = GameUtils.loadImage("/images/spaceship.png");
        BufferedImage t2 = GameUtils.loadImage("/images/background.jpg");
        BufferedImage t3 = GameUtils.loadImage("/images/explosions/type_C.png");
        BufferedImage t4 = GameUtils.loadImage("/images/rock.png");
        BufferedImage t5 = GameUtils.loadImage("/images/fire_blue.png");
        BufferedImage t6 = GameUtils.loadImage("/images/rock_small.png");
        BufferedImage t7 = GameUtils.loadImage("/images/explosions/type_B.png");

        if (t2 == null) {
            System.err.println("Background image is null, game may not render correctly.");
        }
        background = t2;
        sExplosion = t3 != null ? new Animation(t3, 0, 0, 256, 256, 48, 0.5f) : null;
        sRock = t4 != null ? new Animation(t4, 0, 0, 64, 64, 16, 0.2f) : null;
        sRockSmall = t6 != null ? new Animation(t6, 0, 0, 64, 64, 16, 0.2f) : null;
        sBullet = t5 != null ? new Animation(t5, 0, 0, 32, 64, 16, 0.8f) : null;
        sPlayer = t1 != null ? new Animation(t1, 40, 0, 40, 40, 1, 0) : null;
        sPlayerGo = t1 != null ? new Animation(t1, 40, 40, 40, 40, 1, 0) : null;
        sExplosionShip = t7 != null ? new Animation(t7, 0, 0, 192, 192, 64, 0.5f) : null;

        // Initialize asteroids
        if (sRock != null) {
            for (int i = 0; i < 15; i++) {
                Asteroid a = new Asteroid();
                a.settings(sRock, rand.nextInt(W), rand.nextInt(H), rand.nextInt(360), 25);
                entities.add(a);
            }
        }
        // Initialize player
        player = new Player();
        if (sPlayer != null) {
            player.settings(sPlayer, 200, 200, 0, 20);
        }
        entities.add(player);

        // Set up game timer (60 FPS)
        gameTimer = new javax.swing.Timer(1000 / 60, this);
        gameTimer.start();

        // Keyboard input
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.setAngle(player.getAngle() + 5); // Faster turning
                if (e.getKeyCode() == KeyEvent.VK_LEFT) player.setAngle(player.getAngle() - 5); // Faster turning
                if (e.getKeyCode() == KeyEvent.VK_UP) player.setThrust(true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE && sBullet != null) {
                    Bullet b = new Bullet();
                    b.settings(sBullet, (int) player.getX(), (int) player.getY(), player.getAngle(), 10);
                    entities.add(b);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) player.setThrust(false);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (background != null) {
            g2d.drawImage(background, 0, 0, null);
        }
        for (Entity e : entities) {
            if (e.anim.getSprite() != null) {
                e.draw(g2d);
            }
        }
        // Draw score and lives
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("Lives: " + player.getLives(), 20, 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        // Update entities
        for (Entity e : entities) {
            e.update();
            e.anim.update();
        }
        // Update player animation
        if (player.thrust && sPlayerGo != null) {
            player.anim = sPlayerGo;
        } else if (sPlayer != null) {
            player.anim = sPlayer;
        }
        // Check collisions
        checkCollisions();
        // Spawn new asteroids
        spawnAsteroid();
        // Remove dead entities
        entities.removeIf(e -> !e.isAlive());
    }

    private void checkCollisions() {
        List<Entity> toAdd = new ArrayList<>();
        List<Entity> toRemove = new ArrayList<>(); // For entities to remove

        // Iterate over a copy of entities or use index-based iteration to avoid modification issues
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            for (int j = 0; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if (a.getName().equals("asteroid") && b.getName().equals("bullet")) {
                    if (GameUtils.isCollide(a, b)) {
                        toRemove.add(a);
                        toRemove.add(b);
                        // Update score
                        score += (a.getRadius() > 15) ? 100 : 50;
                        if (sExplosion != null) {
                            Entity e = new Entity() {
                                @Override
                                public void update() {}
                            };
                            e.settings(sExplosion, (int) a.getX(), (int) a.getY(), 0, 0);
                            e.name = "explosion";
                            toAdd.add(e);
                        }
                        if (a.getRadius() > 15 && sRockSmall != null) {
                            for (int k = 0; k < 2; k++) {
                                Asteroid small = new Asteroid();
                                small.settings(sRockSmall, (int) a.getX(), (int) a.getY(), (float) (Math.random() * 360), 15);
                                toAdd.add(small);
                            }
                        }
                    }
                }
                if (a.getName().equals("player") && b.getName().equals("asteroid")) {
                    if (GameUtils.isCollide(a, b)) {
                        toRemove.add(b);
                        if (sExplosionShip != null) {
                            Entity e = new Entity() {
                                @Override
                                public void update() {}
                            };
                            e.settings(sExplosionShip, (int) a.getX(), (int) a.getY(), 0, 0);
                            e.name = "explosion";
                            toAdd.add(e);
                        }
                        if (sPlayer != null) {
                            player.loseLife();
                            if (player.getLives() > 0) {
                                player.settings(sPlayer, W / 2, H / 2, 0, 20);
                                player.reset();
                            } else {
                                // Game over: show JOptionPane with score
                                gameTimer.stop(); // Pause the game
                                System.out.println("Showing game over dialog at 09:02 PM PKT, May 24, 2025");
                                try {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "Game Over! Score: " + score + "\nClick OK or press Enter to Restart",
                                        "Game Over",
                                        JOptionPane.INFORMATION_MESSAGE
                                    );
                                    System.out.println("Dialog closed, resetting game");
                                    SwingUtilities.invokeLater(() -> {
                                        try {
                                            resetGame();
                                            setFocusable(true);
                                            requestFocusInWindow();
                                            System.out.println("Focus requested for GamePanel, focus owner: " + 
                                                java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
                                            gameTimer.start();
                                            repaint();
                                            System.out.println("Game resumed");
                                        } catch (Exception ex) {
                                            System.err.println("Error during game reset: " + ex.getMessage());
                                            ex.printStackTrace();
                                        }
                                    });
                                } catch (Exception ex) {
                                    System.err.println("Error showing dialog: " + ex.getMessage());
                                    ex.printStackTrace();
                                    resetGame(); // Fallback reset
                                    setFocusable(true);
                                    requestFocusInWindow();
                                    gameTimer.start();
                                    repaint();
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply removals and additions after iteration
        entities.removeAll(toRemove);
        entities.addAll(toAdd);

        // Remove ended explosions
        for (Entity e : entities) {
            if (e.getName().equals("explosion") && e.anim.isEnd()) {
                toRemove.add(e);
            }
        }
        entities.removeAll(toRemove);
    }

    private void resetGame() {
        System.out.println("Resetting game state");
        try {
            score = 0;
            entities.clear();
            player = new Player();
            if (sPlayer != null) {
                player.settings(sPlayer, 200, 200, 0, 20);
            } else {
                System.err.println("sPlayer is null during resetGame");
            }
            entities.add(player);
            // Reinitialize asteroids
            Random rand = new Random();
            if (sRock != null) {
                for (int k = 0; k < 15; k++) {
                    Asteroid ast = new Asteroid();
                    ast.settings(sRock, rand.nextInt(W), rand.nextInt(H), rand.nextInt(360), 25);
                    entities.add(ast);
                }
            } else {
                System.err.println("sRock is null during resetGame");
            }
        } catch (Exception ex) {
            System.err.println("Error in resetGame: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void spawnAsteroid() {
        if (Math.random() * 150 < 1 && sRock != null) {
            Asteroid a = new Asteroid();
            a.settings(sRock, 0, (int) (Math.random() * H), (float) (Math.random() * 360), 25);
            entities.add(a);
        }
    }
}