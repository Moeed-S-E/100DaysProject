package app;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Floor extends GameObject {
    private double x2;

    public Floor(double height, Image image) {
        super(0, height - image.getHeight(), image);
        this.x2 = image.getWidth();
    }

    @Override
    public void update(double delta, GameConfig gameConfig) {
        double speed = gameConfig.PIPE_SPEED * delta * 30; // Adjust speed for smoother scrolling
        x -= speed;
        x2 -= speed;


        if (x <= -image.getWidth()) {
            x += image.getWidth() * 2;
        }
        if (x2 <= -image.getWidth()) {
            x2 += image.getWidth() * 2;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Render the current visible images
        gc.drawImage(image, x, y);
        gc.drawImage(image, x2, y);

        if (x <= 0) {
            gc.drawImage(image, x + image.getWidth() * 2, y);
        }
        if (x2 <= 0) {
            gc.drawImage(image, x2 + image.getWidth() * 2, y);
        }
    }
}


