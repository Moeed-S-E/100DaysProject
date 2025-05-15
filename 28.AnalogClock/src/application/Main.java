package application;
	
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;


public class Main extends Application {
	@Override
    public void start(Stage stage) {
        Clock clock = new Clock(400, 400);
        
        VBox root = new VBox();

        root.getChildren().addAll(clock.getCanvas());
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Analog Clock");
        stage.setScene(scene);
        stage.setResizable(false);

        
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> clock.update()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        stage.show();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
