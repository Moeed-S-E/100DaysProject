package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ClockHand {
    private double length;
    private double angle;
    private Color color;
    private double thickness;

    public ClockHand(double length, Color color, double thickness) {
        this.length = length;
        this.color = color;
        this.thickness = thickness;
    }

    public void updateAngle(double angle) {
        this.angle = angle;
    }

    public void draw(GraphicsContext gc, double centerX, double centerY) {
        gc.setStroke(color);
        gc.setLineWidth(thickness);
        double radians = Math.toRadians(angle - 90); // Adjust for 12 o'clock at top
        double endX = centerX + length * Math.cos(radians);
        double endY = centerY + length * Math.sin(radians);
        gc.strokeLine(centerX, centerY, endX, endY);
    }
}