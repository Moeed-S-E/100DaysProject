package app;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Pipe {
    List<Point> dirs; // Connections: e.g., (0,-1) for Up
    int orientation;  // Target orientation for rotation (increments, actual angle is orientation * 90)
    double currentAngle; // Current visual angle for smooth animation
    boolean on;       // Whether water/signal is flowing through this pipe

    // Static constants for directions
    public static final Point UP = new Point(0, -1);
    public static final Point DOWN = new Point(0, 1);
    public static final Point RIGHT = new Point(1, 0);
    public static final Point LEFT = new Point(-1, 0);
    public static final Point[] DIRS_CLOCKWISE_ORDER = {UP, RIGHT, DOWN, LEFT};


    public Pipe() {
        dirs = new ArrayList<>();
        orientation = 0;
        currentAngle = 0;
        on = false;
    }

    public void rotateConnections() {
        for (int i = 0; i < dirs.size(); i++) {
            Point currentDir = dirs.get(i);
            if (currentDir.equals(UP)) dirs.set(i, RIGHT);
            else if (currentDir.equals(RIGHT)) dirs.set(i, DOWN);
            else if (currentDir.equals(DOWN)) dirs.set(i, LEFT);
            else if (currentDir.equals(LEFT)) dirs.set(i, UP);
        }
    }

    public boolean isConnectedTo(Point dir) {
        for (Point d : dirs) {
            if (d.equals(dir)) return true;
        }
        return false;
    }

    public String getConnectionsKey() {
        StringBuilder sb = new StringBuilder();
        for (Point dirToCheck : DIRS_CLOCKWISE_ORDER) {
            sb.append(isConnectedTo(dirToCheck) ? '1' : '0');
        }
        return sb.toString();
    }

    public static Point negative(Point p) {
        return new Point(-p.x, -p.y);
    }
}
