package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.ui.MainView;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("OOP Code Analyzer and Visualizer");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
