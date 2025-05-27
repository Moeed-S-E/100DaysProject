package game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entity {
    protected float x, y, dx, dy, radius, angle;
    protected boolean life;
    protected String name;
    protected Animation anim;

    public Entity() {
        life = true;
    }

    public void settings(Animation a, int x, int y, float angle, int radius) {
        this.anim = a;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.radius = radius;
    }

    public abstract void update();

    public void draw(Graphics2D g2d) {
        Rectangle frame = anim.getCurrentFrame();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle + 90));
        g2d.drawImage(anim.getSprite(), -frame.width / 2, -frame.height / 2, frame.width, frame.height, 
                      frame.x, frame.y, frame.x + frame.width, frame.y + frame.height, null);
        g2d.rotate(-Math.toRadians(angle + 90));
        g2d.translate(-x, -y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getRadius() { return radius; }
    public String getName() { return name; }
    public boolean isAlive() { return life; }
    public void setAlive(boolean alive) { life = alive; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }
}