package game;

public class Player extends Entity {
    protected boolean thrust;
    private int lives;
    private static final int W = 1200, H = 800;
    private static final float DEGTORAD = 0.017453f;

    public Player() {
        name = "player";
        dx = 0;
        dy = 0;
        lives = 3; // Initialize with 3 lives
    }

    @Override
    public void update() {
        if (thrust) {
            dx += Math.cos(angle * DEGTORAD) * 0.2f;
            dy += Math.sin(angle * DEGTORAD) * 0.2f;
        } else {
            dx *= 0.99f;
            dy *= 0.99f;
        }
        float maxSpeed = 15;
        float speed = (float) Math.sqrt(dx * dx + dy * dy);
        if (speed > maxSpeed) {
            dx *= maxSpeed / speed;
            dy *= maxSpeed / speed;
        }
        x += dx;
        y += dy;
        if (x > W) x = 0; if (x < 0) x = W;
        if (y > H) y = 0; if (y < 0) y = H;
    }

    public void setThrust(boolean thrust) {
        this.thrust = thrust;
    }

    public void reset() {
        x = W / 2;
        y = H / 2;
        angle = 0;
        dx = 0;
        dy = 0;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
    }
}