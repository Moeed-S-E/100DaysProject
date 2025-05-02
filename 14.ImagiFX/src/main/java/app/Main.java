package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private Scene welcomeScene;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Welcome
        WelcomeScreen welcomeScreen = new WelcomeScreen(this::showMainUI);
        welcomeScreen.getRoot().setStyle("-fx-background-color: #333333;");
        welcomeScene = new Scene(welcomeScreen.getRoot(), 800, 600);

        ImageFilterUI ui = new ImageFilterUI();
        ui.getRoot().setStyle("-fx-background-color: #333333;"); 
        mainScene = new Scene(ui.getRoot(), 800, 600);

        primaryStage.setTitle("Welcome Image Filter App");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    private void showMainUI() {
        primaryStage.setTitle("Image Filter App");
        primaryStage.setScene(mainScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}