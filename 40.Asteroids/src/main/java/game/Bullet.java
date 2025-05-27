package game;

public class Bullet extends Entity {
    private static final int W = 1200, H = 800;
    private static final float DEGTORAD = 0.017453f;

    public Bullet() {
        name = "bullet";
    }

    @Override
    public void update() {
        dx = (float) Math.cos(angle * DEGTORAD) * 6;
        dy = (float) Math.sin(angle * DEGTORAD) * 6;
        x += dx;
        y += dy;
        if (x > W || x < 0 || y > H || y < 0) life = false;
    }
}