package application;

import java.time.LocalTime;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Clock {
    private Canvas canvas;
    private GraphicsContext gc;
    private ClockHand hourHand;
    private ClockHand minuteHand;
    private ClockHand secondHand;
    private double centerX;
    private double centerY;
    private double radius;

    public Clock(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        centerX = width / 2;
        centerY = height / 2;
        radius = Math.min(width, height) / 2 * 0.8;
        hourHand = new ClockHand(radius * 0.5, Color.WHITE, 6);
        minuteHand = new ClockHand(radius * 0.7, Color.GRAY, 4);
        secondHand = new ClockHand(radius * 0.9, Color.RED, 2);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void update() {
        LocalTime time = LocalTime.now();
        double hours = time.getHour() % 12 + time.getMinute() / 60.0;
        double minutes = time.getMinute() + time.getSecond() / 60.0;
        double seconds = time.getSecond();

        hourHand.updateAngle(hours * 30); // 360 / 12 hours
        minuteHand.updateAngle(minutes * 6); // 360 / 60 minutes
        secondHand.updateAngle(seconds * 6); // 360 / 60 seconds

        draw();
    }

    private void draw() {
        // Clear canvas
        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw clock face
        gc.setFill(Color.rgb(15, 51, 39));
        gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Draw hour markers as circles
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            double markerX = centerX + (radius * 0.85) * Math.cos(angle);
            double markerY = centerY + (radius * 0.85) * Math.sin(angle);
            gc.setFill(Color.WHITE);
            gc.fillOval(markerX - 5, markerY - 5, 10, 10); // Circles as hour markers
        }

        // Draw minute markers as lines
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6 - 90);
            double outerX = centerX + radius * Math.cos(angle);
            double outerY = centerY + radius * Math.sin(angle);
            double innerX = centerX + (radius * 0.95) * Math.cos(angle);
            double innerY = centerY + (radius * 0.95) * Math.sin(angle);
            gc.setStroke(i % 5 == 0 ? Color.GRAY : Color.DARKGRAY); // Highlight hour lines
            gc.setLineWidth(1);
            gc.strokeLine(innerX, innerY, outerX, outerY);
        }

        // Draw hands
        hourHand.draw(gc, centerX, centerY);
        minuteHand.draw(gc, centerX, centerY);
        secondHand.draw(gc, centerX, centerY);

        // Draw center dot
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - 5, centerY - 5, 10, 10);
    }
}
