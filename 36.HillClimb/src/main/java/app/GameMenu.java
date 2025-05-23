package app;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class GameMenu implements EventHandler<ActionEvent> {
    public static MenuItem pauseButton, saveButton, loadButton, controls, newGame, exit;

    public static MenuBar init() {
        MenuBar mb = new MenuBar();
        Menu menu = new Menu("Menu");

        pauseButton = new MenuItem("Pause");
        saveButton = new MenuItem("Save");
        loadButton = new MenuItem("Load");
        controls = new MenuItem("Controls");
        newGame = new MenuItem("New Game");
        exit = new MenuItem("Exit");

        GameMenu handler = new GameMenu();
        pauseButton.setOnAction(handler);
        saveButton.setOnAction(handler);
        loadButton.setOnAction(handler);
        controls.setOnAction(handler);
        newGame.setOnAction(handler);
        exit.setOnAction(handler);

        menu.getItems().addAll(pauseButton, saveButton, loadButton, controls, newGame, exit);
        mb.getMenus().add(menu);

        return mb;
    }

    @Override
    public void handle(ActionEvent e) {
        MenuItem source = (MenuItem) e.getSource();
        String s = source.getText();

        if (s.equals("Pause") || s.equals("Unpause") || s.equals("Controls")) {
            Board.paused = !Board.paused;
            pauseButton.setText(Board.paused ? "Unpause" : "Pause");
        } else if (s.equals("Save")) {
            System.out.println("Save functionality not implemented in JavaFX port.");
        } else if (s.equals("Load")) {
            System.out.println("Load functionality not implemented in JavaFX port.");
        } else if (s.equals("New Game")) {
            Board.board.newGame();
        } else if (s.equals("Exit")) {
            MainGame.close();
        }
    }
}