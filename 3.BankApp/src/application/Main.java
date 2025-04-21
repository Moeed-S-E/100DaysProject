package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML
        Parent root = FXMLLoader.load(getClass().getResource("Welcome.fxml"));

        // Create the scene
        Scene scene = new Scene(root, 682, 464);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Set the stage title and icon (logo in the top bar)
        primaryStage.setTitle("HBL ATM Clone");
        Image logo = new Image(getClass().getResourceAsStream("img/logo.jpg"));
        primaryStage.getIcons().add(logo);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}