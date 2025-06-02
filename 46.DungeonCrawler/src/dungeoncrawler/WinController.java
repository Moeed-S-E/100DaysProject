package dungeoncrawler;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WinController {
    @FXML private Label scoreLabel;

    private Runnable playAgainCallback;

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setPlayAgainCallback(Runnable callback) {
        this.playAgainCallback = callback;
    }

    @FXML
    private void playAgain() {
        if (playAgainCallback != null) playAgainCallback.run();
    }
}
