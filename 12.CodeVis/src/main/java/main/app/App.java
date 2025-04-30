package main.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Looking for FXML at: " + getClass().getResource("/main.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        if (loader.getLocation() == null) {
            throw new IllegalStateException("FXML file not found at /main.fxml");
        }
        Scene scene = new Scene(loader.load());
        stage.setTitle("Code Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}