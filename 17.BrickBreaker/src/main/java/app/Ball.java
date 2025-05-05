package app;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball {
    private double x; 
    private double y; 
    private double radius; 
    private double dx; 
    private double dy; 
    private Circle ballShape;

    public Ball(double x, double y, double radius, double dx, double dy, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;

        // Create a circle to represent the ball visually
        ballShape = new Circle(radius);
        ballShape.setFill(color);
        ballShape.setCenterX(x);
        ballShape.setCenterY(y);
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public Circle getBallShape() {
        return ballShape;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
        ballShape.setCenterX(x);
    }

    public void setY(double y) {
        this.y = y;
        ballShape.setCenterY(y);
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }


    public void updatePosition() {
        x += dx;
        y += dy;
        ballShape.setCenterX(x);
        ballShape.setCenterY(y);
    }

    
    public void checkBoundaryCollision(double width, double height) {
        if (x - radius <= 0 || x + radius >= width) {
            dx *= -1; // Reverse X direction
        }
        if (y - radius <= 0 || y + radius >= height) {
            dy *= -1; 
        }
    }
}