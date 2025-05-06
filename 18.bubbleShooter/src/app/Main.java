package app;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    private static final int WIDTH = 628;
    private static final int HEIGHT = 628;
    private static final int BUBBLE_COLORS = 7;

    private Canvas canvas;
    private GraphicsContext gc;
    private Image bubbleImage;
    private Level level;
    private Player player;
    private GameState gameState;
    private int score = 0;
    private int turnCounter = 0;
    private int rowOffset = 0;
    private double fps;
    @SuppressWarnings("unused")
	private long lastFrameTime;
    private double fpsTime = 0;
    private int frameCount = 0;
    private List<Tile> cluster = new ArrayList<>();
    private List<List<Tile>> floatingClusters = new ArrayList<>();
    private Random random = new Random();

    enum GameState {
        INIT, READY, SHOOT_BUBBLE, REMOVE_CLUSTER, GAME_OVER
    }

    static class Level {
        double x = 4;
        double y = 83;
        double width;
        double height;
        int columns = 15;
        int rows = 14;
        double tileWidth = 40;
        double tileHeight = 40;
        double rowHeight = 34;
        double radius = 20;
        Tile[][] tiles;

        Level() {
            width = columns * tileWidth + tileWidth / 2;
            height = (rows - 1) * rowHeight + tileHeight;
            tiles = new Tile[columns][rows];
            for (int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                    tiles[i][j] = new Tile(i, j, -1, 0);
                }
            }
        }
    }

    static class Tile {
        int x, y, type;
        boolean removed = false;
        double shift = 0;
        double velocity = 0;
        double alpha = 1;
        boolean processed = false;

        Tile(int x, int y, int type, double shift) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.shift = shift;
        }
    }

    static class Bubble {
        double x, y, angle, speed;
        int tileType;
        boolean visible;

        Bubble() {
            speed = 1000;
            tileType = 0;
            visible = false;
        }
    }

    static class Player {
        double x, y, angle;
        int tileType;
        Bubble bubble = new Bubble();
        Bubble nextBubble = new Bubble();

        Player() {
            angle = 90;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        Group root = new Group(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle("Bubble Shooter Clone");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

//        Images load and handle Exceptions 
        try {
        	bubbleImage = new Image(getClass().getResourceAsStream("bubble-sprites.png"), 40 * BUBBLE_COLORS, 40, true, true);
            if (bubbleImage.isError()) {
                System.err.println("Failed to load bubble-sprites.png: Image error detected.");
                bubbleImage = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading bubble-sprites.png: " + e.getMessage());
            bubbleImage = null;
        }

        if (bubbleImage == null) {
            System.err.println("Using a default color rectangle as fallback (no image loaded).");
        }

        initGame();
        canvas.setOnMouseMoved(this::onMouseMove);
        canvas.setOnMousePressed(this::onMouseDown);

        new AnimationTimer() {
            long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                update(dt);
                render();
            }
        }.start();
    }

    private void initGame() {
        level = new Level();
        player = new Player();
        player.x = level.x + level.width / 2 - level.tileWidth / 2;
        player.y = level.y + level.height;
        player.nextBubble.x = player.x - 2 * level.tileWidth;
        player.nextBubble.y = player.y;
        newGame();
    }

    private void newGame() {
        score = 0;
        turnCounter = 0;
        rowOffset = 0;
        gameState = GameState.READY;
        createLevel();
        nextBubble();
        nextBubble();
    }

    private void createLevel() {
        for (int j = 0; j < level.rows; j++) {
            int randomTile = randRange(0, BUBBLE_COLORS - 1);
            int count = 0;
            for (int i = 0; i < level.columns; i++) {
                if (count >= 2) {
                    int newTile = randRange(0, BUBBLE_COLORS - 1);
                    if (newTile == randomTile) newTile = (newTile + 1) % BUBBLE_COLORS;
                    randomTile = newTile;
                    count = 0;
                }
                count++;
                level.tiles[i][j].type = (j < level.rows / 2) ? randomTile : -1;
            }
        }
    }

    private void update(double dt) {
        updateFps(dt);
        switch (gameState) {
            case READY:
                break;
            case SHOOT_BUBBLE:
                stateShootBubble(dt);
                break;
            case REMOVE_CLUSTER:
                stateRemoveCluster(dt);
                break;
            case GAME_OVER:
                break;
		default:
			break;
        }
    }

    private void stateShootBubble(double dt) {
        player.bubble.x += dt * player.bubble.speed * Math.cos(Math.toRadians(player.bubble.angle));
        player.bubble.y -= dt * player.bubble.speed * Math.sin(Math.toRadians(player.bubble.angle));

        // Bounce off walls
        if (player.bubble.x <= level.x || player.bubble.x + level.tileWidth >= level.x + level.width) {
            player.bubble.angle = 180 - player.bubble.angle;
        }

        // Check if bubble hits the top or another bubble
        if (player.bubble.y <= level.y || checkCollisionWithGrid()) {
            snapBubble();
            return;
        }
    }

    private boolean checkCollisionWithGrid() {
        for (int i = 0; i < level.columns; i++) {
            for (int j = 0; j < level.rows; j++) {
                Tile tile = level.tiles[i][j];
                if (tile.type < 0) continue;

                Point2D coord = getTileCoordinate(i, j);
                if (circleIntersection(
                        player.bubble.x + level.radius,
                        player.bubble.y + level.radius,
                        level.radius,
                        coord.getX() + level.radius,
                        coord.getY() + level.radius,
                        level.radius)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void snapBubble() {
        Point2D gridPos = getGridPosition(player.bubble.x + level.radius, player.bubble.y + level.radius);
        int gridX = (int) Math.max(0, Math.min(gridPos.getX(), level.columns - 1));
        int gridY = (int) Math.max(0, Math.min(gridPos.getY(), level.rows - 1));

        // Find the first available position below the target grid cell
        while (gridY < level.rows && level.tiles[gridX][gridY].type != -1) {
            gridY++;
        }
        if (gridY >= level.rows) {
            gridY = level.rows - 1;
        }

        // Place the bubble in the grid
        player.bubble.visible = false;
        level.tiles[gridX][gridY].type = player.bubble.tileType;

        // Check for clusters and remove if necessary
        cluster = findCluster(gridX, gridY, true, true, false);
        if (cluster.size() >= 3) {
            for (Tile tile : cluster) {
                tile.removed = true;
            }
            floatingClusters = findFloatingClusters();
            gameState = GameState.REMOVE_CLUSTER;
            return;
        }

        // No cluster found, proceed to the next turn
        turnCounter++;
        if (turnCounter >= 5) {
            addBubbles();
            turnCounter = 0;
            rowOffset = (rowOffset + 1) % 2;
        }

        nextBubble();
        gameState = GameState.READY;
    }

//    private void stateRemoveCluster(double dt) {
//        if (cluster.isEmpty() && floatingClusters.isEmpty()) {
//            nextBubble();
//            gameState = GameState.READY;
//            return;
//        }
//
//        boolean tilesLeft = false;
//
//        // Fade out cluster tiles
//        for (Tile tile : cluster) {
//            if (!tile.removed) continue;
//            tilesLeft = true;
//            tile.alpha -= dt * 15;
//            if (tile.alpha <= 0) {
//                tile.type = -1;
//                tile.alpha = 1.0;
//                tile.removed = false;
//            }
//        }
//
//        // Drop floating clusters
//        for (List<Tile> floatingCluster : floatingClusters) {
//            for (Tile tile : floatingCluster) {
//                if (!tile.removed) continue;
//                tilesLeft = true;
//                tile.velocity += dt * 700;
//                tile.shift += dt * tile.velocity;
//                tile.alpha -= dt * 8;
//
//                if (tile.alpha <= 0 || tile.y * level.rowHeight + tile.shift > (level.rows - 1) * level.rowHeight + level.tileHeight) {
//                    tile.type = -1;
//                    tile.shift = 0;
//                    tile.alpha = 1.0;
//                    tile.removed = false;
//                }
//            }
//        }
//
//        if (!tilesLeft) {
//            score += cluster.size() * 100;
//            for (List<Tile> floatingCluster : floatingClusters) {
//                score += floatingCluster.size() * 100;
//            }
//            resetRemoved();
//            cluster.clear();
//            floatingClusters.clear();
//            if (checkGameOver()) return;
//            gameState = GameState.READY;
//        }
//    }

    private void stateRemoveCluster(double dt) {
        if (cluster.isEmpty() && floatingClusters.isEmpty()) {
            // Add score before moving to next bubble
            score += cluster.size() * 100;
            for (List<Tile> floatingCluster : floatingClusters) {
                score += floatingCluster.size() * 100;
            }
            resetRemoved();
            cluster.clear();
            floatingClusters.clear();
            
            // Check game over before proceeding
            if (checkGameOver()) return;
            
            nextBubble();
            gameState = GameState.READY;
            return;
        }

        boolean tilesLeft = false;

        // Fade out cluster tiles
        for (Tile tile : cluster) {
            if (!tile.removed) continue;
            tilesLeft = true;
            tile.alpha -= dt * 15;
            if (tile.alpha <= 0) {
                tile.type = -1;
                tile.alpha = 1.0;
                tile.removed = false;
            }
        }

        // Drop floating clusters
        for (List<Tile> floatingCluster : floatingClusters) {
            for (Tile tile : floatingCluster) {
                if (!tile.removed) continue;
                tilesLeft = true;
                tile.velocity += dt * 700;
                tile.shift += dt * tile.velocity;
                tile.alpha -= dt * 8;

                if (tile.alpha <= 0 || tile.y * level.rowHeight + tile.shift > (level.rows - 1) * level.rowHeight + level.tileHeight) {
                    tile.type = -1;
                    tile.shift = 0;
                    tile.alpha = 1.0;
                    tile.removed = false;
                }
            }
        }

        if (!tilesLeft) {
            score += cluster.size() * 100;
            for (List<Tile> floatingCluster : floatingClusters) {
                score += floatingCluster.size() * 100;
            }
            resetRemoved();
            cluster.clear();
            floatingClusters.clear();
            if (checkGameOver()) return;
            nextBubble();
            gameState = GameState.READY;
        }
    }
    
    private boolean checkGameOver() {
        for (int i = 0; i < level.columns; i++) {
            if (level.tiles[i][level.rows - 1].type != -1) {
                gameState = GameState.GAME_OVER;
                return true;
            }
        }
        return false;
    }

    private void addBubbles() {
        for (int i = 0; i < level.columns; i++) {
            for (int j = level.rows - 1; j > 0; j--) {
                level.tiles[i][j].type = level.tiles[i][j - 1].type;
            }
            level.tiles[i][0].type = getExistingColor();
        }
    }

    private int getExistingColor() {
        List<Integer> colors = findColors();
        return colors.isEmpty() ? 0 : colors.get(randRange(0, colors.size() - 1));
    }

    private List<Integer> findColors() {
        List<Integer> foundColors = new ArrayList<>();
        boolean[] colorTable = new boolean[BUBBLE_COLORS];
        for (int i = 0; i < level.columns; i++) {
            for (int j = 0; j < level.rows; j++) {
                int type = level.tiles[i][j].type;
                if (type >= 0 && !colorTable[type]) {
                    colorTable[type] = true;
                    foundColors.add(type);
                }
            }
        }
        return foundColors;
    }

    private List<Tile> findCluster(int tx, int ty, boolean matchType, boolean reset, boolean skipRemoved) {
        if (reset) resetProcessed();
        Tile targetTile = level.tiles[tx][ty];
        List<Tile> toProcess = new ArrayList<>();
        toProcess.add(targetTile);
        targetTile.processed = true;
        List<Tile> foundCluster = new ArrayList<>();

        while (!toProcess.isEmpty()) {
            Tile currentTile = toProcess.remove(0);
            if (currentTile.type == -1 || (skipRemoved && currentTile.removed)) continue;
            if (!matchType || currentTile.type == targetTile.type) {
                foundCluster.add(currentTile);
                List<Tile> neighbors = getNeighbors(currentTile);
                for (Tile neighbor : neighbors) {
                    if (!neighbor.processed) {
                        toProcess.add(neighbor);
                        neighbor.processed = true;
                    }
                }
            }
        }
        return foundCluster;
    }

    private List<List<Tile>> findFloatingClusters() {
        resetProcessed();
        List<List<Tile>> foundClusters = new ArrayList<>();
        for (int i = 0; i < level.columns; i++) {
            for (int j = 0; j < level.rows; j++) {
                Tile tile = level.tiles[i][j];
                if (!tile.processed) {
                    List<Tile> foundCluster = findCluster(i, j, false, false, true);
                    if (foundCluster.isEmpty()) continue;

                    boolean floating = true;
                    for (Tile t : foundCluster) {
                        if (t.y == 0 || !t.removed) {
                            floating = false;
                            break;
                        }
                    }
                    if (floating) foundClusters.add(foundCluster);
                }
            }
        }
        return foundClusters;
    }

    private void resetProcessed() {
        for (int i = 0; i < level.columns; i++) {
            for (int j = 0; j < level.rows; j++) {
                level.tiles[i][j].processed = false;
            }
        }
    }

    private void resetRemoved() {
        for (int i = 0; i < level.columns; i++) {
            for (int j = 0; j < level.rows; j++) {
                level.tiles[i][j].removed = false;
            }
        }
    }

    private List<Tile> getNeighbors(Tile tile) {
        int tileRow = (tile.y + rowOffset) % 2;
        int[][] offsets = tileRow == 0 ?
                new int[][]{{1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}} :
                new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 0}, {0, -1}, {1, -1}};
        List<Tile> neighbors = new ArrayList<>();
        for (int[] offset : offsets) {
            int nx = tile.x + offset[0];
            int ny = tile.y + offset[1];
            if (nx >= 0 && nx < level.columns && ny >= 0 && ny < level.rows) {
                neighbors.add(level.tiles[nx][ny]);
            }
        }
        return neighbors;
    }

//    private void nextBubble() {
//        player.tileType = player.nextBubble.tileType;
//        player.bubble.tileType = player.nextBubble.tileType;
//        player.bubble.x = player.x;
//        player.bubble.y = player.y;
//        player.bubble.visible = true;
//        player.nextBubble.tileType = getExistingColor();
//    }
    private void nextBubble() {
        player.tileType = player.nextBubble.tileType;
        player.bubble.tileType = player.nextBubble.tileType;
        player.bubble.x = player.x;
        player.bubble.y = player.y;
        player.bubble.visible = true;
        
        // Ensure we always get a valid bubble type
        List<Integer> colors = findColors();
        if (colors.isEmpty()) {
            player.nextBubble.tileType = 0; // Default color if no colors exist (shouldn't happen)
        } else {
            player.nextBubble.tileType = colors.get(randRange(0, colors.size() - 1));
        }
    }

    private void updateFps(double dt) {
        fpsTime += dt;
        frameCount++;
        if (fpsTime > 0.25) {
            fps = Math.round(frameCount / fpsTime);
            fpsTime = 0;
            frameCount = 0;
        }
    }

    private void render() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        drawFrame();
        double yOffset = level.tileHeight / 2;
        gc.setFill(Color.web("#8c8c8c"));
        gc.fillRect(level.x - 4, level.y - 4, level.width + 8, level.height + 4 - yOffset);
        renderTiles();
        gc.setFill(Color.web("#656565"));
        gc.fillRect(level.x - 4, level.y - 4 + level.height + 4 - yOffset, level.width + 8, 2 * level.tileHeight + 3);
        renderPlayer();
        drawScore();
        if (gameState == GameState.GAME_OVER) drawGameOver();
    }

    private void drawFrame() {
        gc.setFill(Color.web("#e8eaec"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.web("#303030"));
        gc.fillRect(0, 0, WIDTH, 79);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", 24));
        gc.fillText("Fps: " + fps, 13, 57);
    }

    private void renderTiles() {
        for (int j = 0; j < level.rows; j++) {
            for (int i = 0; i < level.columns; i++) {
                Tile tile = level.tiles[i][j];
                if (tile.type >= 0) {
                    Point2D coord = getTileCoordinate(i, j);
                    gc.save();
                    gc.setGlobalAlpha(tile.alpha);
                    drawBubble(coord.getX(), coord.getY() + tile.shift, tile.type);
                    gc.restore();
                }
            }
        }
    }

    private void renderPlayer() {
        double centerX = player.x + level.tileWidth / 2;
        double centerY = player.y + level.tileHeight / 2;
        gc.setFill(Color.web("#7a7a7a"));
        gc.fillOval(centerX - level.radius - 12, centerY - level.radius - 12, (level.radius + 12) * 2, (level.radius + 12) * 2);
        gc.setStroke(Color.web("#8c8c8c"));
        gc.setLineWidth(2);
        gc.strokeOval(centerX - level.radius - 12, centerY - level.radius - 12, (level.radius + 12) * 2, (level.radius + 12) * 2);
        gc.setStroke(Color.BLUE);
        gc.beginPath();
        gc.moveTo(centerX, centerY);
        gc.lineTo(centerX + 1.5 * level.tileWidth * Math.cos(Math.toRadians(player.angle)),
                centerY - 1.5 * level.tileHeight * Math.sin(Math.toRadians(player.angle)));
        gc.stroke();
        drawBubble(player.nextBubble.x, player.nextBubble.y, player.nextBubble.tileType);
        if (player.bubble.visible) drawBubble(player.bubble.x, player.bubble.y, player.bubble.tileType);
    }

    private void drawScore() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", 18));
        double scoreX = level.x + level.width - 150;
        double scoreY = level.y + level.height + level.tileHeight - level.tileHeight / 2 - 8;
        drawCenterText("Score:", scoreX, scoreY, 150);
        gc.setFont(Font.font("Verdana", 24));
        drawCenterText(String.valueOf(score), scoreX, scoreY + 30, 150);
    }

    private void drawGameOver() {
        gc.setFill(Color.color(0, 0, 0, 0.8));
        gc.fillRect(level.x - 4, level.y - 4, level.width + 8, level.height + 2 * level.tileHeight + 8 - level.tileHeight / 2);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", 24));
        drawCenterText("Game Over!", level.x, level.y + level.height / 2 + 10, level.width);
        drawCenterText("Click to start", level.x, level.y + level.height / 2 + 40, level.width);
    }

    private void drawBubble(double x, double y, int index) {
        if (bubbleImage != null && index >= 0 && index < BUBBLE_COLORS) {
            gc.drawImage(bubbleImage, index * 40, 0, 40, 40, x, y, level.tileWidth, level.tileHeight);
        } else {
            gc.setFill(Color.hsb(index * 360.0 / BUBBLE_COLORS, 0.8, 0.8));
            gc.fillOval(x, y, level.tileWidth, level.tileHeight);
        }
    }

    private void drawCenterText(String text, double x, double y, double width) {
        double textWidth = gc.getFont().getSize() * text.length() / 2; // Approximation
        gc.fillText(text, x + (width - textWidth) / 2, y);
    }

    private Point2D getTileCoordinate(int column, int row) {
        double tileX = level.x + column * level.tileWidth;
        if ((row + rowOffset) % 2 == 1) tileX += level.tileWidth / 2;
        double tileY = level.y + row * level.rowHeight;
        return new Point2D(tileX, tileY);
    }

    private Point2D getGridPosition(double x, double y) {
        int gridY = (int) Math.floor((y - level.y) / level.rowHeight);
        double xOffset = ((gridY + rowOffset) % 2 == 1) ? level.tileWidth / 2 : 0;
        int gridX = (int) Math.floor((x - xOffset - level.x) / level.tileWidth);
        return new Point2D(gridX, gridY);
    }

    private boolean circleIntersection(double x1, double y1, double r1, double x2, double y2, double r2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < r1 + r2;
    }

    private void onMouseMove(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double dx = mouseX - (player.x + level.tileWidth / 2);
        double dy = (player.y + level.tileHeight / 2) - mouseY;
        double mouseAngle = Math.toDegrees(Math.atan2(dy, dx));
        if (mouseAngle < 0) mouseAngle = 180 + (180 + mouseAngle);
        double lbound = 8, ubound = 172;
        if (mouseAngle > 90 && mouseAngle < 270) {
            if (mouseAngle > ubound) mouseAngle = ubound;
        } else {
            if (mouseAngle < lbound || mouseAngle >= 270) mouseAngle = lbound;
        }
        player.angle = mouseAngle;
    }

    private void onMouseDown(MouseEvent event) {
        if (gameState == GameState.READY) shootBubble();
        else if (gameState == GameState.GAME_OVER) newGame();
    }

    private void shootBubble() {
        player.bubble.x = player.x;
        player.bubble.y = player.y;
        player.bubble.angle = player.angle;
        player.bubble.tileType = player.tileType;
        gameState = GameState.SHOOT_BUBBLE;
    }

    private int randRange(int low, int high) {
        return low + random.nextInt(high - low + 1);
    }

    static class Point2D {
        private final double x, y;

        Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
