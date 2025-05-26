package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

@SuppressWarnings("serial")
public class FifteenPuzzle extends JFrame {
    private static final int W = 64; // Tile size
    private static final int GRID_SIZE = 4;
    private int[][] grid = new int[GRID_SIZE + 2][GRID_SIZE + 2];
    private BufferedImage[] tiles = new BufferedImage[17]; // 1-16, 16 is empty
    private JPanel gamePanel;
    private int emptyX, emptyY; // Track empty tile position
    private boolean isAnimating = false; // Track animation state
    private int movingTile = 0; // Tile being animated
    private int drawX, drawY; // Current position of moving tile during animation


	public FifteenPuzzle() {
        // Initialize window
        setTitle("15-Puzzle!");
        setSize(256, 256);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialize grid
        int n = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                n++;
                grid[i + 1][j + 1] = n;
            }
        }
        grid[GRID_SIZE][GRID_SIZE] = 16; // Empty tile
        emptyX = GRID_SIZE;
        emptyY = GRID_SIZE;

        // Shuffle the grid
        shuffle();

        // Load and split image
        try {
            BufferedImage fullImage = ImageIO.read(FifteenPuzzle.class.getResource("/images/15.png"));
            if (fullImage == null) {
                throw new IOException("Image resource not found: /images/15.png");
            }
            n = 0;
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    n++;
                    tiles[n] = fullImage.getSubimage(i * W, j * W, W, W);
                }
            }
            // Create empty tile (white)
            tiles[16] = new BufferedImage(W, W, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = tiles[16].createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, W, W);
            g.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        // Create game panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw all tiles except the moving one during animation
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        int tile = grid[i + 1][j + 1];
                        if (!isAnimating || tile != movingTile) {
                            g.drawImage(tiles[tile], i * W, j * W, null);
                        }
                    }
                }
                // Draw moving tile at its current position during animation
                if (isAnimating) {
                    g.drawImage(tiles[movingTile], drawX, drawY, null);
                }
            }
        };
        gamePanel.setPreferredSize(new Dimension(256, 256));
        gamePanel.setBackground(Color.WHITE);

        // Add mouse listener
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && !isAnimating) {
                    int x = e.getX() / W + 1;
                    int y = e.getY() / W + 1;

                    int dx = 0, dy = 0;
                    if (x + 1 <= GRID_SIZE + 1 && grid[x + 1][y] == 16) { dx = 1; dy = 0; }
                    else if (y + 1 <= GRID_SIZE + 1 && grid[x][y + 1] == 16) { dx = 0; dy = 1; }
                    else if (y - 1 >= 1 && grid[x][y - 1] == 16) { dx = 0; dy = -1; }
                    else if (x - 1 >= 1 && grid[x - 1][y] == 16) { dx = -1; dy = 0; }

                    if (dx != 0 || dy != 0) {
                        // Swap tiles
                        int n = grid[x][y];
                        grid[x][y] = 16;
                        grid[x + dx][y + dy] = n;
                        emptyX = x;
                        emptyY = y;

                        // Set up animation
                        isAnimating = true;
                        movingTile = n;
                        final int finalDx = dx;
                        final int finalDy = dy;
                        final int targetX = (x + finalDx - 1) * W;
                        final int targetY = (y + finalDy - 1) * W;
                        final int startX = (x - 1) * W;
                        final int startY = (y - 1) * W;
                        drawX = startX;
                        drawY = startY;
                        final float speed = 3;
                        final int[] currentStep = {0};
                        final int totalSteps = W;

                        Timer timer = new Timer(1000 / 60, null);
                        timer.addActionListener(e1 -> {
                            currentStep[0] += speed;
                            float t = Math.min(currentStep[0] / (float)totalSteps, 1.0f);
                            drawX = (int)(startX + (targetX - startX) * t);
                            drawY = (int)(startY + (targetY - startY) * t);
                            gamePanel.repaint();

                            if (currentStep[0] >= totalSteps) {
                                timer.stop();
                                isAnimating = false;
                                movingTile = 0;
                                gamePanel.repaint();
                            }
                        });
                        timer.start();
                    }
                }
            }
        });

        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void shuffle() {
        Random rand = new Random();
        int moves = 100; // Number of random moves to shuffle
        for (int i = 0; i < moves; i++) {
            // Possible moves for the empty tile
            int[][] directions = { {1, 0}, {0, 1}, {0, -1}, {-1, 0} };
            // Randomly select a direction
            int[] dir = directions[rand.nextInt(directions.length)];
            int newX = emptyX + dir[0];
            int newY = emptyY + dir[1];

            // Check if the move is valid
            if (newX >= 1 && newX <= GRID_SIZE && newY >= 1 && newY <= GRID_SIZE) {
                // Swap empty tile with the adjacent tile
                int n = grid[newX][newY];
                grid[emptyX][emptyY] = n;
                grid[newX][newY] = 16;
                emptyX = newX;
                emptyY = newY;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FifteenPuzzle().setVisible(true);
        });
    }
}