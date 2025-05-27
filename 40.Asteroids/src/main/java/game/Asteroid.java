package game;

public class Asteroid extends Entity {
    private static final int W = 1200, H = 800;

    public Asteroid() {
        name = "asteroid";
        dx = (float) (Math.random() * 8 - 4);
        dy = (float) (Math.random() * 8 - 4);
    }

    @Override
    public void update() {
        x += dx;
        y += dy;
        if (x > W) x = 0; if (x < 0) x = W;
        if (y > H) y = 0; if (y < 0) y = H;
    }
}