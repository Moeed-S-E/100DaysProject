package dungeoncrawler;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.Node;

public class Player extends GameEntity {
    public Player(int x, int y) { super(x, y); }
    @Override
    public Node getView() {
        Circle c = new Circle(20, Color.web("#a259c4"));
        c.getStyleClass().add("player");
        return c;
    }
}
