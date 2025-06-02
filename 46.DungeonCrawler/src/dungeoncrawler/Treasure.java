package dungeoncrawler;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.Node;

public class Treasure extends GameEntity {
    public Treasure(int x, int y) { super(x, y); }
    @Override
    public Node getView() {
        Circle c = new Circle(15, Color.GOLD);
        c.getStyleClass().add("treasure");
        return c;
    }
}
