package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Piano extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        Pane pane = new Pane();


        double whiteKeyWidth = 60;
        double whiteKeyHeight = 200;
        double blackKeyWidth = 40;
        double blackKeyHeight = 120;

        
        for (int i = 0; i < 7; i++) { 
            Rectangle whiteKey = new Rectangle(i * whiteKeyWidth, 0, whiteKeyWidth, whiteKeyHeight);
            whiteKey.setFill(Color.WHITE);
            whiteKey.setStroke(Color.BLACK);

            
            whiteKey.setOnMouseClicked(event -> handleKeyPress(event, "White"));

            pane.getChildren().add(whiteKey);
        }

        
        int[] blackKeyPositions = {0, 1, 3, 4, 5}; 
        for (int i : blackKeyPositions) {
            Rectangle blackKey = new Rectangle((i + 1) * whiteKeyWidth - blackKeyWidth / 2, 0, blackKeyWidth, blackKeyHeight);
            blackKey.setFill(Color.BLACK);

            
            blackKey.setOnMouseClicked(event -> handleKeyPress(event, "Black"));

            pane.getChildren().add(blackKey);
        }

        
        Scene scene = new Scene(pane, whiteKeyWidth * 7, whiteKeyHeight);


        primaryStage.setTitle("Piano with Events");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    
    private void handleKeyPress(MouseEvent event, String keyType) {
        Rectangle key = (Rectangle) event.getSource();


        if (keyType.equals("White")) {
            key.setFill(Color.LIGHTGRAY);
        } else {
            key.setFill(Color.DARKGRAY);
        }

        
        new Thread(() -> {
            try {
                Thread.sleep(150); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            javafx.application.Platform.runLater(() -> {
                key.setFill(keyType.equals("White") ? Color.WHITE : Color.BLACK);
            });
        }).start();

        
        System.out.println(keyType + " key pressed");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
