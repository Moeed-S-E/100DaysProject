package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class DoodleJump extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final int WIDTH = 400, HEIGHT = 533;
    private Timer timer;
    private int x = 100, y = 100, h = 200;
    private float dx = 0, dy = 0;
    private final float MOVE_SPEED = 5.0f;
    private Point[] plat = new Point[20];
    private Random random = new Random();
    private BufferedImage backgroundImg, platformImg, playerImg;
    private int score = 0;
    private boolean gameOver = false;

    public DoodleJump() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN); // Fallback background color
        initializePlatforms();

        try {
            // Load images from src/main/resources/images
            backgroundImg = ImageIO.read(getClass().getResource("/images/background.png"));
            platformImg = ImageIO.read(getClass().getResource("/images/platform.png"));
            playerImg = ImageIO.read(getClass().getResource("/images/doodle.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Set framerate to ~60 FPS (1000ms / 60 ≈ 16.67ms)
        timer = new Timer(16, this);
        timer.start();

        // Keyboard input for movement and restart
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        resetGame();
                    }
                } else {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        dx = MOVE_SPEED; // Move right
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        dx = -MOVE_SPEED; // Move left
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    dx = 0; // Stop moving when key is released
                }
            }
        });
        setFocusable(true);
    }

    private void initializePlatforms() {
        for (int i = 0; i < 10; i++) {
            plat[i] = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
    }

    private void resetGame() {
        x = 100;
        y = 100;
        h = 200;
        dx = 0;
        dy = 0;
        score = 0;
        gameOver = false;
        initializePlatforms();
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(backgroundImg, 0, 0, WIDTH, HEIGHT, null);

        if (!gameOver) {
            // Draw player (doodle)
            g.drawImage(playerImg, x, y, 70, 70, null); // Assuming player size ~70x70

            // Draw platforms
            for (int i = 0; i < 10; i++) {
                g.drawImage(platformImg, plat[i].x, plat[i].y, 68, 14, null); // Platform size
            }

            // Draw score
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 30);
        } else {
            // Draw game over screen
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WIDTH / 2 - 80, HEIGHT / 2 - 20);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, WIDTH / 2 - 40, HEIGHT / 2 + 20);
            g.drawString("Press R to Restart", WIDTH / 2 - 80, HEIGHT / 2 + 50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Update horizontal position
            x += dx;

            // Screen wrapping
            if (x < -70) x = WIDTH - 70; // Wrap from left to right
            if (x > WIDTH) x = -70; // Wrap from right to left

            // Update vertical position
            dy += 0.2; // Gravity
            y += dy;

            // Check for game over
            if (y > HEIGHT) {
                gameOver = true;
                timer.stop();
            }

            // Adjust platforms and score when player goes above h
            if (y < h) {
                int deltaY = (int)(h - y);
                y = h;
                score += deltaY / 10; // Increment score based on upward movement
                for (int i = 0; i < 10; i++) {
                    plat[i].y -= dy;
                    if (plat[i].y > HEIGHT) {
                        plat[i].y = 0;
                        plat[i].x = random.nextInt(WIDTH);
                    }
                }
            }

            // Collision detection 
            for (int i = 0; i < 10; i++) {
                if ((x + 50 > plat[i].x) && (x + 20 < plat[i].x + 68) &&
                    (y + 70 > plat[i].y) && (y + 70 < plat[i].y + 14) && (dy > 0)) {
                    dy = -10;
                }
            }
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Doodle Game!");
        DoodleJump game = new DoodleJump();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Point class 
    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}