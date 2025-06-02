package dungeoncrawler;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.Node;

public class Enemy extends GameEntity {
    public Enemy(int x, int y) { super(x, y); }
    @Override
    public Node getView() {
        Circle c = new Circle(20, Color.web("#ff4d4d"));
        c.getStyleClass().add("enemy");
        return c;
    }
}
