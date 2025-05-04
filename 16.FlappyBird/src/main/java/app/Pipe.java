package app;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Pipe extends GameObject {
    private Image upPipe;
    private double gapY;
    private boolean scored;
    private GameConfig gameConfig;

    public Pipe(double x, Image upPipe, Image downPipe, GameConfig gameConfig) {
        super(x, 0, downPipe);
        this.upPipe = upPipe;
        this.gapY = new Random().nextDouble() * (gameConfig.HEIGHT - gameConfig.GAP - 200) + 100;
        this.scored = false;
        this.gameConfig = gameConfig;
    }

    @Override
    public void update(double delta, GameConfig config) {
        x -= gameConfig.PIPE_SPEED;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(upPipe, x, gapY - gameConfig.GAP / 2 - upPipe.getHeight());
        gc.drawImage(image, x, gapY + gameConfig.GAP / 2);
    }

    public boolean isOffScreen() {
        return x + getWidth() < 0;
    }

    public boolean hasPassed(double birdX) {
        return x + getWidth() < birdX;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public double getGapY() {
        return gapY;
    }
}