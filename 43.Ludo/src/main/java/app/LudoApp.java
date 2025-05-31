package app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class LudoApp extends Application {
    private LudoPlayer[] players = new LudoPlayer[2];
    private int currentPlayer = 0; // 0 = Blue, 1 = Green
    private int diceValue = 0;
    private boolean diceRolled = false;
    private Label diceLabel;
    private Button diceButton;
    private Label turnLabel;
    private Pane pieceLayer;

    @Override
    public void start(Stage primaryStage) {
        // Board background
        Image boardImage = new Image(getClass().getResource("/ludo-bg.jpg").toExternalForm());
        ImageView boardView = new ImageView(boardImage);
        boardView.setFitWidth(LudoConstants.BOARD_SIZE);
        boardView.setFitHeight(LudoConstants.BOARD_SIZE);

        StackPane boardPane = new StackPane(boardView);
        boardPane.setPrefSize(LudoConstants.BOARD_SIZE, LudoConstants.BOARD_SIZE);

        pieceLayer = new Pane();
        pieceLayer.setPickOnBounds(false);
        boardPane.getChildren().add(pieceLayer);

        // Players
        players[0] = new LudoPlayer(0); // Blue
        players[1] = new LudoPlayer(1); // Green

        // Place all pieces in their base
        for (LudoPlayer player : players) {
            for (int i = 0; i < 4; i++) {
                LudoPiece piece = player.pieces[i];
                setPiecePosition(piece, piece.position);
                int pi = player.playerId, idx = i;
                piece.setOnMouseClicked(evt -> handlePieceClick(evt, pi, idx));
                pieceLayer.getChildren().add(piece);
            }
        }

        // Dice and turn controls
        diceLabel = new Label("🎲 -");
        diceLabel.getStyleClass().add("dice-value");

        diceButton = new Button("Roll Dice");
        diceButton.getStyleClass().add("btn-dice");
        diceButton.setOnAction(e -> rollDice());

        turnLabel = new Label("Turn: " + players[currentPlayer].name);
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox controls = new HBox(20, diceButton, diceLabel, turnLabel);
        controls.setAlignment(Pos.CENTER);
        controls.setPrefHeight(60);

        VBox root = new VBox(boardPane, controls);
        root.getStyleClass().add("ludo-container");

        Scene scene = new Scene(root, LudoConstants.BOARD_SIZE, LudoConstants.BOARD_SIZE + 60);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Ludo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void rollDice() {
        if (diceRolled) return;
        diceValue = new Random().nextInt(6) + 1;
        diceLabel.setText("🎲 " + diceValue); 
        diceRolled = true;
        diceButton.setDisable(true);

        // Highlight eligible pieces
        List<LudoPiece> eligible = getEligiblePieces(players[currentPlayer]);
        if (eligible.isEmpty()) {
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(this::nextTurn);
            }).start();
        } else {
            for (LudoPiece piece : eligible) {
                piece.getStyleClass().add("highlight");
            }
        }
    }


    private List<LudoPiece> getEligiblePieces(LudoPlayer player) {
        List<LudoPiece> eligible = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            LudoPiece piece = player.pieces[i];
            // If in base
            if (isInBase(piece) && diceValue == 6) {
                eligible.add(piece);
            } else if (isOnBoard(piece) && canMove(piece, diceValue)) {
                eligible.add(piece);
            }
        }
        return eligible;
    }

    private void handlePieceClick(MouseEvent evt, int playerId, int pieceIdx) {
        if (playerId != currentPlayer || !diceRolled) return;
        LudoPiece piece = players[playerId].pieces[pieceIdx];
        if (!piece.getStyleClass().contains("highlight")) return;

        // Remove highlights
        for (LudoPiece p : players[currentPlayer].pieces) {
            p.getStyleClass().remove("highlight");
        }

        // Move logic
        if (isInBase(piece)) {
            // Move out of base to start
            piece.position = players[currentPlayer].startPosition;
            setPiecePosition(piece, piece.position);
        } else {
            // Move along the path
            for (int i = 0; i < diceValue; i++) {
                piece.position = getNextPosition(piece);
            }
            setPiecePosition(piece, piece.position);
        }

     // Check for win
        if (hasPlayerWon(players[currentPlayer])) {
            showWinPopup(players[currentPlayer].name);
            diceButton.setDisable(true);
            return;
        }


        // Check for kill
        checkForKill(piece);

        // If dice was 6, allow another roll, else next turn
        if (diceValue == 6) {
            diceRolled = false;
            diceButton.setDisable(false);
        } else {
            nextTurn();
        }
    }

    private void nextTurn() {
        diceRolled = false;
        diceButton.setDisable(false);
        diceLabel.setText("🎲 -");
        currentPlayer = 1 - currentPlayer;
        turnLabel.setText("Turn: " + players[currentPlayer].name);
    }

    private void setPiecePosition(LudoPiece piece, int position) {
    	double[] coord = LudoConstants.COORDINATES_MAP.get(position);
    	piece.setLayoutX(coord[0] * LudoConstants.SQUARE_SIZE + 15);
    	piece.setLayoutY(coord[1] * LudoConstants.SQUARE_SIZE + 15);
    }

    private boolean isInBase(LudoPiece piece) {
        int[] base = players[piece.player].basePositions;
        for (int b : base) if (piece.position == b) return true;
        return false;
    }

    private boolean isOnBoard(LudoPiece piece) {
        return !isInBase(piece) && piece.position != players[piece.player].homePosition;
    }

    private boolean canMove(LudoPiece piece, int value) {
        int pos = piece.position;
        int home = players[piece.player].homePosition;
        int turning = players[piece.player].turningPoint;
        int[] entrance = players[piece.player].homeEntrance;

        // If in home entrance
        for (int i = 0; i < entrance.length - 1; i++) {
            if (pos == entrance[i] && (entrance[i] + value) <= home) return true;
        }
        // If on main path
        if (pos == turning && value == 1) return true;
        if (pos < 52 && pos + value < 52) return true;
        if (pos < 52 && pos + value > 51) {
            int stepsToTurning = turning - pos;
            if (value > stepsToTurning) {
                int inHome = value - stepsToTurning - 1;
                return inHome < entrance.length;
            }
        }
        if (pos < 52 && pos + value == 52) return true;
        return false;
    }

    private int getNextPosition(LudoPiece piece) {
        int pos = piece.position;
        int turning = players[piece.player].turningPoint;
        int[] entrance = players[piece.player].homeEntrance;
        int home = players[piece.player].homePosition;

        // Move into home entrance
        if (pos == turning) return entrance[0];
        // Move along home entrance
        for (int i = 0; i < entrance.length - 1; i++) {
            if (pos == entrance[i]) return entrance[i + 1];
        }
        // Move to home
        if (pos == entrance[entrance.length - 1]) return home;
        // Loop the main path
        if (pos < 51) return pos + 1;
        if (pos == 51) return 0;
        return pos;
    }

    private void checkForKill(LudoPiece moved) {
        for (LudoPlayer player : players) {
            if (player.playerId == moved.player) continue;
            for (LudoPiece piece : player.pieces) {
                if (piece.position == moved.position && !isSafePosition(piece.position)) {
                    // Send back to base
                    piece.position = player.basePositions[piece.index];
                    setPiecePosition(piece, piece.position);
                }
            }
        }
    }

    private boolean isSafePosition(int pos) {
        for (int safe : LudoConstants.SAFE_POSITIONS) if (pos == safe) return true;
        return false;
    }
    private void showWinPopup(String winnerName) {
        // Load the GIF image from resources
        Image winImage = new Image(getClass().getResource("/win.gif").toExternalForm());
        ImageView imageView = new ImageView(winImage);

        // Optionally set size
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(250); // adjust as needed

        // Label for winner
        Label winLabel = new Label(winnerName + " wins!");
        winLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #009d60;");

        VBox vbox = new VBox(20, winLabel, imageView);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white; -fx-padding: 40px;");

        Scene scene = new Scene(vbox);
        Stage popup = new Stage();
        popup.setTitle("Game Over");
        popup.setScene(scene);
        popup.setResizable(false);
        popup.initOwner(diceButton.getScene().getWindow());
        popup.show();
    }

    private boolean hasPlayerWon(LudoPlayer player) {
        for (LudoPiece piece : player.pieces) {
            if (piece.position != player.homePosition) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
