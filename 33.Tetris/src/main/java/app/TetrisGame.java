package app;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TetrisGame extends JPanel implements ActionListener {
    private static final int M = 20; // Rows
    private static final int N = 10; // Columns
    private static final int TILE_SIZE = 18;
    private static final int WIDTH = N * TILE_SIZE + 60;
    private static final int HEIGHT = M * TILE_SIZE + 60;

    private int[][] field = new int[M][N];
    private Point[] a = new Point[4]; // Current piece
    private Point[] b = new Point[4]; // Backup for collision checks
    private int[][] figures = {
            {1, 3, 5, 7}, // I
            {2, 4, 5, 7}, // Z
            {3, 5, 4, 6}, // S
            {3, 5, 4, 7}, // T
            {2, 3, 5, 7}, // L
            {3, 5, 7, 6}, // J
            {2, 3, 4, 5}  // O
    };

    private Timer timer;
    private int dx = 0;
    private boolean rotate = false;
    private int colorNum = 1;
    private float delay = 300;
    private Random rand = new Random();

    public TetrisGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        rotate = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        dx = -1;
                        break;
                    case KeyEvent.VK_RIGHT:
                        dx = 1;
                        break;
                    case KeyEvent.VK_DOWN:
                        delay = 50;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    delay = 300;
                }
            }
        });

        timer = new Timer((int) delay, this);
        timer.start();
    }

    private void initGame() {
        for (int i = 0; i < 4; i++) {
            a[i] = new Point();
            b[i] = new Point();
        }
        spawnNewPiece();
    }

    private void resetGame() {
        // Clear the field
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                field[i][j] = 0;
            }
        }
        // Reset piece and variables
        dx = 0;
        rotate = false;
        delay = 300;
        timer.setDelay((int) delay);
        spawnNewPiece();
        timer.start();
    }

    private void spawnNewPiece() {
        int n = rand.nextInt(7);
        for (int i = 0; i < 4; i++) {
            a[i].x = figures[n][i] % 2 + N / 2 - 1; // Center the piece
            a[i].y = figures[n][i] / 2;
        }
        colorNum = 1 + rand.nextInt(7);
        if (!check()) { // Check if new piece can be placed
            timer.stop();
            int option = JOptionPane.showConfirmDialog(this, "Game Over! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0); // Exit if player chooses not to restart
            }
        }
    }

    private boolean check() {
        for (Point p : a) {
            if (p.x < 0 || p.x >= N || p.y >= M || (p.y >= 0 && field[p.y][p.x] != 0)) {
                return false;
            }
        }
        return true;
    }

    private void tick() {
        for (int i = 0; i < 4; i++) {
            b[i].x = a[i].x;
            b[i].y = a[i].y;
            a[i].y += 1;
        }

        if (!check()) {
            for (Point p : b) {
                if (p.y >= 0) { // Ensure we don't write to negative y
                    field[p.y][p.x] = colorNum;
                }
            }
            spawnNewPiece();
        }
    }

    private void move() {
        for (int i = 0; i < 4; i++) {
            b[i].x = a[i].x;
            a[i].x += dx;
        }
        if (!check()) {
            for (int i = 0; i < 4; i++) {
                a[i].x = b[i].x;
            }
        }
    }

    private void rotatePiece() {
        if (isOShape()) return; // Skip rotation for 'O' shape
        Point center = a[1];
        for (int i = 0; i < 4; i++) {
            int x = a[i].y - center.y;
            int y = a[i].x - center.x;
            a[i].x = center.x - x;
            a[i].y = center.y + y;
        }
        if (!check()) {
            for (int i = 0; i < 4; i++) {
                a[i].x = b[i].x;
                a[i].y = b[i].y;
            }
        }
    }

    private boolean isOShape() {
        // 'O' shape has points at (0,0), (0,1), (1,0), (1,1) relative to top-left
        int minX = a[0].x, minY = a[0].y;
        for (Point p : a) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
        }
        boolean[][] shape = new boolean[2][2];
        for (Point p : a) {
            int relX = p.x - minX;
            int relY = p.y - minY;
            if (relX >= 0 && relX < 2 && relY >= 0 && relY < 2) {
                shape[relX][relY] = true;
            }
        }
        return shape[0][0] && shape[0][1] && shape[1][0] && shape[1][1];
    }

    private void clearLines() {
        int linesCleared = 0;
        for (int i = M - 1; i >= 0; i--) {
            int count = 0;
            for (int j = 0; j < N; j++) {
                if (field[i][j] != 0) {
                    count++;
                }
            }
            if (count == N) { // Full row
                linesCleared++;
            } else if (linesCleared > 0) {
                // Shift rows down
                for (int j = 0; j < N; j++) {
                    field[i + linesCleared][j] = field[i][j];
                }
            }
        }
        // Clear the top rows that were shifted down
        for (int i = 0; i < linesCleared; i++) {
            for (int j = 0; j < N; j++) {
                field[i][j] = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (rotate) {
            rotatePiece();
            rotate = false;
        }
        tick();
        clearLines();
        repaint();
        dx = 0;
        timer.setDelay((int) delay);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= M; i++) {
            g.drawLine(30, i * TILE_SIZE + 30, N * TILE_SIZE + 30, i * TILE_SIZE + 30);
        }
        for (int j = 0; j <= N; j++) {
            g.drawLine(j * TILE_SIZE + 30, 30, j * TILE_SIZE + 30, M * TILE_SIZE + 30);
        }

        // Draw field
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (field[i][j] != 0) {
                    g.setColor(getColor(field[i][j]));
                    g.fillRect(j * TILE_SIZE + 30, i * TILE_SIZE + 30, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(j * TILE_SIZE + 30, i * TILE_SIZE + 30, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw current piece
        g.setColor(getColor(colorNum));
        for (Point p : a) {
            if (p.y >= 0) { 
                g.fillRect(p.x * TILE_SIZE + 30, p.y * TILE_SIZE + 30, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(p.x * TILE_SIZE + 30, p.y * TILE_SIZE + 30, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private Color getColor(int num) {
        switch (num) {
            case 1: return Color.CYAN;
            case 2: return Color.RED;
            case 3: return Color.GREEN;
            case 4: return Color.MAGENTA;
            case 5: return Color.ORANGE;
            case 6: return Color.BLUE;
            case 7: return Color.YELLOW;
            default: return Color.BLACK;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}