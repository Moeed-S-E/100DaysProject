package app;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LudoPiece extends Circle {
    public final int player; // 0 = Blue, 1 = Green
    public final int index;  // 0-3 for each player's piece
    public int position;     // Board position

    public LudoPiece(int player, int index, int position, Color color) {
        super(LudoConstants.SQUARE_SIZE * 0.5, color);
        this.player = player;
        this.index = index;
        this.position = position;
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(2);
        this.getStyleClass().add("player-piece");
        this.getStyleClass().add(player == 0 ? "player-p1" : "player-p2");
    }
}
