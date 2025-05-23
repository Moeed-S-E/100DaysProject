package app;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
    protected Vector2d vel;
    protected Vector2d pos;
    protected Vector2d acc;
    protected double mass;
    protected boolean visible = true;
    protected int w;
    protected int h;
    protected Image image;

    public static final double GRAVITY = 0.2;

    public Sprite(String imagePath) {
        mass = 1;
        pos = new Vector2d();
        vel = new Vector2d();
        acc = new Vector2d();
        loadImage(imagePath);
    }

    private void loadImage(String imagePath) {
        try {
        	image = new Image(getClass().getResourceAsStream("/" + imagePath), 60, 0, true, false);
            w = (int) image.getWidth();
            h = (int) image.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        acc.add(0, GRAVITY);
        vel.add(acc);
        pos.add(vel);
        acc.set(0, 0);
    }

    public void set(int x, int y) {
        pos.set(x, y);
        visible = true;
    }

    public void destroy() {
        pos.set(-1000, -1000);
        visible = false;
    }

    public void draw(GraphicsContext g, int x, int y) {
        if (visible) {
            g.drawImage(image, x, y);
        }
    }

    public void draw(GraphicsContext g) {
        draw(g, getX(), getY());
    }

    public void applyForce(double x, double y) {
        acc.add(x / mass, y / mass);
    }

    public boolean colliding(Sprite o) {
        return pos.x + w > o.pos.x && pos.x < o.pos.x + o.w &&
               pos.y + h > o.pos.y && pos.y < o.pos.y + o.h;
    }

    public int getX() {
        return (int) pos.x;
    }

    public int getY() {
        return (int) pos.y;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public Image getImage() {
        return image;
    }
}