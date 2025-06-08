package game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

// Helper class for 3D integer vectors
class Vector3i {
    int x, y, z;

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3i vector3i = (Vector3i) o;
        return x == vector3i.x && y == vector3i.y && z == vector3i.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3i{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}

public class MahjongSolitaire extends Application {

    // --- Constants ---
    private static final int BOARD_MAX_X_RAW = 30;
    private static final int BOARD_MAX_Y_RAW = 18;
    private static final int BOARD_MAX_Z_RAW = 10;

    private static final int FIELD_DIM_X = 50;
    private static final int FIELD_DIM_Y = 50;
    private static final int FIELD_DIM_Z = 50;

    private final int tileWidth = 48;
    private final int tileHeight = 66;
    private final int stepX = tileWidth / 2 - 2;
    private final int stepY = tileHeight / 2 - 2;
    private final float offX = 4.6f;
    private final float offY = 7.1f;
    private final int deskOffset = 30;

    // --- Game State Variables ---
    private int[][][] field = new int[FIELD_DIM_Y][FIELD_DIM_X][FIELD_DIM_Z];
    private Image tilesSpriteSheet;
    private Image backgroundImage;

    private Vector3i firstSelectedTile = new Vector3i(-1, -1, -1); 
    private Vector3i secondSelectedTileCandidate = new Vector3i(-1, -1, -1);

    private List<Vector3i> moves = new ArrayList<>();
    private Random random = new Random();

    // --- UI and Rendering ---
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private boolean needsRedraw = true;

    // --- Keyboard Navigation ---
    private Vector3i keyboardFocusedTile = null;
    private List<Vector3i> openSelectableTiles = new ArrayList<>();
    private int currentKeyboardFocusIndex = -1;

    // --- Game Flow ---
    private enum GameStatus { PLAYING, WON, LOST_STUCK }
    private GameStatus currentGameState = GameStatus.PLAYING;
    private int totalTiles = 0;
    private int matchedTilesCount = 0;


