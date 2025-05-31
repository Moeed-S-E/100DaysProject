package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bejeweled extends Application {
    private static final int TILE_SIZE = 54;
    private static final int GRID_SIZE = 8;
    private static final int OFFSET_X = 48;
    private static final int OFFSET_Y = 24;
    private static final int GEM_TYPES = 7;
    private static final double CANVAS_WIDTH = 740;
    private static final double CANVAS_HEIGHT = 480;

    private Piece[][] grid = new Piece[GRID_SIZE + 2][GRID_SIZE + 2];
    private Image background, gems;
    private boolean isSwap = false, isMoving = false, needRevert = false;
    private boolean isShuffling = false;
    private long shuffleStartTime = 0;
    private int score = 0;
    private int dragStartX = -1, dragStartY = -1;
    private int dragCurrentX = -1, dragCurrentY = -1;
    private int dragEndX = -1, dragEndY = -1;
    private boolean dragging = false;
    private long lastSwapTime = System.nanoTime();
    private List<int[]> hintMatches = new ArrayList<>();
    private int currentHintIndex = -1;
    private long hintStartTime = 0;

    static class Piece {
        double x, y;
        int col, row, kind, match;
        double alpha;

        Piece(int col, int row, int kind) {
            this.col = col;
            this.row = row;
            this.kind = kind;
            this.x = col * TILE_SIZE;
            this.y = row * TILE_SIZE;
            this.match = 0;
            this.alpha = 1.0;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);

        loadImages();
        initGrid();


        canvas.setOnMousePressed(event -> {
            if (isSwap || isMoving || isShuffling) return;
            double mx = event.getX();
            double my = event.getY();
            int px = (int) ((mx - OFFSET_X) / TILE_SIZE) + 1;
            int py = (int) ((my - OFFSET_Y) / TILE_SIZE) + 1;
            if (px < 1 || px > GRID_SIZE || py < 1 || py > GRID_SIZE) return;

            dragStartX = px;
            dragStartY = py;
            dragCurrentX = px;
            dragCurrentY = py;
            dragging = true;
        });

        canvas.setOnMouseDragged(event -> {
            if (!dragging || isSwap || isMoving || isShuffling) return;
            double mx = event.getX();
            double my = event.getY();
            int px = (int) ((mx - OFFSET_X) / TILE_SIZE) + 1;
            int py = (int) ((my - OFFSET_Y) / TILE_SIZE) + 1;
            if (px >= 1 && px <= GRID_SIZE && py >= 1 && py <= GRID_SIZE) {
                dragCurrentX = px;
                dragCurrentY = py;
            } else {
                dragCurrentX = dragCurrentY = -1;
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (!dragging || isSwap || isMoving || isShuffling) {
                dragging = false;
                dragStartX = dragStartY = dragCurrentX = dragCurrentY = -1;
                return;
            }
            double mx = event.getX();
            double my = event.getY();
            int px = (int) ((mx - OFFSET_X) / TILE_SIZE) + 1;
            int py = (int) ((my - OFFSET_Y) / TILE_SIZE) + 1;
            if (px < 1 || px > GRID_SIZE || py < 1 || py > GRID_SIZE) {
                dragging = false;
                dragStartX = dragStartY = dragCurrentX = dragCurrentY = -1;
                return;
            }
            if ((Math.abs(px - dragStartX) == 1 && py == dragStartY) ||
                (Math.abs(py - dragStartY) == 1 && px == dragStartX)) {
                swap(grid[dragStartY][dragStartX], grid[py][px]);
                dragEndX = px;
                dragEndY = py;
                if (hasMatch()) {
                    isSwap = true;
                    needRevert = false;
                    lastSwapTime = System.nanoTime();
                } else {
                    isSwap = true;
                    needRevert = true;
                }
            }
            dragging = false;
            dragStartX = dragStartY = dragCurrentX = dragCurrentY = -1;
        });

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        };
        timer.start();

        primaryStage.setTitle("Bejeweled Game!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void loadImages() {
        try {
            background = new Image(getClass().getResourceAsStream("/images/background.png"));
            gems = new Image(getClass().getResourceAsStream("/images/gems.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGrid() {
        for (int i = 1; i <= GRID_SIZE; i++)
            for (int j = 1; j <= GRID_SIZE; j++)
                grid[i][j] = new Piece(j, i, (int)(Math.random() * GEM_TYPES));
        for (int i = 0; i <= GRID_SIZE + 1; i++) {
            grid[0][i] = new Piece(i, 0, -1);
            grid[GRID_SIZE + 1][i] = new Piece(i, GRID_SIZE + 1, -1);
            grid[i][0] = new Piece(0, i, -1);
            grid[i][GRID_SIZE + 1] = new Piece(GRID_SIZE + 1, i, -1);
        }
    }

    private void swap(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) return;
        int tmpCol = p1.col, tmpRow = p1.row;
        p1.col = p2.col; p1.row = p2.row;
        p2.col = tmpCol; p2.row = tmpRow;
        grid[p1.row][p1.col] = p1;
        grid[p2.row][p2.col] = p2;
    }

    private boolean hasMatch() {
        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                if (grid[i][j] == null || grid[i][j].kind < 0) continue;
                int kind = grid[i][j].kind;

                // Check horizontal matches of 3 or more
                int hCount = 1;
                int k = j - 1;
                while (k >= 1 && grid[i][k] != null && grid[i][k].kind == kind) {
                    hCount++;
                    k--;
                }
                k = j + 1;
                while (k <= GRID_SIZE && grid[i][k] != null && grid[i][k].kind == kind) {
                    hCount++;
                    k++;
                }
                if (hCount >= 3) return true;

                // Check vertical matches of 3 or more
                int vCount = 1;
                k = i - 1;
                while (k >= 1 && grid[k][j] != null && grid[k][j].kind == kind) {
                    vCount++;
                    k--;
                }
                k = i + 1;
                while (k <= GRID_SIZE && grid[k][j] != null && grid[k][j].kind == kind) {
                    vCount++;
                    k++;
                }
                if (vCount >= 3) return true;
            }
        }
        return false;
    }

    private List<int[]> findPossibleMatches() {
        List<int[]> matches = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                if (grid[i][j] == null || grid[i][j].kind < 0) continue;
                // Check right swap
                if (j < GRID_SIZE) {
                    swap(grid[i][j], grid[i][j+1]);
                    if (hasMatch()) {
                        matches.add(new int[]{i, j, i, j+1});
                    }
                    swap(grid[i][j], grid[i][j+1]); // Revert
                }
                // Check down swap
                if (i < GRID_SIZE) {
                    swap(grid[i][j], grid[i+1][j]);
                    if (hasMatch()) {
                        matches.add(new int[]{i, j, i+1, j});
                    }
                    swap(grid[i][j], grid[i+1][j]); // Revert
                }
            }
        }
        return matches;
    }

    private void shuffleGrid() {
        Random rand = new Random();
        do {
            // Collect all gems
            List<Piece> gems = new ArrayList<>();
            for (int i = 1; i <= GRID_SIZE; i++)
                for (int j = 1; j <= GRID_SIZE; j++)
                    if (grid[i][j] != null && grid[i][j].kind >= 0)
                        gems.add(grid[i][j]);

            // Shuffle gems
            for (int i = gems.size() - 1; i > 0; i--) {
                int j = rand.nextInt(i + 1);
                Piece temp = gems.get(i);
                gems.set(i, gems.get(j));
                gems.set(j, temp);
            }

            // Reassign to grid
            int index = 0;
            for (int i = 1; i <= GRID_SIZE; i++) {
                for (int j = 1; j <= GRID_SIZE; j++) {
                    grid[i][j] = gems.get(index++);
                    grid[i][j].row = i;
                    grid[i][j].col = j;
                    grid[i][j].x = j * TILE_SIZE;
                    grid[i][j].y = i * TILE_SIZE;
                    grid[i][j].match = 0;
                    grid[i][j].alpha = 0.5; 
                }
            }
        } while (findPossibleMatches().isEmpty()); 
    }

    private void update() {
        if (isShuffling) {
            long currentTime = System.nanoTime();
            double shuffleElapsed = (currentTime - shuffleStartTime) / 1_000_000_000.0;
            if (shuffleElapsed < 0.5) {
                // Fade in gems during shuffle animation
                for (int i = 1; i <= GRID_SIZE; i++)
                    for (int j = 1; j <= GRID_SIZE; j++)
                        if (grid[i][j] != null)
                            grid[i][j].alpha = Math.min(1.0, grid[i][j].alpha + 0.04);
            } else {
                // End shuffle
                isShuffling = false;
                for (int i = 1; i <= GRID_SIZE; i++)
                    for (int j = 1; j <= GRID_SIZE; j++)
                        if (grid[i][j] != null)
                            grid[i][j].alpha = 1.0;
                lastSwapTime = System.nanoTime();
                hintMatches.clear();
                currentHintIndex = -1;
            }
            return;
        }

        // Check for matches of 3 or more
        for (int i = 1; i <= GRID_SIZE; i++) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                if (grid[i][j] == null) continue;
                grid[i][j].match = 0;

                // Check horizontal matches
                int kind = grid[i][j].kind;
                int hStart = j, hEnd = j;
                while (hStart > 1 && grid[i][hStart-1] != null && grid[i][hStart-1].kind == kind) hStart--;
                while (hEnd < GRID_SIZE && grid[i][hEnd+1] != null && grid[i][hEnd+1].kind == kind) hEnd++;
                if (hEnd - hStart + 1 >= 3) {
                    for (int k = hStart; k <= hEnd; k++) grid[i][k].match++;
                }

                // Check vertical matches
                int vStart = i, vEnd = i;
                while (vStart > 1 && grid[vStart-1][j] != null && grid[vStart-1][j].kind == kind) vStart--;
                while (vEnd < GRID_SIZE && grid[vEnd+1][j] != null && grid[vEnd+1][j].kind == kind) vEnd++;
                if (vEnd - vStart + 1 >= 3) {
                    for (int k = vStart; k <= vEnd; k++) grid[k][j].match++;
                }
            }
        }

        // Move pieces
        isMoving = false;
        for (int i = 1; i <= GRID_SIZE; i++)
            for (int j = 1; j <= GRID_SIZE; j++) {
                if (grid[i][j] == null) continue;
                Piece p = grid[i][j];
                double targetX = p.col * TILE_SIZE;
                double targetY = p.row * TILE_SIZE;
                p.x += (targetX - p.x) * 0.25;
                p.y += (targetY - p.y) * 0.25;
                if (Math.abs(p.x - targetX) > 2 || Math.abs(p.y - targetY) > 2) isMoving = true;
                else {
                    p.x = targetX;
                    p.y = targetY;
                }
            }


        if (!isMoving) {
            for (int i = 1; i <= GRID_SIZE; i++)
                for (int j = 1; j <= GRID_SIZE; j++) {
                    if (grid[i][j] == null) continue;
                    if (grid[i][j].match > 0 && grid[i][j].alpha > 0.04) {
                        grid[i][j].alpha = Math.max(0, grid[i][j].alpha - 0.08);
                        isMoving = true;
                    }
                }
        }


        int matched = 0;
        for (int i = 1; i <= GRID_SIZE; i++)
            for (int j = 1; j <= GRID_SIZE; j++)
                if (grid[i][j] != null && grid[i][j].match > 0) matched++;
        score += matched;

 
        if (isSwap && !isMoving) {
            if (matched == 0 && needRevert) {
                swap(grid[dragStartY][dragStartX], grid[dragEndY][dragEndX]);
                needRevert = false;
            }
            isSwap = false;
        }


        if (!isMoving) {
            for (int j = 1; j <= GRID_SIZE; j++) {
                int emptyRow = GRID_SIZE;
                for (int i = GRID_SIZE; i >= 1; i--) {
                    if (grid[i][j] == null || grid[i][j].match > 0) {
                        grid[i][j] = null;
                    } else if (i < emptyRow) {
                        grid[emptyRow][j] = grid[i][j];
                        grid[emptyRow][j].row = emptyRow;
                        grid[emptyRow][j].col = j;
                        grid[i][j] = null;
                        emptyRow--;
                    } else {
                        emptyRow--;
                    }
                }
                for (int i = emptyRow; i >= 1; i--) {
                    grid[i][j] = new Piece(j, i, (int)(Math.random() * GEM_TYPES));
                    grid[i][j].y = -TILE_SIZE * (emptyRow - i + 1);
                    grid[i][j].match = 0;
                    grid[i][j].alpha = 1.0;
                }
            }
        }


        if (!isSwap && !isMoving) {
            long currentTime = System.nanoTime();
            double elapsedSeconds = (currentTime - lastSwapTime) / 1_000_000_000.0;
            if (elapsedSeconds > 5) {
                if (hintMatches.isEmpty()) {
                    hintMatches = findPossibleMatches();
                    if (hintMatches.isEmpty()) {
                        isShuffling = true;
                        shuffleStartTime = currentTime;
                        shuffleGrid();
                    } else {
                        currentHintIndex = 0;
                        hintStartTime = currentTime;
                    }
                } else {
                    double hintDuration = (currentTime - hintStartTime) / 1_000_000_000.0;
                    if (hintDuration > 2 && !hintMatches.isEmpty()) {
                        currentHintIndex = (currentHintIndex + 1) % hintMatches.size();
                        hintStartTime = currentTime;
                    }
                }
            } else {
                hintMatches.clear();
                currentHintIndex = -1;
            }
        }
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        if (background != null) {
            gc.drawImage(background, 0, 0);
        }

        for (int i = 1; i <= GRID_SIZE; i++)
            for (int j = 1; j <= GRID_SIZE; j++) {
                if (grid[i][j] == null || grid[i][j].kind < 0) continue;
                Piece p = grid[i][j];
                double sx = p.kind * 49;
                double sy = 0;
                double sw = 49;
                double sh = 49;
                double drawX = p.x + OFFSET_X - TILE_SIZE;
                double drawY = p.y + OFFSET_Y - TILE_SIZE;

                if (dragging && j == dragStartX && i == dragStartY && dragCurrentX != -1 && dragCurrentY != -1 && !isShuffling) {
                    double targetX = dragCurrentX * TILE_SIZE;
                    double targetY = dragCurrentY * TILE_SIZE;
                    drawX += (targetX - p.x) * 0.3;
                    drawY += (targetY - p.y) * 0.3;
                }

                gc.setGlobalAlpha(p.alpha);
                gc.drawImage(gems, sx, sy, sw, sh, drawX, drawY, TILE_SIZE, TILE_SIZE);
                gc.setGlobalAlpha(1.0);
            }


        if (!isShuffling && currentHintIndex >= 0 && currentHintIndex < hintMatches.size()) {
            int[] match = hintMatches.get(currentHintIndex);
            int r1 = match[0], c1 = match[1], r2 = match[2], c2 = match[3];
            gc.setFill(Color.rgb(0, 255, 255, 0.5));
            if (grid[r1][c1] != null) {
                gc.fillRect(grid[r1][c1].x + OFFSET_X - TILE_SIZE, grid[r1][c1].y + OFFSET_Y - TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
            if (grid[r2][c2] != null) {
                gc.fillRect(grid[r2][c2].x + OFFSET_X - TILE_SIZE, grid[r2][c2].y + OFFSET_Y - TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }


        if (dragging && !isShuffling) {
            for (int i = 1; i <= GRID_SIZE; i++)
                for (int j = 1; j <= GRID_SIZE; j++) {
                    if (grid[i][j] == null) continue;
                    Piece p = grid[i][j];
                    if (j == dragStartX && i == dragStartY) {
                        gc.setFill(Color.rgb(255, 255, 0, 0.5));
                        gc.fillRect(p.x + OFFSET_X - TILE_SIZE, p.y + OFFSET_Y - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    } else if (j == dragCurrentX && i == dragCurrentY &&
                               ((Math.abs(j - dragStartX) == 1 && i == dragStartY) ||
                                (Math.abs(i - dragStartY) == 1 && j == dragStartX))) {
                        gc.setFill(Color.rgb(0, 255, 0, 0.5));
                        gc.fillRect(p.x + OFFSET_X - TILE_SIZE, p.y + OFFSET_Y - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
        }

        
        String scoreStr = "Score: " + score;
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.setFill(Color.BLACK);
        gc.fillText(scoreStr, CANVAS_WIDTH - 120 + 1, CANVAS_HEIGHT - 10 + 1);
        gc.fillText(scoreStr, CANVAS_WIDTH - 120 - 1, CANVAS_HEIGHT - 10 - 1);
        gc.setFill(Color.YELLOW);
        gc.fillText(scoreStr, CANVAS_WIDTH - 120, CANVAS_HEIGHT - 10);
    }

    public static void main(String[] args) {
        launch(args);
    }
}