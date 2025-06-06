package app;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("The Pipe Puzzle!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            PipePuzzlePanel gamePanel = new PipePuzzlePanel();
            frame.add(gamePanel);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
