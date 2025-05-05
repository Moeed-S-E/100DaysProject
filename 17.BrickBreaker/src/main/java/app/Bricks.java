package app;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bricks {
    private double x;
    private double y; 
    private double width;
    private double height; 
    private int durability;
    private Rectangle brickShape;

    public Bricks(double x, double y, double width, double height, int durability, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.durability = durability;

        // Create a rectangle to represent the brick visually
        brickShape = new Rectangle(x, y, width, height);
        brickShape.setFill(color);
        brickShape.setStroke(Color.BLACK); // Add a border for better visibility
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getDurability() {
        return durability;
    }

    public Rectangle getBrickShape() {
        return brickShape;
    }

    // Reduce durability when hit
    public void hit() {
        if (durability > 0) {
            durability--;
        }
        if (durability == 0) {
            brickShape.setVisible(false); // Hide the brick when broken
        }
    }

    // Check if the brick is still active
    public boolean isActive() {
        return durability > 0;
    }
}
