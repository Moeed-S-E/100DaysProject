package dungeoncrawler;

import javafx.scene.Node;

public abstract class GameEntity {
    int x, y;

    public GameEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract Node getView();
}
