package application.gui;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class FileTreeItem extends TreeItem<Path> {
    private boolean childrenLoaded = false;
    private final Image folderIcon = new Image(getClass().getResourceAsStream("/icons/folder.png"));
    private final Image fileIcon = new Image(getClass().getResourceAsStream("/icons/file.png"));

    public FileTreeItem(Path path) {
        super(path);
        setGraphic(new ImageView(Files.isDirectory(path) ? folderIcon : fileIcon));
        expandedProperty().addListener((obs, wasExpanded, isExpanded) -> {
            if (isExpanded && !childrenLoaded) {
                loadChildren();
            }
        });
    }

    @Override
    public ObservableList<TreeItem<Path>> getChildren() {
        if (!childrenLoaded) {
            ObservableList<TreeItem<Path>> children = super.getChildren();
            if (children.isEmpty()) {
                TreeItem<Path> loadingItem = new TreeItem<>(null);
                loadingItem.setGraphic(new Label("Loading..."));
                children.add(loadingItem);
            }
        }
        return super.getChildren();
    }

    private void loadChildren() {
        Path path = getValue();
        if (path == null) {
            childrenLoaded = true;
            return;
        }
        if (Files.isDirectory(path)) {
            if (!Files.isReadable(path)) {
                System.err.println("Directory not readable: " + path);
                ObservableList<TreeItem<Path>> children = super.getChildren();
                Platform.runLater(() -> {
                    children.clear();
                    childrenLoaded = true;
                });
                return;
            }
            Task<List<TreeItem<Path>>> task = new Task<List<TreeItem<Path>>>() {
                @Override
                protected List<TreeItem<Path>> call() throws Exception {
                    List<TreeItem<Path>> childrenList = new ArrayList<>();
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                        for (Path child : stream) {
                            childrenList.add(new FileTreeItem(child));
                        }
                    }
                    return childrenList;
                }
            };
            task.setOnSucceeded(event -> {
                ObservableList<TreeItem<Path>> children = super.getChildren();
                Platform.runLater(() -> {
                    children.clear();
                    children.setAll(task.getValue());
                    childrenLoaded = true;
                });
            });
            task.setOnFailed(event -> {
                System.err.println("Error loading children for " + path + ": " + task.getException().getMessage());
                ObservableList<TreeItem<Path>> children = super.getChildren();
                Platform.runLater(() -> {
                    children.clear();
                    childrenLoaded = true;
                });
            });
            new Thread(task).start();
        } else {
            ObservableList<TreeItem<Path>> children = super.getChildren();
            Platform.runLater(() -> children.clear());
            childrenLoaded = true;
        }
    }

    @Override
    public boolean isLeaf() {
        Path path = getValue();
        return path == null || !Files.isDirectory(path);
    }
}