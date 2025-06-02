package dungeoncrawler;

import javafx.scene.layout.StackPane;

public class GameCell extends StackPane {
    private GameEntity entity;
    public GameCell() {
        setPrefSize(60, 60);
        getStyleClass().add("grid-cell");
    }
    public void setEntity(GameEntity entity) {
        this.entity = entity;
        getChildren().clear();
        if (entity != null) getChildren().add(entity.getView());
    }
    public boolean isEmpty() {
        return entity == null;
    }
    public GameEntity getEntity() {
        return entity;
    }
}
