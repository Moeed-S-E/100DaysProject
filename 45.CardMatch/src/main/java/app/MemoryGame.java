package app;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.util.*;

public class MemoryGame extends Application {
    private final String[] cardTypes = {
            "darkness", "double", "fairy", "fighting", "fire",
            "grass", "lightning", "metal", "psychic", "water"
    };
    private final int rows = 4;
    private final int columns = 5;
    private String[][] board = new String[rows][columns];
    private ImageView[][] imageViews = new ImageView[rows][columns];
    private ImageView card1Selected = null;
    private ImageView card2Selected = null;
    private int errors = 0;
    private Label errorLabel;
    private boolean acceptingInput = true;
    private GridPane boardGrid; // Make boardGrid an instance variable

    @Override
    public void start(Stage primaryStage) {
        // Background
        BackgroundImage bgImage = new BackgroundImage(
                new Image(getClass().getResource("/pokemon-bg.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        BorderPane root = new BorderPane();
        root.setBackground(new Background(bgImage));

        // Error label
        errorLabel = new Label("Errors: 0");
        errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // ReRun button
        Button rerunButton = new Button("ReRun");
        rerunButton.setStyle("-fx-font-size: 16px; -fx-padding: 5 20 5 20;");
        rerunButton.setOnAction(e -> startGame()); // Call startGame on click

        HBox topBox = new HBox(15, errorLabel, rerunButton);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 15px;");
        root.setTop(topBox);

        // Board grid
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setStyle("-fx-background-color: whitesmoke; -fx-border-color: lightgrey; -fx-border-width: 10px;");
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        root.setCenter(boardGrid);

        // Initialize game
        startGame();

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Memory Card Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame() {
        // Prepare and shuffle cards
        List<String> cards = new ArrayList<>();
        for (String type : cardTypes) {
            cards.add(type);
            cards.add(type);
        }
        Collections.shuffle(cards);

        // Assign cards to board
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                board[r][c] = cards.remove(cards.size() - 1);
            }
        }

        // Reset error count and selections
        errors = 0;
        errorLabel.setText("Errors: 0");
        card1Selected = null;
        card2Selected = null;
        acceptingInput = true;

        // Add card backs to grid
        boardGrid.getChildren().clear();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                ImageView cardView = new ImageView(new Image(getClass().getResource("/back.jpg").toExternalForm()));
                cardView.setFitWidth(90);
                cardView.setFitHeight(128);
                cardView.setStyle("-fx-effect: dropshadow(gaussian, lightgrey, 5, 0, 0, 0);");
                final int row = r, col = c;
                cardView.setOnMouseClicked(e -> handleCardClick(cardView, row, col));
                imageViews[r][c] = cardView;
                boardGrid.add(cardView, c, r);
            }
        }

        // Briefly show all cards, then hide
        revealAllCards();
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> hideAllCards());
        pause.play();
    }

    private void revealAllCards() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                imageViews[r][c].setImage(new Image(getClass().getResource("/" + board[r][c] + ".jpg").toExternalForm()));
    }

    private void hideAllCards() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                imageViews[r][c].setImage(new Image(getClass().getResource("/back.jpg").toExternalForm()));
    }

    private void handleCardClick(ImageView cardView, int r, int c) {
        if (!acceptingInput) return;
        if (cardView == card1Selected || cardView == card2Selected) return;
        if (!cardView.getImage().getUrl().endsWith("back.jpg")) return;

        cardView.setImage(new Image(getClass().getResource("/" + board[r][c] + ".jpg").toExternalForm()));

        if (card1Selected == null) {
            card1Selected = cardView;
        } else if (card2Selected == null) {
            card2Selected = cardView;
            acceptingInput = false;
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> checkMatch());
            pause.play();
        }
    }

    private void checkMatch() {
        int r1 = -1, c1 = -1, r2 = -1, c2 = -1;
        // Find coordinates of selected cards
        outer: for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++) {
                if (imageViews[r][c] == card1Selected) { r1 = r; c1 = c; }
                if (imageViews[r][c] == card2Selected) { r2 = r; c2 = c; }
                if (r1 != -1 && r2 != -1) break outer;
            }

        if (board[r1][c1].equals(board[r2][c2])) {
            // Match: leave cards revealed
        } else {
            // Not a match: flip back
            card1Selected.setImage(new Image(getClass().getResource("/back.jpg").toExternalForm()));
            card2Selected.setImage(new Image(getClass().getResource("/back.jpg").toExternalForm()));
            errors++;
            errorLabel.setText("Errors: " + errors);
        }
        card1Selected = null;
        card2Selected = null;
        acceptingInput = true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
