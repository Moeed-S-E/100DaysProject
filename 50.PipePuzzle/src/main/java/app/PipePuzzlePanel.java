package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PipePuzzlePanel extends JPanel {
    private static final int N = 6; // Grid size
    private static final int TILE_SIZE = 54;
    private static final Point OFFSET = new Point(65, 55); // Drawing offset

    private Pipe[][] grid = new Pipe[N][N];
    private Point serverPos;
    private Random random = new Random();

    private BufferedImage backgroundImage;
    private BufferedImage compSheetImage;
    private BufferedImage serverImage;
    private BufferedImage pipesSpriteSheet;

    private BufferedImage[] pipeKindImages = new BufferedImage[5];
    private BufferedImage compImage_Off, compImage_On;

    private javax.swing.Timer animationTimer;
    private boolean missingSpriteWarningLogged = false; // To avoid flooding console

    public PipePuzzlePanel() {
        setPreferredSize(new Dimension(390, 390));
        loadImages();
        extractSubImages(); // This will now handle errors more gracefully
        initGame();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        animationTimer = new javax.swing.Timer(16, ae -> updateAndRepaint());
        animationTimer.start();
    }

    private void loadImages() {
        try {
            backgroundImage = loadImage("/images/background.png");
            compSheetImage = loadImage("/images/comp.png");
            serverImage = loadImage("/images/server.png");
            pipesSpriteSheet = loadImage("/images/pipes.png");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(),
                                          "Image Loading Error", JOptionPane.ERROR_MESSAGE);
            // Consider exiting or disabling parts of the game if critical images fail
             if (pipesSpriteSheet == null || backgroundImage == null) {
                System.err.println("Critical images failed to load. Exiting.");
                System.exit(1);
            }
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl == null) {
            throw new IOException("Cannot find image resource: " + path);
        }
        return ImageIO.read(imgUrl);
    }

    private void extractSubImages() {
        // Extract pipe sprites
        if (pipesSpriteSheet != null) {
            System.out.println("pipesSpriteSheet dimensions: " + pipesSpriteSheet.getWidth() + "x" + pipesSpriteSheet.getHeight());
            int expectedMinWidth = 5 * TILE_SIZE;
            int expectedMinHeight = TILE_SIZE;

            if (pipesSpriteSheet.getWidth() < expectedMinWidth || pipesSpriteSheet.getHeight() < expectedMinHeight) {
                System.err.println("ERROR: pipes.png is too small! Expected at least " +
                                   expectedMinWidth + "x" + expectedMinHeight +
                                   ", but got " + pipesSpriteSheet.getWidth() + "x" + pipesSpriteSheet.getHeight() +
                                   ". Some pipe sprites may be missing or placeholders will be used.");
            }

            for (int i = 0; i < 5; i++) {
                int x = i * TILE_SIZE;
                if (x + TILE_SIZE <= pipesSpriteSheet.getWidth() && TILE_SIZE <= pipesSpriteSheet.getHeight()) {
                    try {
                        pipeKindImages[i] = pipesSpriteSheet.getSubimage(x, 0, TILE_SIZE, TILE_SIZE);
                    } catch (RasterFormatException e) {
                        System.err.println("RasterFormatException while extracting pipe kind " + i + " from pipes.png.");
                        System.err.println("Attempted: x=" + x + ", y=0, width=" + TILE_SIZE + ", height=" + TILE_SIZE);
                        System.err.println("Sprite sheet dimensions: " + pipesSpriteSheet.getWidth() + "x" + pipesSpriteSheet.getHeight());
                        pipeKindImages[i] = null; // Mark as not loaded
                    }
                } else {
                    System.err.println("Skipping pipe kind " + i + " from pipes.png: Sprite region is outside the image bounds.");
                    pipeKindImages[i] = null; // Mark as not loaded
                }
            }
        } else {
            System.err.println("CRITICAL ERROR: pipesSpriteSheet (pipes.png) is null. Image loading failed. Pipes will not be drawn.");
            // All pipeKindImages will remain null
        }

        // Extract computer component sprites
        if (compSheetImage != null) {
            System.out.println("compSheetImage dimensions: " + compSheetImage.getWidth() + "x" + compSheetImage.getHeight());
            // For compImage_Off: x=0, width=36. Expected sheet width >= 36
            if (0 + 36 <= compSheetImage.getWidth() && 36 <= compSheetImage.getHeight()) {
                try {
                    compImage_Off = compSheetImage.getSubimage(0, 0, 36, 36);
                } catch (RasterFormatException e) {
                    System.err.println("RasterFormatException for compImage_Off. Setting to null.");
                    compImage_Off = null;
                }
            } else {
                System.err.println("comp.png too small for compImage_Off. Setting to null.");
                compImage_Off = null;
            }

            // For compImage_On: x=53, width=36. Expected sheet width >= 53+36 = 89
            if (53 + 36 <= compSheetImage.getWidth() && 36 <= compSheetImage.getHeight()) {
                 try {
                    compImage_On = compSheetImage.getSubimage(53, 0, 36, 36);
                } catch (RasterFormatException e) {
                    System.err.println("RasterFormatException for compImage_On. Setting to null.");
                    compImage_On = null;
                }
            } else {
                System.err.println("comp.png too small for compImage_On. Setting to null.");
                compImage_On = null;
            }
        } else {
            System.err.println("ERROR: compSheetImage (comp.png) is null. Computer components will not be drawn.");
            compImage_Off = null;
            compImage_On = null;
        }
    }

    // ... (initGame, getCell, isOutOfBounds, generatePuzzle, drop, handleMouseClick, updateAndRepaint methods remain the same as previously provided) ...
    // Make sure these methods are present from the previous correct version of the code.
    // For brevity, I am not repeating them here but they are essential.
    // The initGame, getCell, etc., from your attachment [1] are fine.

    private void initGame() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = new Pipe();
            }
        }

        generatePuzzle();

        for (int i = 0; i < N; i++) { // Row
            for (int j = 0; j < N; j++) { // Column
                Pipe p = grid[j][i]; 

                Pipe tempPipeState = new Pipe();
                tempPipeState.dirs = new ArrayList<>(p.dirs); 
                for (int n = 4; n > 0; n--) {
                    String s = tempPipeState.getConnectionsKey();
                    if (s.equals("0011") || s.equals("0111") || s.equals("0101") || s.equals("0010")) {
                        p.orientation = n; 
                    }
                    tempPipeState.rotateConnections(); 
                }
                
                int shuffles = random.nextInt(4);
                for (int k = 0; k < shuffles; k++) {
                    p.orientation++; 
                    p.rotateConnections(); 
                }
            }
        }
        
        do {
            serverPos = new Point(random.nextInt(N), random.nextInt(N));
        } while (getCell(serverPos).dirs.size() == 1); 

        drop(serverPos);
    }

    private Pipe getCell(Point v) {
        if (isOutOfBounds(v)) return new Pipe(); 
        return grid[v.x][v.y];
    }

    private boolean isOutOfBounds(Point v) {
        return v.x < 0 || v.x >= N || v.y < 0 || v.y >= N;
    }

    private void generatePuzzle() {
        List<Point> nodes = new ArrayList<>();
        nodes.add(new Point(random.nextInt(N), random.nextInt(N)));

        while (!nodes.isEmpty()) {
            int n = random.nextInt(nodes.size());
            Point v = nodes.get(n);
            Point d = Pipe.DIRS_CLOCKWISE_ORDER[random.nextInt(4)]; 

            Pipe cellV = getCell(v);

            if (cellV.dirs.size() == 3) { 
                nodes.remove(n);
                continue;
            }
            if (cellV.dirs.size() == 2 && random.nextInt(50) != 0) { 
                continue;
            }

            boolean complete = true;
            for (Point dirOption : Pipe.DIRS_CLOCKWISE_ORDER) {
                Point neighbor = new Point(v.x + dirOption.x, v.y + dirOption.y);
                if (!isOutOfBounds(neighbor) && getCell(neighbor).dirs.isEmpty()) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                nodes.remove(n);
                continue;
            }

            Point targetV = new Point(v.x + d.x, v.y + d.y);
            if (isOutOfBounds(targetV) || !getCell(targetV).dirs.isEmpty()) {
                continue;
            }

            cellV.dirs.add(d);
            getCell(targetV).dirs.add(Pipe.negative(d));
            nodes.add(targetV);
        }
    }

    private void drop(Point v) {
        Pipe currentPipe = getCell(v);
        if (currentPipe.on) return;
        currentPipe.on = true;

        for (Point dir : Pipe.DIRS_CLOCKWISE_ORDER) { 
            if (currentPipe.isConnectedTo(dir)) { 
                Point neighborPos = new Point(v.x + dir.x, v.y + dir.y);
                if (!isOutOfBounds(neighborPos)) {
                    Pipe neighborPipe = getCell(neighborPos);
                    if (neighborPipe.isConnectedTo(Pipe.negative(dir))) {
                        drop(neighborPos); 
                    }
                }
            }
        }
    }
    
    private void handleMouseClick(Point mousePos) {
        int gridX = (mousePos.x - OFFSET.x + TILE_SIZE / 2) / TILE_SIZE; // Adjusted for center click like SFML
        int gridY = (mousePos.y - OFFSET.y + TILE_SIZE / 2) / TILE_SIZE; // Adjusted for center click
        Point clickedGridPos = new Point(gridX, gridY);

        if (isOutOfBounds(clickedGridPos)) return;

        Pipe clickedPipe = getCell(clickedGridPos);
        clickedPipe.orientation++; 
        clickedPipe.rotateConnections(); 

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j].on = false; // Reset flow for grid[i][j] (all pipes)
            }
        }
        drop(serverPos);
        
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void updateAndRepaint() {
        boolean needsRepaint = false;
        for (int r = 0; r < N; r++) { // Use r, c consistently if preferred
            for (int c = 0; c < N; c++) {
                Pipe p = grid[c][r]; // grid[col][row]
                double targetAngle = p.orientation * 90.0;
                
                if (p.currentAngle != targetAngle) { // Animate if not at target
                    // Simple discrete step towards target, can be made smoother
                    if (p.currentAngle < targetAngle) {
                        p.currentAngle += 15; 
                        if (p.currentAngle > targetAngle) p.currentAngle = targetAngle;
                    } else if (p.currentAngle > targetAngle) { // Handles wrap-around if orientation can decrease
                         p.currentAngle -=15;
                         if(p.currentAngle < targetAngle) p.currentAngle = targetAngle;
                    }
                     // For instant rotation (like original SFML after click, before animation was added)
                     // p.currentAngle = targetAngle; 
                    needsRepaint = true;
                }
            }
        }
        if (needsRepaint) {
            repaint();
        }
        // Always repaint if timer is running to catch 'on' state changes or continuous animations
        // If you optimize to stop timer, then repaint only if needsRepaint is true.
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, this);
        }

        for (int r = 0; r < N; r++) { // row
            for (int c = 0; c < N; c++) { // col
                Pipe p = grid[c][r]; // grid[col][row]

                int kind = p.dirs.size();
                if (kind == 2) {
                    if (!p.dirs.isEmpty() && p.dirs.size() == 2) { // Ensure dirs has 2 elements
                        Point dir1 = p.dirs.get(0);
                        Point dir2 = p.dirs.get(1);
                        if (dir1.x == -dir2.x && dir1.y == -dir2.y) {
                            kind = 0;
                        }
                    } else { // Should not happen if logic is correct, but defensive
                        kind = 0; 
                    }
                }
                if (kind > 4) kind = 4;
                if (p.dirs.isEmpty() && kind !=0 ) continue; // Don't draw if logically empty, unless kind forced to 0 (straight)

                float originX_onScreen = c * TILE_SIZE + OFFSET.x;
                float originY_onScreen = r * TILE_SIZE + OFFSET.y;
                float drawX = originX_onScreen - TILE_SIZE / 2.0f;
                float drawY = originY_onScreen - TILE_SIZE / 2.0f;

                BufferedImage pipeImageToDraw = null;
                if (kind >= 0 && kind < pipeKindImages.length) {
                    pipeImageToDraw = pipeKindImages[kind];
                }

                AffineTransform oldTransform = g2d.getTransform();
                g2d.rotate(Math.toRadians(p.currentAngle), originX_onScreen, originY_onScreen);

                if (pipeImageToDraw != null) {
                    g2d.drawImage(pipeImageToDraw, (int)drawX, (int)drawY, this);
                } else {
                    // Draw placeholder for missing pipe sprite
                    g2d.setColor(Color.MAGENTA);
                    g2d.fillRect((int)drawX, (int)drawY, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("M", (int)drawX + TILE_SIZE/2 -4, (int)drawY + TILE_SIZE/2 + 4);
                    if (!missingSpriteWarningLogged) {
                       // System.err.println("Warning: Missing sprite for pipe kind " + kind + ". Drawing placeholder. (This message will not repeat)");
                       // missingSpriteWarningLogged = true; // Log only once per type or overall
                    }
                }
                g2d.setTransform(oldTransform);

                if (kind == 1) { // End-pipe with computer
                    BufferedImage compToDraw = p.on ? compImage_On : compImage_Off;
                    if (compToDraw != null) {
                        float compDrawX = originX_onScreen - 18;
                        float compDrawY = originY_onScreen - 18;
                        g2d.drawImage(compToDraw, (int)compDrawX, (int)compDrawY, this);
                    } else {
                        // Placeholder for missing computer sprite (optional)
                        // g2d.setColor(Color.ORANGE);
                        // g2d.fillRect((int)(originX_onScreen - 18), (int)(originY_onScreen - 18), 36, 36);
                    }
                }
            }
        }

        if (serverImage != null && serverPos != null) {
            float serverOriginX_onScreen = serverPos.x * TILE_SIZE + OFFSET.x;
            float serverOriginY_onScreen = serverPos.y * TILE_SIZE + OFFSET.y;
            float serverDrawX = serverOriginX_onScreen - 20;
            float serverDrawY = serverOriginY_onScreen - 20;
            g2d.drawImage(serverImage, (int)serverDrawX, (int)serverDrawY, this);
        }
        g2d.dispose();
    }
}
