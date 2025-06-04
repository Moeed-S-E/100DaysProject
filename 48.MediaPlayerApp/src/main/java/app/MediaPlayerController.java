package app;

import javafx.animation.RotateTransition;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.Duration;

public class MediaPlayerController {
    private final PlaylistManager playlistManager = new PlaylistManager();
    private final MediaPlayerService mediaService = new MediaPlayerService();
    private final BorderPane root = new BorderPane();

    // UI Components
    private final ListView<String> playlistView = playlistManager.getPlaylistView();
    private final MediaView mediaView = mediaService.getMediaView();
    private final Label currentTimeLabel = new Label("00:00");
    private final Label totalTimeLabel = new Label("00:00");
    private final Slider seekSlider = new Slider();
    private final Slider volumeSlider = new Slider(0, 1, 0.5);
    private final Label volumeIcon = new Label("\uD83D\uDD0A"); // 🔊

    // Disc image and animation
    private final ImageView discView = new ImageView();
    private final RotateTransition discRotation = new RotateTransition(Duration.seconds(2), discView);

    // Play/Pause toggle
    private final Button playPauseButton = new Button("▶");
    private boolean isPlaying = false;

    public MediaPlayerController() {
        setupDiscImage();
        setupLayout();
        setupEvents();
        setupBindings();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void setupDiscImage() {
        try {
            Image discImage = new Image(getClass().getResource("/disc.png").toExternalForm());
            discView.setImage(discImage);
            discView.setFitWidth(220);
            discView.setFitHeight(220);
            discView.setPreserveRatio(true);
            discView.setVisible(false);

            discRotation.setByAngle(360);
            discRotation.setCycleCount(RotateTransition.INDEFINITE);
            discRotation.setInterpolator(javafx.animation.Interpolator.LINEAR);
        } catch (Exception e) {
            System.err.println("Error loading disc.png: " + e.getMessage());
        }
    }

    private void setupLayout() {
        // Playlist Panel with max height
        VBox playlistPanel = new VBox(playlistView);
        playlistPanel.setId("playlist-panel");
        playlistPanel.setPrefWidth(220);
        playlistPanel.setMaxHeight(Double.MAX_VALUE);

        playlistView.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(playlistView, Priority.ALWAYS);

        // Controls
        Button prevButton = new Button("⏮");
        Button nextButton = new Button("⏭");
        Button loadButton = new Button("⏏");

        seekSlider.setMin(0);
        seekSlider.setMax(100);
        seekSlider.setValue(0);
        seekSlider.setPrefWidth(350);

        volumeSlider.setPrefWidth(100);

        currentTimeLabel.setId("current-time");
        totalTimeLabel.setId("total-time");
        HBox timeBox = new HBox(5, currentTimeLabel, new Label("/"), totalTimeLabel);

        volumeIcon.setId("volume-icon");

        HBox controls = new HBox(10, loadButton, prevButton, playPauseButton, nextButton,
                                 seekSlider, timeBox, volumeIcon, volumeSlider);
        controls.setId("controls-bar");
        controls.setStyle("-fx-alignment: center;");

        // StackPane to overlay disc and mediaView
        StackPane centerPane = new StackPane(discView, mediaView);
        root.setCenter(centerPane);
        root.setRight(playlistPanel);
        root.setBottom(controls);

        // Button Actions
        loadButton.setOnAction(e -> playlistManager.loadMediaFiles(root.getScene().getWindow()));
        prevButton.setOnAction(e -> playPrevious());
        nextButton.setOnAction(e -> playNext());
        playPauseButton.setOnAction(e -> togglePlayPause());
        playlistView.setOnMouseClicked(e -> { if (e.getClickCount() == 2) playSelected(); });
    }

    private void setupEvents() {
        // Seek slider events
        seekSlider.setOnMousePressed(e -> mediaService.setPauseSeekUpdate(true));
        seekSlider.setOnMouseReleased(e -> {
            mediaService.seek(Duration.seconds(seekSlider.getValue()));
            mediaService.setPauseSeekUpdate(false);
        });

        // Volume slider event (icon + volume)
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            mediaService.setVolume(newVal.doubleValue());
            if (newVal.doubleValue() == 0.0) {
                volumeIcon.setText("\uD83D\uDD07"); // Muted
            } else {
                volumeIcon.setText("\uD83D\uDD0A"); // Speaker
            }
        });
    }

    private void setupBindings() {
        mediaService.setSeekSlider(seekSlider);
        mediaService.setCurrentTimeLabel(currentTimeLabel);
        mediaService.setTotalTimeLabel(totalTimeLabel);
        mediaService.setPlaylistManager(playlistManager);
        mediaService.setDiscView(discView, discRotation, mediaView);
    }

    private void togglePlayPause() {
        if (mediaService.isMediaLoaded()) {
            if (isPlaying) {
                mediaService.pause();
                playPauseButton.setText("▶");
                // Pause disc rotation if visible
                if (mediaService.isAudio() && discRotation.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                    discRotation.pause();
                }
                isPlaying = false;
            } else {
                mediaService.play();
                playPauseButton.setText("⏸");
                // Resume disc rotation if visible
                if (mediaService.isAudio() && discRotation.getStatus() != javafx.animation.Animation.Status.RUNNING) {
                    discRotation.play();
                }
                isPlaying = true;
            }
        }
    }

    private void playSelected() {
        int index = playlistView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            mediaService.playMedia(index);
            playlistView.getSelectionModel().select(index);
            playPauseButton.setText("⏸");
            isPlaying = true;
        }
    }

    private void playNext() {
        int next = playlistManager.getNextIndex();
        mediaService.playMedia(next);
        playlistView.getSelectionModel().select(next);
        playPauseButton.setText("⏸");
        isPlaying = true;
    }

    private void playPrevious() {
        int prev = playlistManager.getPreviousIndex();
        mediaService.playMedia(prev);
        playlistView.getSelectionModel().select(prev);
        playPauseButton.setText("⏸");
        isPlaying = true;
    }
}
