package game;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private float frame;
    private float speed;
    private BufferedImage sprite;
    private List<Rectangle> frames;

    public Animation(BufferedImage sprite, int x, int y, int w, int h, int count, float speed) {
        this.frame = 0;
        this.speed = speed;
        this.sprite = sprite;
        this.frames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            frames.add(new Rectangle(x + i * w, y, w, h));
        }
    }

    public void update() {
        frame += speed;
        int n = frames.size();
        if (frame >= n) frame -= n;
    }

    public boolean isEnd() {
        return frame + speed >= frames.size();
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public Rectangle getCurrentFrame() {
        return frames.get((int) frame);
    }
}