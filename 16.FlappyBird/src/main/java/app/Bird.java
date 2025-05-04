package app;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bird extends GameObject {
    private double velocity;
    private Image[] frames;
    private int frameCount;
    private GameConfig gameConfig;
    private static final double SCALE = 0.6;

    public Bird(double x, double y, Image frame1, Image frame2, Image frame3, GameConfig gameConfig) {
        super(x, y, frame1);
        this.frames = new Image[]{frame1, frame2, frame3};
        this.velocity = 0;
        this.frameCount = 0;
        this.gameConfig = gameConfig;
    }

    public void jump() {
        velocity = -10;
    }

    @Override
    public void update(double delta, GameConfig config) {
        frameCount++;
        velocity += gameConfig.GRAVITY * delta * 60;
        y += velocity * delta * 60;
        image = frames[(frameCount / 10) % 3];
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.scale(SCALE, SCALE);
        gc.drawImage(image, x / SCALE, y / SCALE);
        gc.restore();
    }

    public boolean collidesWith(Pipe pipe) {
        double scaledWidth = getWidth() * SCALE;
        double scaledHeight = getHeight() * SCALE;
        double birdRight = x + scaledWidth;
        double birdBottom = y + scaledHeight;
        double pipeLeft = pipe.getX();
        double pipeRight = pipe.getX() + pipe.getWidth();
        double gapTop = pipe.getGapY() - gameConfig.GAP / 2;
        double gapBottom = pipe.getGapY() + gameConfig.GAP / 2;

        return (birdRight > pipeLeft && x < pipeRight) &&
               (y < gapTop || birdBottom > gapBottom);
    }

    @Override
    protected double getWidth() {
        return super.getWidth();
    }

    @Override
    protected double getHeight() {
        return super.getHeight();
    }
}