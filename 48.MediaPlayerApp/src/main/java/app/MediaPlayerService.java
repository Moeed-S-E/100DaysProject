package app;

import javafx.animation.RotateTransition;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.*;
import javafx.util.Duration;

public class MediaPlayerService {
    private MediaPlayer mediaPlayer;
    private final MediaView mediaView = new MediaView();
    private boolean pauseSeekUpdate = false;
    private Slider seekSlider;
    private Label currentTimeLabel, totalTimeLabel;
    private PlaylistManager playlistManager;
    private ImageView discView;
    private RotateTransition discRotation;
    private MediaView externalMediaView;

    private boolean isAudio = false;
    private boolean mediaLoaded = false;

    public MediaView getMediaView() { return mediaView; }
    public void setSeekSlider(Slider slider) { this.seekSlider = slider; }
    public void setCurrentTimeLabel(Label label) { this.currentTimeLabel = label; }
    public void setTotalTimeLabel(Label label) { this.totalTimeLabel = label; }
    public void setPlaylistManager(PlaylistManager manager) { this.playlistManager = manager; }

    // For disc animation
    public void setDiscView(ImageView discView, RotateTransition discRotation, MediaView externalMediaView) {
        this.discView = discView;
        this.discRotation = discRotation;
        this.externalMediaView = externalMediaView;
    }

    public void playMedia(int index) {
        if (playlistManager == null || !playlistManager.hasPlaylist()) return;
        if (mediaPlayer != null) mediaPlayer.stop();
        Media media = playlistManager.getMedia(index);
        if (media == null) return;
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // Decide audio or video
        String fileName = playlistManager.getFileName(index).toLowerCase();
        isAudio = fileName.endsWith(".mp3") || fileName.endsWith(".wav") || fileName.endsWith(".aiff");
        mediaLoaded = true;

        if (discView != null && discRotation != null && externalMediaView != null) {
            if (isAudio) {
                discView.setVisible(true);
                externalMediaView.setVisible(false);
                discRotation.play();
            } else {
                discView.setVisible(false);
                externalMediaView.setVisible(true);
                discRotation.stop();
                discView.setRotate(0);
            }
        }

        // Set up seek and time labels
        mediaPlayer.setOnReady(() -> {
            Duration total = mediaPlayer.getTotalDuration();
            seekSlider.setMax(total.toSeconds());
            totalTimeLabel.setText(formatTime(total));
            currentTimeLabel.setText("00:00");
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!pauseSeekUpdate) {
                seekSlider.setValue(newTime.toSeconds());
            }
            currentTimeLabel.setText(formatTime(newTime));
        });

        seekSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
            }
        });

        mediaPlayer.play();
        playlistManager.setCurrentIndex(index);
    }

    public void play() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void seek(Duration duration) {
        if (mediaPlayer != null) mediaPlayer.seek(duration);
    }

    public void setVolume(double value) {
        if (mediaPlayer != null) mediaPlayer.setVolume(value);
    }

    public void setPauseSeekUpdate(boolean pause) {
        this.pauseSeekUpdate = pause;
    }

    public boolean isAudio() { return isAudio; }
    public boolean isMediaLoaded() { return mediaLoaded; }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
