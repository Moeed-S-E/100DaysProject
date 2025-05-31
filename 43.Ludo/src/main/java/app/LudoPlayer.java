package app;

import javafx.scene.paint.Color;

public class LudoPlayer {
    public final int playerId; // 0 = Blue, 1 = Green
    public final String name;
    public final Color color;
    public final int[] basePositions;
    public final int startPosition;
    public final int homeEntrance[];
    public final int homePosition;
    public final int turningPoint;
    public final LudoPiece[] pieces = new LudoPiece[4];

    public LudoPlayer(int playerId) {
        this.playerId = playerId;
        if (playerId == 0) {
            name = "Blue";
            color = Color.web("#2eafff");
            basePositions = LudoConstants.BLUE_BASE;
            startPosition = LudoConstants.BLUE_START;
            homeEntrance = LudoConstants.BLUE_HOME_ENTRANCE;
            homePosition = LudoConstants.BLUE_HOME;
            turningPoint = LudoConstants.BLUE_TURNING;
        } else {
            name = "Green";
            color = Color.web("#00b550");
            basePositions = LudoConstants.GREEN_BASE;
            startPosition = LudoConstants.GREEN_START;
            homeEntrance = LudoConstants.GREEN_HOME_ENTRANCE;
            homePosition = LudoConstants.GREEN_HOME;
            turningPoint = LudoConstants.GREEN_TURNING;
        }
        for (int i = 0; i < 4; i++) {
            pieces[i] = new LudoPiece(playerId, i, basePositions[i], color);
        }
    }
}
