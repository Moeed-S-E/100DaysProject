package app;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.input.KeyCode;

public class Game {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static Set<KeyCode> keys = new HashSet<>();
    public static int highScore = 0;
}