package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QuizGame extends JFrame {
    private static final long serialVersionUID = 1L;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HashMap<String, ArrayList<Question>> quizzes;

    public QuizGame() {
        setTitle("Quiz Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        quizzes = new HashMap<>();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Initialize panels
        mainPanel.add(createMainMenuPanel(), "MainMenu");
        mainPanel.add(createQuizPanel(), "CreateQuiz");
        mainPanel.add(createTakeQuizPanel(), "TakeQuiz");
        mainPanel.add(createViewQuizPanel(), "ViewQuiz");
        mainPanel.add(createListQuizPanel(), "ListQuiz");

        cardLayout.show(mainPanel, "MainMenu");
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 255)); // Light purple-blue background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Quiz Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JButton createButton = new JButton("Create Quiz");
        createButton.setFont(new Font("Arial", Font.PLAIN, 16));
        createButton.addActionListener(e -> cardLayout.show(mainPanel, "CreateQuiz"));
        gbc.gridy = 1;
        panel.add(createButton, gbc);

        JButton takeButton = new JButton("Take Quiz");
        takeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        takeButton.addActionListener(e -> cardLayout.show(mainPanel, "TakeQuiz"));
        gbc.gridy = 2;
        panel.add(takeButton, gbc);

        JButton viewButton = new JButton("View Quiz");
        viewButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewButton.addActionListener(e -> cardLayout.show(mainPanel, "ViewQuiz"));
        gbc.gridy = 3;
        panel.add(viewButton, gbc);

        JButton listButton = new JButton("List Quizzes");
        listButton.setFont(new Font("Arial", Font.PLAIN, 16));
        listButton.addActionListener(e -> cardLayout.show(mainPanel, "ListQuiz"));
        gbc.gridy = 4;
        panel.add(listButton, gbc);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 5;
        panel.add(exitButton, gbc);

        return panel;
    }

    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create a Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Quiz Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel numQuestionsLabel = new JLabel("Number of Questions:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(numQuestionsLabel, gbc);

        JTextField numQuestionsField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(numQuestionsField, gbc);

        JTextArea questionArea = new JTextArea(5, 20);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(new JScrollPane(questionArea), gbc);

        JTextField[] choiceFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            choiceFields[i] = new JTextField(20);
            JLabel choiceLabel = new JLabel("Choice " + (i + 1) + ":");
            gbc.gridx = 0;
            gbc.gridy = 4 + i;
            gbc.gridwidth = 1;
            panel.add(choiceLabel, gbc);
            gbc.gridx = 1;
            panel.add(choiceFields[i], gbc);
        }

        JLabel correctLabel = new JLabel("Correct Choice (1-4):");
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(correctLabel, gbc);

        JTextField correctField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(correctField, gbc);

        JLabel feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        feedbackLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(feedbackLabel, gbc);

        JButton submitButton = new JButton("Add Question");
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        panel.add(submitButton, gbc);

        JButton finishButton = new JButton("Finish Quiz");
        gbc.gridx = 1;
        gbc.gridy = 10;
        panel.add(finishButton, gbc);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        panel.add(backButton, gbc);

        ArrayList<Question> questions = new ArrayList<>();
        submitButton.addActionListener(e -> {
            try {
                String questionText = questionArea.getText().trim();
                String[] choices = new String[4];
                for (int i = 0; i < 4; i++) {
                    choices[i] = choiceFields[i].getText().trim();
                }
                int correctChoice = Integer.parseInt(correctField.getText().trim());
                if (questionText.isEmpty() || choices[0].isEmpty() || choices[1].isEmpty() ||
                    choices[2].isEmpty() || choices[3].isEmpty() || correctChoice < 1 || correctChoice > 4) {
                    feedbackLabel.setText("Please fill all fields and enter a valid correct choice (1-4).");
                    feedbackLabel.setForeground(Color.RED);
                    return;
                }
                questions.add(new Question(questionText, choices, correctChoice));
                feedbackLabel.setText("Question added!");
                feedbackLabel.setForeground(Color.GREEN);
                questionArea.setText("");
                for (JTextField field : choiceFields) {
                    field.setText("");
                }
                correctField.setText("");
            } catch (NumberFormatException ex) {
                feedbackLabel.setText("Correct choice must be a number (1-4).");
                feedbackLabel.setForeground(Color.RED);
            }
        });

        finishButton.addActionListener(e -> {
            String quizName = nameField.getText().trim();
            if (quizName.isEmpty() || questions.isEmpty()) {
                feedbackLabel.setText("Please enter a quiz name and add at least one question.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            if (quizzes.containsKey(quizName)) {
                feedbackLabel.setText("Quiz name already exists!");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            quizzes.put(quizName, new ArrayList<>(questions));
            questions.clear();
            nameField.setText("");
            numQuestionsField.setText("");
            feedbackLabel.setText("Quiz '" + quizName + "' created successfully!");
            feedbackLabel.setForeground(Color.GREEN);
        });

        return panel;
    }

    private JPanel createTakeQuizPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Take a Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Quiz Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel questionLabel = new JLabel("Select an answer:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(questionLabel, gbc);

        ButtonGroup answerGroup = new ButtonGroup();
        JRadioButton[] answerButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JRadioButton();
            answerButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            answerGroup.add(answerButtons[i]);
            gbc.gridx = 0;
            gbc.gridy = 3 + i;
            gbc.gridwidth = 2;
            panel.add(answerButtons[i], gbc);
        }

        JLabel feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        feedbackLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(feedbackLabel, gbc);

        JButton submitButton = new JButton("Submit Answer");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        panel.add(submitButton, gbc);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(backButton, gbc);

        final int[] currentQuestionIndex = {0};
        final int[] score = {0};
        final ArrayList<Question> currentQuiz = new ArrayList<>();

        submitButton.addActionListener(e -> {
            String quizName = nameField.getText().trim();
            if (currentQuiz.isEmpty()) {
                if (!quizzes.containsKey(quizName)) {
                    feedbackLabel.setText("Quiz not found!");
                    feedbackLabel.setForeground(Color.RED);
                    return;
                }
                currentQuiz.addAll(quizzes.get(quizName));
                currentQuestionIndex[0] = 0;
                score[0] = 0;
                nameField.setEditable(false);
            }

            if (currentQuestionIndex[0] < currentQuiz.size()) {
                Question question = currentQuiz.get(currentQuestionIndex[0]);
                questionLabel.setText("Q" + (currentQuestionIndex[0] + 1) + ": " + question.getQuestionText());
                for (int i = 0; i < 4; i++) {
                    answerButtons[i].setText(question.getChoices()[i]);
                    answerButtons[i].setSelected(false);
                }

                if (e.getSource() == submitButton && currentQuestionIndex[0] > 0) {
                    // Check previous answer
                    int selected = -1;
                    for (int i = 0; i < 4; i++) {
                        if (answerButtons[i].isSelected()) {
                            selected = i + 1;
                            break;
                        }
                    }
                    if (selected == -1) {
                        feedbackLabel.setText("Please select an answer!");
                        feedbackLabel.setForeground(Color.RED);
                        return;
                    }
                    if (selected == currentQuiz.get(currentQuestionIndex[0] - 1).getCorrectChoice()) {
                        score[0]++;
                    }
                    currentQuestionIndex[0]++;
                }

                if (currentQuestionIndex[0] < currentQuiz.size()) {
                    Question nextQuestion = currentQuiz.get(currentQuestionIndex[0]);
                    questionLabel.setText("Q" + (currentQuestionIndex[0] + 1) + ": " + nextQuestion.getQuestionText());
                    for (int i = 0; i < 4; i++) {
                        answerButtons[i].setText(nextQuestion.getChoices()[i]);
                        answerButtons[i].setSelected(false);
                    }
                    feedbackLabel.setText("");
                } else {
                    feedbackLabel.setText("Quiz finished! Score: " + score[0] + "/" + currentQuiz.size());
                    feedbackLabel.setForeground(Color.GREEN);
                    submitButton.setEnabled(false);
                    currentQuiz.clear();
                    nameField.setEditable(true);
                }
            }
        });

        return panel;
    }

    private JPanel createViewQuizPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("View a Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Quiz Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(new JScrollPane(outputArea), gbc);

        JButton viewButton = new JButton("View Quiz");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(viewButton, gbc);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(backButton, gbc);

        viewButton.addActionListener(e -> {
            String quizName = nameField.getText().trim();
            if (!quizzes.containsKey(quizName)) {
                outputArea.setText("Quiz '" + quizName + "' not found!");
                return;
            }
            ArrayList<Question> questions = quizzes.get(quizName);
            StringBuilder output = new StringBuilder("Quiz: " + quizName + "\n\n");
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                output.append("Q").append(i + 1).append(": ").append(q.getQuestionText()).append("\n");
                for (int j = 0; j < 4; j++) {
                    output.append("  ").append(j + 1).append(". ").append(q.getChoices()[j]);
                    if (j + 1 == q.getCorrectChoice()) {
                        output.append(" (Correct)");
                    }
                    output.append("\n");
                }
                output.append("\n");
            }
            outputArea.setText(output.toString());
        });

        return panel;
    }

    private JPanel createListQuizPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("List Quizzes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JScrollPane(outputArea), gbc);

        JButton listButton = new JButton("List Quizzes");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(listButton, gbc);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(backButton, gbc);

        listButton.addActionListener(e -> {
            if (quizzes.isEmpty()) {
                outputArea.setText("No quizzes available!");
            } else {
                StringBuilder output = new StringBuilder("Available Quizzes:\n\n");
                for (String quizName : quizzes.keySet()) {
                    output.append("- ").append(quizName).append("\n");
                }
                outputArea.setText(output.toString());
            }
        });

        return panel;
    }

    private class Question {
        private String questionText;
        private String[] choices;
        private int correctChoice;

        public Question(String questionText, String[] choices, int correctChoice) {
            this.questionText = questionText;
            this.choices = choices.clone();
            this.correctChoice = correctChoice;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String[] getChoices() {
            return choices.clone();
        }

        public int getCorrectChoice() {
            return correctChoice;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizGame game = new QuizGame();
            game.setVisible(true);
        });
    }
}

