package app;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class TronGame extends JPanel {
    private static final int WIDTH = 600; 
    private static final int HEIGHT = 480; 
    private static final int SPEED = 4; 
    private boolean[][] field = new boolean[WIDTH][HEIGHT];
    private Player p1, p2; 
    private boolean game = true; 
    private BufferedImage canvas; 

    // Player class
    static class Player {
        int x, y, dir;
        Color color;

        public Player(Color color) {
            Random rand = new Random();
            this.x = rand.nextInt(WIDTH);
            this.y = rand.nextInt(HEIGHT);
            this.color = color;
            this.dir = rand.nextInt(4); // 0=down, 1=left, 2=right, 3=up
        }

        public void tick() {
            switch (dir) {
                case 0: y += 1; break; // Down
                case 1: x -= 1; break; // Left
                case 2: x += 1; break; // Right
                case 3: y -= 1; break; // Up
            }
            // Wrap around screen edges
            if (x >= WIDTH) x = 0;
            if (x < 0) x = WIDTH - 1;
            if (y >= HEIGHT) y = 0;
            if (y < 0) y = HEIGHT - 1;
        }
    }

    public TronGame() {
        // Initialize players
        p1 = new Player(Color.RED);
        p2 = new Player(Color.GREEN);

        // Create off-screen buffer
        canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT); // Clear canvas with black background
        g2d.dispose();

        // Set up keyboard input
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Player 1 controls (arrow keys)
                if (e.getKeyCode() == KeyEvent.VK_LEFT && p1.dir != 2) p1.dir = 1;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && p1.dir != 1) p1.dir = 2;
                if (e.getKeyCode() == KeyEvent.VK_UP && p1.dir != 0) p1.dir = 3;
                if (e.getKeyCode() == KeyEvent.VK_DOWN && p1.dir != 3) p1.dir = 0;

                // Player 2 controls (WASD)
                if (e.getKeyCode() == KeyEvent.VK_A && p2.dir != 2) p2.dir = 1;
                if (e.getKeyCode() == KeyEvent.VK_D && p2.dir != 1) p2.dir = 2;
                if (e.getKeyCode() == KeyEvent.VK_W && p2.dir != 0) p2.dir = 3;
                if (e.getKeyCode() == KeyEvent.VK_S && p2.dir != 3) p2.dir = 0;
            }
        });

        // Game loop using Timer
        Timer timer = new Timer(1000 / 60, e -> { // 60 FPS
            if (game) {
                updateGame();
                repaint();
            }
        });
        timer.start();
    }

    private void updateGame() {
        for (int i = 0; i < SPEED; i++) {
            p1.tick();
            p2.tick();

            // Check for collisions
            if (field[p1.x][p1.y] || field[p2.x][p2.y]) {
                game = false;
                return;
            }

            // Mark positions in field
            field[p1.x][p1.y] = true;
            field[p2.x][p2.y] = true;

            // Draw trails on canvas
            Graphics2D g2d = canvas.createGraphics();
            g2d.setColor(p1.color);
            g2d.fillOval(p1.x - 3, p1.y - 3, 6, 6); // Draw player 1 trail
            g2d.setColor(p2.color);
            g2d.fillOval(p2.x - 3, p2.y - 3, 6, 6); // Draw player 2 trail
            g2d.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the canvas (trails)
        g2d.drawImage(canvas, 0, 0, null);

        // Draw current player positions
        g2d.setColor(p1.color);
        g2d.fillOval(p1.x - 3, p1.y - 3, 6, 6);
        g2d.setColor(p2.color);
        g2d.fillOval(p2.x - 3, p2.y - 3, 6, 6);

        // If game over, display message
        if (!game) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("Game Over!", WIDTH / 2 - 80, HEIGHT / 2);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("The Tron Game!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TronGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}