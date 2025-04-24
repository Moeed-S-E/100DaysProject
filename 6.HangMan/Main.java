import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class HangmanGame extends JFrame implements ActionListener {
    private String[] words = {"hangman", "java", "swing", "programming", "moeed"};
    private String wordToGuess;
    private int guessesLeft = 6;
    private StringBuilder hiddenWord;
    private Set<Character> guessedLetters = new HashSet<>();
    private Set<Character> incorrectGuesses = new HashSet<>();

    private JLabel hiddenWordLabel;
    private JLabel guessesLeftLabel;
    private JLabel incorrectGuessesLabel;
    private JTextField guessTextField;
    private JButton guessButton;
    private HangmanPanel hangmanPanel;

    public HangmanGame() {
        setTitle("Hangman Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        hiddenWordLabel = new JLabel();
        guessesLeftLabel = new JLabel("Guesses Left: " + guessesLeft);
        incorrectGuessesLabel = new JLabel("Incorrect Guesses: ");
        guessTextField = new JTextField(10);
        guessButton = new JButton("Guess");
        hangmanPanel = new HangmanPanel();

        guessButton.addActionListener(this);
        guessTextField.addActionListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));
        JPanel wordPanel = new JPanel();
        wordPanel.add(hiddenWordLabel);
        mainPanel.add(wordPanel);
        mainPanel.add(guessesLeftLabel);
        mainPanel.add(incorrectGuessesLabel);
        
        JPanel inputPanel = new JPanel();
        inputPanel.add(guessTextField);
        inputPanel.add(guessButton);
        mainPanel.add(inputPanel);

        getContentPane().add(mainPanel, BorderLayout.NORTH);
        getContentPane().add(hangmanPanel, BorderLayout.CENTER);

        initializeGame();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeGame() {
        wordToGuess = words[(int) (Math.random() * words.length)];
        hiddenWord = new StringBuilder();
        for (int i = 0; i < wordToGuess.length(); i++) {
            hiddenWord.append("_");
        }
        hiddenWordLabel.setText(hiddenWord.toString());
        guessesLeft = 6;
        guessedLetters.clear();
        incorrectGuesses.clear();
        guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
        incorrectGuessesLabel.setText("Incorrect Guesses: ");
        hangmanPanel.repaint(); // Reset hangman image
    }

    private void updateHiddenWord(char guess) {
        if (guessedLetters.contains(guess)) {
            JOptionPane.showMessageDialog(this, "You already guessed that letter!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        guessedLetters.add(guess);
        boolean found = false;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                hiddenWord.setCharAt(i, guess);
                found = true;
            }
        }

        if (!found) {
            incorrectGuesses.add(guess);
            guessesLeft--;
            guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
            incorrectGuessesLabel.setText("Incorrect Guesses: " + incorrectGuesses);
            hangmanPanel.repaint(); // Update hangman drawing
            if (guessesLeft == 0) {
                endGame("You lose! The word was: " + wordToGuess);
            }
        } else if (hiddenWord.toString().equals(wordToGuess)) {
            endGame("Congratulations! You won!");
        } else {
            hiddenWordLabel.setText(hiddenWord.toString());
        }
    }

    private void endGame(String message) {
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        int option = JOptionPane.showConfirmDialog(this, message + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            initializeGame();
            guessTextField.setEnabled(true);
            guessButton.setEnabled(true);
            guessTextField.requestFocus();
        } else {
            System.exit(0);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == guessButton || e.getSource() == guessTextField) {
            String guessText = guessTextField.getText().trim();
            if (guessText.length() == 1 && Character.isLetter(guessText.charAt(0))) {
                char guess = guessText.toLowerCase().charAt(0);
                updateHiddenWord(guess);
                guessTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid single letter!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new);
    }

    // Custom JPanel for drawing the Hangman
    class HangmanPanel extends JPanel {
        private static final int PANEL_WIDTH = 200;
        private static final int PANEL_HEIGHT = 250;

        public HangmanPanel() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);

            // Draw the gallows
            g2d.drawLine(50, 200, 150, 200); // Base
            g2d.drawLine(75, 200, 75, 50);   // Pole
            g2d.drawLine(75, 50, 125, 50);   // Top bar
            g2d.drawLine(125, 50, 125, 75);  // Vertical bar

            // Draw the parts of the hangman based on guesses left
            if (guessesLeft <= 5) drawHead(g2d);
            if (guessesLeft <= 4) drawBody(g2d);
            if (guessesLeft <= 3) drawLeftArm(g2d);
            if (guessesLeft <= 2) drawRightArm(g2d);
            if (guessesLeft <= 1) drawLeftLeg(g2d);
            if (guessesLeft <= 0) drawRightLeg(g2d);
        }

        private void drawHead(Graphics2D g2d) {
            g2d.drawOval(105, 75, 20, 20);  // Head (circle)
        }

        private void drawBody(Graphics2D g2d) {
            g2d.drawLine(115, 95, 115, 140); // Body
        }

        private void drawLeftArm(Graphics2D g2d) {
            g2d.drawLine(115, 105, 90, 120); // Left arm
        }

        private void drawRightArm(Graphics2D g2d) {
            g2d.drawLine(115, 105, 140, 120); // Right arm
        }

        private void drawLeftLeg(Graphics2D g2d) {
            g2d.drawLine(115, 140, 95, 170); // Left leg
        }

        private void drawRightLeg(Graphics2D g2d) {
            g2d.drawLine(115, 140, 135, 170); // Right leg
        }
    }
}
