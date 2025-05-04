package app;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected double x, y;
    protected Image image;

    public GameObject(double x, double y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public abstract void update(double delta, GameConfig gameConfig);
    public abstract void render(GraphicsContext gc);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    protected double getWidth() {
        return image != null ? image.getWidth() : 0;
    }

    protected double getHeight() {
        return image != null ? image.getHeight() : 0;
    }
}