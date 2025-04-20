package noteapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class NoteAppFrame extends JFrame {
    private JTextArea noteTextArea;
    private JComboBox<String> categoryComboBox;
    private ArrayList<Note> notes;

    public NoteAppFrame() {
        setTitle("JavaNote - Note Taking Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        notes = new ArrayList<>();

        noteTextArea = new JTextArea();
        categoryComboBox = new JComboBox<>();

        JButton createButton = new JButton("Create");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(createButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(saveButton);

        createButton.addActionListener(e -> createNote());
        editButton.addActionListener(e -> editNote());
        deleteButton.addActionListener(e -> deleteNote());
        saveButton.addActionListener(e -> saveNotes());

        categoryComboBox.addActionListener(e -> displaySelectedNote());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(categoryComboBox, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(noteTextArea), BorderLayout.CENTER);

        loadNotes();
    }

    @SuppressWarnings("unchecked")
    private void loadNotes() {
        try {
            FileInputStream fileIn = new FileInputStream("notes.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            notes = (ArrayList<Note>) in.readObject();
            in.close();
            fileIn.close();
            populateCategoryComboBox();
        } catch (IOException | ClassNotFoundException ex) {
            notes = new ArrayList<>(); // Initialize with an empty list if loading fails
        }
    }

    private void saveNotes() {
        try {
            FileOutputStream fileOut = new FileOutputStream("notes.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(notes);
            out.close();
            fileOut.close();
            JOptionPane.showMessageDialog(this, "Notes saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving notes.");
        }
    }

    private void createNote() {
        String title = JOptionPane.showInputDialog(this, "Enter note title:");
        if (title != null && !title.trim().isEmpty()) {
            String content = noteTextArea.getText();
            String category = "Default"; // Optional: Add a category input if desired
            Note newNote = new Note(title, content, category);
            notes.add(newNote);
            populateCategoryComboBox();
            saveNotes();
            noteTextArea.setText("");
            JOptionPane.showMessageDialog(this, "Note created successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Note title cannot be empty!");
        }
    }

    private void editNote() {
        int selectedIndex = categoryComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < notes.size()) {
            Note selectedNote = notes.get(selectedIndex);
            selectedNote.setContent(noteTextArea.getText());
            saveNotes();
            JOptionPane.showMessageDialog(this, "Note edited successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "No note selected.");
        }
    }

    private void deleteNote() {
        int selectedIndex = categoryComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < notes.size()) {
            notes.remove(selectedIndex);
            populateCategoryComboBox();
            saveNotes();
            noteTextArea.setText("");
            JOptionPane.showMessageDialog(this, "Note deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "No note selected.");
        }
    }

    private void displaySelectedNote() {
        int selectedIndex = categoryComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < notes.size()) {
            Note selectedNote = notes.get(selectedIndex);
            noteTextArea.setText(selectedNote.getContent());
        } else {
            noteTextArea.setText("");
        }
    }

    private void populateCategoryComboBox() {
        categoryComboBox.removeAllItems();
        for (Note note : notes) {
            categoryComboBox.addItem(note.getTitle());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new NoteAppFrame();
            frame.setVisible(true);
        });
    }
}