    @Override
    public void start(Stage primaryStage) {
        loadResources();
        initializeGame();

        Pane root = new Pane();
        gameCanvas = new Canvas(740, 570);
        gc = gameCanvas.getGraphicsContext2D();
        root.getChildren().add(gameCanvas);

        Scene scene = new Scene(root);
        setupInputHandlers(scene);

        primaryStage.setTitle("Mahjong Solitaire");
        primaryStage.setScene(scene);
        primaryStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (needsRedraw) {
                    renderGame();
                    needsRedraw = false;
                }
            }
        }.start();
    }

    private void initializeGame() {
        initializeField();
        loadMapFromFile("files/map.txt");
        generateTileLayout();
        finalizeTileIds();
        countTotalTiles();
        updateOpenSelectableTilesAndKeyboardFocus();
        needsRedraw = true;
    }
    
    private void countTotalTiles() {
        totalTiles = 0;
        for (int z = 0; z < BOARD_MAX_Z_RAW; z++) {
            for (int y = 0; y < BOARD_MAX_Y_RAW; y++) {
                for (int x = 0; x < BOARD_MAX_X_RAW; x++) {
                    if (getField(x, y, z) > 0) {
                        totalTiles++;
                    }
                }
            }
        }
        matchedTilesCount = 0;
    }


    private void setupInputHandlers(Scene scene) {
        scene.setOnMouseClicked(event -> {
            if (currentGameState != GameStatus.PLAYING) return;
            if (event.getButton() == MouseButton.SECONDARY) {
                handleUndoAction();
            } else if (event.getButton() == MouseButton.PRIMARY) {
                handleMouseSelectAction(new Point2D(event.getX(), event.getY()));
            }
        });

        scene.setOnKeyPressed(event -> {
            if (currentGameState != GameStatus.PLAYING) return;
            boolean focusChanged = false;
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                navigateKeyboardFocus(1, 0); // Horizontal positive
                focusChanged = true;
            } else if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                navigateKeyboardFocus(-1, 0); // Horizontal negative
                focusChanged = true;
            } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                navigateKeyboardFocus(0, 1); // Vertical positive
                focusChanged = true;
            } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                navigateKeyboardFocus(0, -1); // Vertical negative
                focusChanged = true;
            } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                if (keyboardFocusedTile != null) {
                    processTileSelection(keyboardFocusedTile);
                }
            } else if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.U) {
                handleUndoAction();
            }
            if (focusChanged) needsRedraw = true;
        });
    }
    
    // Navigate based on current keyboard focus using a spatial-like list traversal
    private void navigateKeyboardFocus(int dx, int dy) {
        if (openSelectableTiles.isEmpty()) return;

        if (keyboardFocusedTile == null || currentKeyboardFocusIndex == -1) {
            currentKeyboardFocusIndex = 0;
            keyboardFocusedTile = openSelectableTiles.get(0);
            return;
        }

        // Simple list-based navigation for now. More complex spatial navigation can be added.
        if (dx > 0 || dy > 0) { // Next (Right/Down)
            currentKeyboardFocusIndex = (currentKeyboardFocusIndex + 1) % openSelectableTiles.size();
        } else if (dx < 0 || dy < 0) { // Previous (Left/Up)
            currentKeyboardFocusIndex = (currentKeyboardFocusIndex - 1 + openSelectableTiles.size()) % openSelectableTiles.size();
        }
        keyboardFocusedTile = openSelectableTiles.get(currentKeyboardFocusIndex);
    }


    private void loadResources() {
        try {
            InputStream tilesStream = getClass().getResourceAsStream("/files/tiles.png");
            InputStream bgStream = getClass().getResourceAsStream("/files/background.png");
            if (tilesStream == null) throw new IOException("Cannot find /files/tiles.png");
            if (bgStream == null) throw new IOException("Cannot find /files/background.png");
            tilesSpriteSheet = new Image(tilesStream);
            backgroundImage = new Image(bgStream);
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace(); System.exit(1);
        }
    }

    private void initializeField() { /* ... same as previous ... */ 
        for (int i = 0; i < FIELD_DIM_Y; i++) {
            for (int j = 0; j < FIELD_DIM_X; j++) {
                for (int k = 0; k < FIELD_DIM_Z; k++) {
                    field[i][j][k] = 0;
                }
            }
        }
    }
    private int getField(int x, int y, int z) { /* ... same ... */
        if (y + 2 < 0 || y + 2 >= FIELD_DIM_Y || x + 2 < 0 || x + 2 >= FIELD_DIM_X || z < 0 || z >= FIELD_DIM_Z) {
            return 0;
        }
        return field[y + 2][x + 2][z];
    }
    private void setField(int x, int y, int z, int value) { /* ... same ... */
        if (y + 2 < 0 || y + 2 >= FIELD_DIM_Y || x + 2 < 0 || x + 2 >= FIELD_DIM_X || z < 0 || z >= FIELD_DIM_Z) {
            return;
        }
        field[y + 2][x + 2][z] = value;
    }
    private int getField(Vector3i v) { return getField(v.x, v.y, v.z); }
    private void setField(Vector3i v, int value) { setField(v.x, v.y, v.z, value); }

    private boolean isOpen(int x, int y, int z) { /* ... same as previous, critical game rule ... */
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (getField(x + 2, y + i, z) > 0 && getField(x - 2, y + j, z) > 0) {
                    return false;
                }
            }
        }
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                 if (z + 1 < BOARD_MAX_Z_RAW) {
                    if (getField(x + i, y + j, z + 1) > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void loadMapFromFile(String filePath) { /* ... same ... */
        try (InputStream is = getClass().getResourceAsStream("/" + filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {
            String line;
            for (int y_map = 0; y_map < BOARD_MAX_Y_RAW; y_map++) {
                line = reader.readLine();
                if (line == null) break;
                for (int x_map = 0; x_map < BOARD_MAX_X_RAW; x_map++) {
                    if (x_map >= line.length()) break;
                    char a = line.charAt(x_map);
                    int n = a - '0';
                    for (int z_coord = 0; z_coord < n; z_coord++) {
                        if (z_coord >= BOARD_MAX_Z_RAW) break;
                        if (getField(x_map - 1, y_map - 1, z_coord) > 0) {
                            setField(x_map - 1, y_map, z_coord, 0);
                            setField(x_map, y_map - 1, z_coord, 0);
                        } else {
                            setField(x_map, y_map, z_coord, 1);
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace(); System.err.println("Error loading map: " + e.getMessage());
        }
    }
    private void generateTileLayout() { /* ... same, tile ID assignment logic ... */
        int k = 1;
        while (true) {
            List<Vector3i> openTilesForLayout = new ArrayList<>();
            for (int z_coord = 0; z_coord < BOARD_MAX_Z_RAW; z_coord++) {
                for (int y_coord = 0; y_coord < BOARD_MAX_Y_RAW; y_coord++) {
                    for (int x_coord = 0; x_coord < BOARD_MAX_X_RAW; x_coord++) {
                        if (getField(x_coord, y_coord, z_coord) > 0 && isOpen(x_coord, y_coord, z_coord)) {
                            openTilesForLayout.add(new Vector3i(x_coord, y_coord, z_coord));
                        }
                    }
                }
            }

            if (openTilesForLayout.size() < 2) break;

            int n = openTilesForLayout.size();
            int idxA = random.nextInt(n);
            int idxB;
            do {
                idxB = random.nextInt(n);
            } while (idxA == idxB);

            Vector3i tileA = openTilesForLayout.get(idxA);
            Vector3i tileB = openTilesForLayout.get(idxB);

            setField(tileA, -k);
            int kForB = k;
            if (k > 34) { // Special rule for seasons/flowers from original C++
                kForB++;
            }
            setField(tileB, -kForB);
            
            k = kForB; // Continue sequence from B's ID
            k %= 42;
            k++;
            if (k == 0) k = 1; // Ensure k is at least 1
        }
    }
    private void finalizeTileIds() { /* ... same ... */
        for (int z = 0; z < BOARD_MAX_Z_RAW; z++) {
            for (int y = 0; y < BOARD_MAX_Y_RAW; y++) {
                for (int x = 0; x < BOARD_MAX_X_RAW; x++) {
                    if (getField(x, y, z) < 0) {
                        setField(x, y, z, getField(x, y, z) * -1);
                    }
                }
            }
        }
    }

    private void handleUndoAction() {
        if (moves.size() >= 2) {
            Vector3i move2 = moves.remove(moves.size() - 1);
            Vector3i move1 = moves.remove(moves.size() - 1);
            setField(move1, Math.abs(getField(move1))); // Restore tile
            setField(move2, Math.abs(getField(move2)));
            matchedTilesCount -=2;

            firstSelectedTile = new Vector3i(-1, -1, -1); // Reset selections
            secondSelectedTileCandidate = new Vector3i(-1, -1, -1);
            
            updateOpenSelectableTilesAndKeyboardFocus();
            needsRedraw = true;
        }
    }

    private void handleMouseSelectAction(Point2D mousePos) {
        Vector3i clickedTile = null;
        // Iterate Z from front to back to find the topmost clicked tile
        for (int z_scan = BOARD_MAX_Z_RAW - 1; z_scan >= 0; z_scan--) {
            for (int x_scan = BOARD_MAX_X_RAW -1; x_scan >=0; x_scan--) { // Match drawing order for picking
                 for (int y_scan = 0; y_scan < BOARD_MAX_Y_RAW; y_scan++) {
                    if (getField(x_scan,y_scan,z_scan) > 0 && isOpen(x_scan,y_scan,z_scan)) {
                        double drawX = x_scan * stepX + (z_scan * offX) + deskOffset;
                        double drawY = y_scan * stepY - (z_scan * offY);
                        if (mousePos.getX() >= drawX && mousePos.getX() < drawX + tileWidth &&
                            mousePos.getY() >= drawY && mousePos.getY() < drawY + tileHeight) {
                            clickedTile = new Vector3i(x_scan,y_scan,z_scan);
                            // Update keyboard focus to clicked tile
                            keyboardFocusedTile = clickedTile;
                            currentKeyboardFocusIndex = openSelectableTiles.indexOf(clickedTile);
                            // Break all loops once the top-most tile is found
                            z_scan = -1; y_scan = BOARD_MAX_Y_RAW; x_scan = -1; 
                            break;
                        }
                    }
                 }
            }
        }
        if (clickedTile != null) {
            processTileSelection(clickedTile);
        } else {
            // Clicked on empty space: Deselect the first tile for better UX
            firstSelectedTile = new Vector3i(-1, -1, -1);
            needsRedraw = true;
        }
    }

    private void processTileSelection(Vector3i newlySelectedTile) {
        secondSelectedTileCandidate = newlySelectedTile;

        // If the new selection is the same as the already selected first tile, effectively deselect it.
        if (secondSelectedTileCandidate.equals(firstSelectedTile)) {
            firstSelectedTile = new Vector3i(-1,-1,-1); // Deselect
            secondSelectedTileCandidate = new Vector3i(-1,-1,-1);
            needsRedraw = true;
            return;
        }

        int tileId1 = getField(secondSelectedTileCandidate); // Current (second)
        int tileId2 = getField(firstSelectedTile);         // Previous (first)

        // Check if we have two valid tiles selected
        if (tileId1 > 0 && tileId2 > 0 && firstSelectedTile.x != -1) {
            boolean match = false;
            // Standard match
            if (tileId1 == tileId2) {
                match = true;
            } else {
                // Season tiles: IDs 35-38
                boolean selIsSeason = tileId1 >= 35 && tileId1 <= 38;
                boolean firstIsSeason = tileId2 >= 35 && tileId2 <= 38;
                if (selIsSeason && firstIsSeason) match = true;
                else {
                    // Flower tiles: IDs 39+ (up to 42 or 43 based on k generation)
                    boolean selIsFlower = tileId1 >= 39 && tileId1 <= 43;
                    boolean firstIsFlower = tileId2 >= 39 && tileId2 <= 43;
                    if (selIsFlower && firstIsFlower) match = true;
                }
            }

            if (match) {
                setField(secondSelectedTileCandidate, -Math.abs(tileId1));
                setField(firstSelectedTile, -Math.abs(tileId2));
                moves.add(firstSelectedTile); // Add in order of selection
                moves.add(secondSelectedTileCandidate);
                matchedTilesCount += 2;

                firstSelectedTile = new Vector3i(-1, -1, -1);
                secondSelectedTileCandidate = new Vector3i(-1, -1, -1);
                updateOpenSelectableTilesAndKeyboardFocus(); // Game state changed
            } else {
                // No match, so the current selection becomes the first selected tile for the next attempt
                firstSelectedTile = secondSelectedTileCandidate;
            }
        } else {
            // Not enough tiles selected yet, or first tile was invalid; current selection becomes the first.
            firstSelectedTile = secondSelectedTileCandidate;
        }
        needsRedraw = true;
    }
    
    private void updateOpenSelectableTilesAndKeyboardFocus() {
        openSelectableTiles.clear();
        for (int z = 0; z < BOARD_MAX_Z_RAW; z++) {
            for (int y = 0; y < BOARD_MAX_Y_RAW; y++) { // Iterate for a consistent order
                for (int x = 0; x < BOARD_MAX_X_RAW; x++) {
                    if (getField(x, y, z) > 0 && isOpen(x, y, z)) {
                        openSelectableTiles.add(new Vector3i(x, y, z));
                    }
                }
            }
        }
        // Sort tiles: Z ascending, then Y ascending, then X ascending.
        // This provides a predictable navigation order.
        openSelectableTiles.sort(Comparator.comparingInt((Vector3i v) -> v.z)
                                     .thenComparingInt(v -> v.y)
                                     .thenComparingInt(v -> v.x));

        if (openSelectableTiles.isEmpty()) {
            keyboardFocusedTile = null;
            currentKeyboardFocusIndex = -1;
            if (matchedTilesCount == totalTiles && totalTiles > 0) {
                 currentGameState = GameStatus.WON;
            } else if (totalTiles > 0) { // Tiles remain but none are open
                 currentGameState = GameStatus.LOST_STUCK;
            }
        } else {
            if (keyboardFocusedTile != null && openSelectableTiles.contains(keyboardFocusedTile)) {
                currentKeyboardFocusIndex = openSelectableTiles.indexOf(keyboardFocusedTile);
            } else {
                currentKeyboardFocusIndex = 0;
                keyboardFocusedTile = openSelectableTiles.get(0);
            }
        }
        // Further check: if openSelectableTiles.size() >=2, are there any possible matches?
        // For simplicity, we'll rely on player finding them or getting stuck.
        // A true "stuck" state requires checking all pairs of open tiles.
        if (currentGameState == GameStatus.PLAYING && !openSelectableTiles.isEmpty() && !hasPossibleMoves()) {
            currentGameState = GameStatus.LOST_STUCK;
        }
    }
    
    private boolean hasPossibleMoves() {
        if (openSelectableTiles.size() < 2) return false;
        for (int i = 0; i < openSelectableTiles.size(); i++) {
            for (int j = i + 1; j < openSelectableTiles.size(); j++) {
                Vector3i tileA = openSelectableTiles.get(i);
                Vector3i tileB = openSelectableTiles.get(j);
                int idA = getField(tileA);
                int idB = getField(tileB);

                if (idA == idB) return true;
                boolean tileAIsSeason = idA >= 35 && idA <= 38;
                boolean tileBIsSeason = idB >= 35 && idB <= 38;
                if (tileAIsSeason && tileBIsSeason) return true;
                boolean tileAIsFlower = idA >= 39 && idA <= 43;
                boolean tileBIsFlower = idB >= 39 && idB <= 43;
                if (tileAIsFlower && tileBIsFlower) return true;
            }
        }
        return false;
    }


    private void renderGame() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        }
        if (tilesSpriteSheet == null) return;

        // Render tiles
        for (int z = 0; z < BOARD_MAX_Z_RAW; z++) {
            for (int x_draw = BOARD_MAX_X_RAW - 1; x_draw >= 0; x_draw--) {
                for (int y_draw = 0; y_draw < BOARD_MAX_Y_RAW; y_draw++) {
                    int tileId = getField(x_draw, y_draw, z);
                    if (tileId <= 0) continue; // Empty or removed

                    Vector3i currentTilePos = new Vector3i(x_draw, y_draw, z);
                    int spriteCol = tileId - 1;
                    int spriteRow = 0; // Normal tile image

                    if (isOpen(x_draw, y_draw, z)) {
                        spriteRow = 1; // Highlighted version from sprite sheet
                    }

                    double sx = spriteCol * tileWidth;
                    double sy = spriteRow * tileHeight;
                    if (sx < 0 || sy < 0 || sx + tileWidth > tilesSpriteSheet.getWidth() || sy + tileHeight > tilesSpriteSheet.getHeight()) {
                        continue;
                    }

                    double drawX = x_draw * stepX + (z * offX) + deskOffset;
                    double drawY = y_draw * stepY - (z * offY);

                    gc.drawImage(tilesSpriteSheet, sx, sy, tileWidth, tileHeight, drawX, drawY, tileWidth, tileHeight);

                    // Additional highlight for first selected tile (v2)
                    if (currentTilePos.equals(firstSelectedTile)) {
                        gc.setStroke(Color.BLUE);
                        gc.setLineWidth(2);
                        gc.strokeRect(drawX, drawY, tileWidth, tileHeight);
                    }
                    // Additional highlight for keyboard focused tile
                    if (currentTilePos.equals(keyboardFocusedTile)) {
                        gc.setStroke(Color.YELLOW);
                        gc.setLineWidth(2.5); // Slightly thicker
                        gc.strokeRect(drawX -1, drawY -1 , tileWidth + 2, tileHeight + 2); // Offset for visibility
                    }
                }
            }
        }
        
        // Game Status Message
        if (currentGameState != GameStatus.PLAYING) {
            gc.setFill(new Color(0,0,0,0.7));
            gc.fillRect(0, gameCanvas.getHeight()/2 - 50, gameCanvas.getWidth(), 100);
            gc.setFont(Font.font("Arial", 40));
            gc.setFill(Color.WHITE);
            String message = "";
            if (currentGameState == GameStatus.WON) message = "YOU WIN!";
            else if (currentGameState == GameStatus.LOST_STUCK) message = "GAME OVER - No more moves";
            
            double textWidth = gc.getFont().getSize() * message.length() * 0.6; // Approximate
            gc.fillText(message, gameCanvas.getWidth()/2 - textWidth/2, gameCanvas.getHeight()/2 + 15);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


