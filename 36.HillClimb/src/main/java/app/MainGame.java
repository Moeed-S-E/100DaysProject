package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainGame extends Application {
    @Override
    public void start(Stage primaryStage) {
        Board board = new Board();
        BorderPane root = new BorderPane();
        root.setStyle("fx-background-color: #000;");
        root.setTop(GameMenu.init());
        root.setCenter(board);
        Scene scene = new Scene(root, Game.WIDTH, Game.HEIGHT);
        scene.setOnKeyPressed(board::handleKeyPressed);
        scene.setOnKeyReleased(board::handleKeyReleased);
        primaryStage.setTitle("JavaFX Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        board.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void close() {
        System.exit(0);
    }
}