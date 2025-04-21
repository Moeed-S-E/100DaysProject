package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            // Ensure the file is in the correct location, here we assume it is in the same package as this Main.java class
            Parent root = FXMLLoader.load(getClass().getResource("Welcome.fxml"));
            
            if (root == null) {
                // If the resource could not be found, show a message
                System.out.println("FXML file not found. Please check the file location.");
                return;
            }

            // Set up the stage with the loaded FXML content
            primaryStage.setTitle("ATM CLONE");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            // Exception handling for file loading failure
            e.printStackTrace();
            // Optionally, you can show an error dialog to the user
            System.out.println("Error loading FXML file.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
