package MusicPlayer.main;

import javazoom.jl.player.advanced.AdvancedPlayer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class MusicPlayer extends JFrame {
    private static final long serialVersionUID = 1L;
    private ArrayList<File> playlist = new ArrayList<>();
    private int currentSongIndex = -1;
    private AdvancedPlayer player;
    private Thread playbackThread;
    private volatile boolean isPlaying = false;

    // GUI Components
    private JButton loadButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JButton previousButton;
    private JList<String> playlistView;

    public MusicPlayer() {
        setTitle("Music Player");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Font customFont = new Font("Segoe UI", Font.PLAIN, 14);

        playlistView = new JList<>();
        playlistView.setFont(customFont);
        JScrollPane playlistScrollPane = new JScrollPane(playlistView);
        playlistScrollPane.setBorder(BorderFactory.createTitledBorder("Playlist"));

        loadButton = createStyledButton("Load Songs");
        playButton = createStyledButton("Play");
        pauseButton = createStyledButton("Pause");
        nextButton = createStyledButton("Next");
        previousButton = createStyledButton("Previous");


        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(loadButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(nextButton);


        add(playlistScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadSongs());
        playButton.addActionListener(e -> playCurrentSong());
        pauseButton.addActionListener(e -> pauseSong());
        nextButton.addActionListener(e -> playNextSong());
        previousButton.addActionListener(e -> playPreviousSong());

        // Handle double-click or Enter to play song
        playlistView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    currentSongIndex = playlistView.getSelectedIndex();
                    playCurrentSong();
                }
            }
        });
        playlistView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    currentSongIndex = playlistView.getSelectedIndex();
                    playCurrentSong();
                }
            }
        });

    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(28, 28, 30));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));
        return button;
    }

    private void loadSongs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select a Folder with MP3 Files");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

            if (files != null) {
                playlist.clear();
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (File file : files) {
                    playlist.add(file);
                    listModel.addElement(file.getName());
                }
                playlistView.setModel(listModel);
                currentSongIndex = 0;
                JOptionPane.showMessageDialog(this, "Songs loaded successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No MP3 files found in the selected folder.");
            }
        }
    }

    private void playCurrentSong() {
        if (currentSongIndex < 0 || currentSongIndex >= playlist.size()) {
            JOptionPane.showMessageDialog(this, "No song selected or playlist is empty.");
            return;
        }

        File song = playlist.get(currentSongIndex);
        try {
            if (player != null) {
                player.close();
            }

            FileInputStream fis = new FileInputStream(song);
            player = new AdvancedPlayer(fis);

            playbackThread = new Thread(() -> {
                try {
                    isPlaying = true;
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            playbackThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseSong() {
        if (player != null && isPlaying) {
            isPlaying = false;
        }
    }

    private void playNextSong() {
        if (!playlist.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % playlist.size();
            playCurrentSong();
        }
    }

    private void playPreviousSong() {
        if (!playlist.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
            playCurrentSong();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicPlayer musicPlayer = new MusicPlayer();
            musicPlayer.setVisible(true);
        });
    }
}
