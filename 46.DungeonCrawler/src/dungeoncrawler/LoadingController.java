package dungeoncrawler;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingController {
    @FXML private Circle loaderCircle;

    @FXML
    public void initialize() {
        RotateTransition rt = new RotateTransition(Duration.seconds(2), loaderCircle);
        rt.setByAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.play();
    }
}
