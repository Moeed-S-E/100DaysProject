package app;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Platform {
    private double x;
    private double y; 
    private double width; 
    private double height; 
    private Rectangle platformShape; 

    public Platform(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;


        platformShape = new Rectangle(x, y, width, height);
        platformShape.setFill(color);
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

    public Rectangle getPlatformShape() {
        return platformShape;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
        platformShape.setX(x);
    }

    public void setY(double y) {
        this.y = y;
        platformShape.setY(y);
    }

    // Method to move the platform left
    public void moveLeft(double distance) {
        x -= distance;
        if (x < 0) {
            x = 0; 
        }
        platformShape.setX(x);
    }

    // Method to move the platform right
    public void moveRight(double distance, double boundaryWidth) {
        x += distance;
        if (x + width > boundaryWidth) {
            x = boundaryWidth - width; 
        }
        platformShape.setX(x);
    }
}
