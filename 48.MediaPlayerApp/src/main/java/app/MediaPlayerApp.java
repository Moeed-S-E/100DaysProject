package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MediaPlayerApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        MediaPlayerController controller = new MediaPlayerController();
        Scene scene = new Scene(controller.getRoot(), 1100, 600);
        scene.getStylesheets().add(getClass().getResource("/media-player.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Media Player");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
