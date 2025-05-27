package game;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AsteroidGame extends JFrame {
    private GamePanel gamePanel;

    public AsteroidGame() {
        setTitle("Asteroids!");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel = new GamePanel();
        add(gamePanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AsteroidGame();
    }
}