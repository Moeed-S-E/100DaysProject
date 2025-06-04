package app;

import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.media.Media;
import java.io.File;
import java.util.List;

public class PlaylistManager {
    private List<File> playlist;
    private int currentIndex = -1;
    private final ListView<String> playlistView = new ListView<>();

    public ListView<String> getPlaylistView() { return playlistView; }
    public boolean hasPlaylist() { return playlist != null && !playlist.isEmpty(); }
    public void setCurrentIndex(int idx) { currentIndex = idx; }
    public int getNextIndex() {
        if (!hasPlaylist()) return -1;
        return (currentIndex + 1) % playlist.size();
    }
    public int getPreviousIndex() {
        if (!hasPlaylist()) return -1;
        return (currentIndex - 1 + playlist.size()) % playlist.size();
    }
    public Media getMedia(int index) {
        if (!hasPlaylist() || index < 0 || index >= playlist.size()) return null;
        return new Media(playlist.get(index).toURI().toString());
    }
    public String getFileName(int index) {
        if (!hasPlaylist() || index < 0 || index >= playlist.size()) return "";
        return playlist.get(index).getName();
    }
    public void loadMediaFiles(Window window) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Media Folder");
        File dir = chooser.showDialog(window);
        if (dir != null) {
            File[] files = dir.listFiles(f -> {
                String name = f.getName().toLowerCase();
                return name.endsWith(".mp3") || name.endsWith(".mp4") ||
                       name.endsWith(".wav") || name.endsWith(".aiff");
            });
            if (files != null) {
                playlist = List.of(files);
                playlistView.getItems().clear();
                for (File file : files) playlistView.getItems().add(file.getName());
                currentIndex = 0;
            }
        }
    }
}
