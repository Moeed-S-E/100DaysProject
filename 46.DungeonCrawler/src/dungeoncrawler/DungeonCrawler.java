package dungeoncrawler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class DungeonCrawler extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        showLoadingScreen();
    }

    private void showLoadingScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadingScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dungeon Crawler");
        primaryStage.show();

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> {
            try {
                showGameScene();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    public void showGameScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        controller.setMainApp(this);
        Scene scene = new Scene(root, 600, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showWinScene(int score) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WinScreen.fxml"));
        Parent root = loader.load();
        WinController controller = loader.getController();
        controller.setScore(score);
        controller.setPlayAgainCallback(() -> {
            try {
                showGameScene();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Scene scene = new Scene(root, 600, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
