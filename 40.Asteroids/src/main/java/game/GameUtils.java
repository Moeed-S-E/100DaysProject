package game;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameUtils {
    public static boolean isCollide(Entity a, Entity b) {
        return (b.getX() - a.getX()) * (b.getX() - a.getX()) +
               (b.getY() - a.getY()) * (b.getY() - a.getY()) <
               (a.getRadius() + b.getRadius()) * (a.getRadius() + b.getRadius());
    }

    public static BufferedImage loadImage(String path) {
        try {
            BufferedImage img = ImageIO.read(GameUtils.class.getResource(path));
            if (img == null) {
                throw new IOException("Image not found: " + path);
            }
            return img;
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
            return null;
        }
    }
}