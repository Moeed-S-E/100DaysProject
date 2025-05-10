package application.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import application.core.*;

public class MainWindow extends Application {

    private NavigationHistory navigationHistory = new NavigationHistory();
    private UndoRedoManager undoRedoManager = new UndoRedoManager();
    private BorderPane root = new BorderPane();
    private TreeView<Path> treeView;
    private ListView<Path> listView = new ListView<>();
    private Label statusBar = new Label("Ready");
    private WatchService watchService;
    private WatchKey watchKey;

    private Button backButton;
    private Button forwardButton;
    private Button undoButton;
    private Button redoButton;

    private final Image folderIcon = new Image(getClass().getResourceAsStream("/icons/folder.png"));
    private final Image fileIcon = new Image(getClass().getResourceAsStream("/icons/file.png"));

//    @Override
//    public void start(Stage primaryStage) {
//        setupToolbar();
//        setupTreeView();
//        setupListView();
//        setupStatusBar();
//
//        root.setTop(createToolbar());
//        root.setLeft(treeView);
//        root.setCenter(listView);
//        root.setBottom(statusBar);
//
//        Scene scene = new Scene(root, 800, 600);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("File Navigator");
//        primaryStage.show();
//
//        Path initialPath = Paths.get(System.getProperty("user.home"));
//        navigationHistory.addPath(initialPath);
//        TreeItem<Path> initialItem = findTreeItem(treeView.getRoot(), initialPath);
//        if (initialItem != null) {
//            treeView.getSelectionModel().select(initialItem);
//        } else {
//            setStatus("Initial path not found: " + initialPath);
//        }
//        updateButtonStates();
//    }
    @Override
    public void start(Stage primaryStage) {
        setupToolbar();
        setupTreeView();
        setupListView();
        setupStatusBar();

        root.setTop(createToolbar());
        root.setLeft(treeView);
        root.setCenter(listView);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Navigator");
        primaryStage.show();

        Path initialPath = Paths.get("C:\\");
        navigationHistory.addPath(initialPath);
        TreeItem<Path> initialItem = findTreeItem(treeView.getRoot(), initialPath);
        if (initialItem != null) {
            treeView.getSelectionModel().select(initialItem);
        } else {
            setStatus("Initial path not found: " + initialPath);
        }
        updateButtonStates();
    }
    private void setupToolbar() {
        backButton = new Button("Back");
        forwardButton = new Button("Forward");
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");

        backButton.setOnAction(e -> {
            if (navigationHistory.canGoBack()) {
                Path prevPath = navigationHistory.goBack();
                updateView(prevPath);
            }
        });
        forwardButton.setOnAction(e -> {
            if (navigationHistory.canGoForward()) {
                Path nextPath = navigationHistory.goForward();
                updateView(nextPath);
            }
        });
        undoButton.setOnAction(e -> {
            if (undoRedoManager.canUndo()) {
                undoRedoManager.undo();
                refreshViews();
            }
        });
        redoButton.setOnAction(e -> {
            if (undoRedoManager.canRedo()) {
                undoRedoManager.redo();
                refreshViews();
            }
        });
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().addAll(backButton, forwardButton, undoButton, redoButton);
        return toolbar;
    }

    private void setupTreeView() {
        FileTreeItem rootItem = new FileTreeItem(Paths.get("/"));
        treeView = new TreeView<>(rootItem);
        treeView.setCellFactory(param -> new TextFieldTreeCell<Path>(new StringConverter<Path>() {
            @Override
            public String toString(Path path) {
                if (path != null) {
                    if (path.getFileName() != null) {
                        return path.getFileName().toString();
                    } else {
                        return path.toString();
                    }
                }
                return "Loading...";
            }

            @Override
            public Path fromString(String string) {
                return null;
            }
        }));
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                Path selectedPath = newVal.getValue();
                updateListView(selectedPath);
                navigationHistory.addPath(selectedPath);
                startWatching(selectedPath);
                updateButtonStates();
                setStatus("Selected: " + selectedPath.toString());
            }
        });

        ContextMenu treeContextMenu = new ContextMenu();
        MenuItem openItem = new MenuItem("Open");
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem batchAddItem = new MenuItem("Batch Add");

        openItem.setOnAction(e -> {
            TreeItem<Path> selected = treeView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                treeView.getSelectionModel().select(selected);
            }
        });
        renameItem.setOnAction(e -> handleRename(treeView.getSelectionModel().getSelectedItem().getValue()));
        deleteItem.setOnAction(e -> handleDelete(treeView.getSelectionModel().getSelectedItem().getValue()));
        batchAddItem.setOnAction(e -> setStatus("Batch Add not implemented yet"));

        treeContextMenu.getItems().addAll(openItem, renameItem, deleteItem, batchAddItem);
        treeView.setContextMenu(treeContextMenu);
    }

    private void setupListView() {
        listView.setCellFactory(param -> new ListCell<Path>() {
            @Override
            protected void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getFileName().toString());
                    setGraphic(new ImageView(Files.isDirectory(item) ? folderIcon : fileIcon));
                }
            }
        });

        ContextMenu listContextMenu = new ContextMenu();
        listView.setContextMenu(listContextMenu);
        listView.setOnContextMenuRequested(e -> {
            Path selectedPath = listView.getSelectionModel().getSelectedItem();
            if (selectedPath != null) {
                listContextMenu.getItems().clear();
                MenuItem openItem = new MenuItem("Open");
                MenuItem renameItem = new MenuItem("Rename");
                MenuItem deleteItem = new MenuItem("Delete");

                if (Files.isDirectory(selectedPath)) {
                    openItem.setOnAction(ev -> {
                        TreeItem<Path> item = findTreeItem(treeView.getRoot(), selectedPath);
                        if (item != null) treeView.getSelectionModel().select(item);
                    });
                } else {
                    openItem.setOnAction(ev -> setStatus("File opening not implemented"));
                }
                renameItem.setOnAction(ev -> handleRename(selectedPath));
                deleteItem.setOnAction(ev -> handleDelete(selectedPath));

                listContextMenu.getItems().addAll(openItem, renameItem, deleteItem);
            }
        });
    }

    private void setupStatusBar() {
        statusBar.setStyle("-fx-padding: 5; -fx-background-color: lightgray;");
    }

    private void updateListView(Path directory) {
        Task<List<Path>> task = new Task<List<Path>>() {
            @Override
            protected List<Path> call() throws Exception {
                if (!Files.isReadable(directory)) {
                    throw new IOException("Directory not readable: " + directory);
                }
                return Files.list(directory).collect(Collectors.toList());
            }
        };
        task.setOnSucceeded(event -> {
            Platform.runLater(() -> listView.getItems().setAll(task.getValue()));
        });
        task.setOnFailed(event -> {
            Platform.runLater(() -> setStatus("Error loading files: " + task.getException().getMessage()));
        });
        new Thread(task).start();
    }

    private void updateView(Path path) {
        TreeItem<Path> item = findTreeItem(treeView.getRoot(), path);
        if (item != null) {
            treeView.getSelectionModel().select(item);
        } else {
            updateListView(path);
        }
        updateButtonStates();
    }

    private void refreshViews() {
        Path currentPath = treeView.getSelectionModel().getSelectedItem().getValue();
        if (currentPath != null) {
            updateListView(currentPath);
        }
        updateButtonStates();
    }

    private TreeItem<Path> findTreeItem(TreeItem<Path> root, Path path) {
        if (root == null || path == null) return null;
        Path rootValue = root.getValue();
        if (rootValue != null && rootValue.equals(path)) return root;
        for (TreeItem<Path> child : root.getChildren()) {
            TreeItem<Path> found = findTreeItem(child, path);
            if (found != null) return found;
        }
        return null;
    }

    private void handleRename(Path path) {
        if (path == null) return;
        TextInputDialog dialog = new TextInputDialog(path.getFileName().toString());
        dialog.setTitle("Rename");
        dialog.setHeaderText("Enter new name:");
        dialog.showAndWait().ifPresent(newName -> {
            Path newPath = path.resolveSibling(newName);
            RenameOperation renameOp = new RenameOperation(path, newPath);
            renameOp.execute();
            undoRedoManager.pushUndo(renameOp);
            refreshViews();
            setStatus("Renamed to " + newName);
        });
    }

    private void handleDelete(Path path) {
        if (path == null) return;
        if (Files.isDirectory(path)) {
            setStatus("Deleting directories is not supported.");
            return;
        }
        DeleteOperation deleteOp = new DeleteOperation(path);
        deleteOp.execute();
        undoRedoManager.pushUndo(deleteOp);
        refreshViews();
        setStatus("Deleted: " + path.getFileName());
    }

    private void startWatching(Path dir) {
        if (dir == null) return;
        try {
            if (watchKey != null) watchKey.cancel();
            if (watchService != null) watchService.close();
            watchService = FileSystems.getDefault().newWatchService();
            watchKey = dir.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            new Thread(() -> {
                while (true) {
                    try {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path changed = dir.resolve((Path) event.context());
                            Platform.runLater(() -> {
                                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                    listView.getItems().add(changed);
                                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                                    listView.getItems().remove(changed);
                                } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    listView.refresh();
                                }
                            });
                        }
                        key.reset();
                    } catch (InterruptedException | ClosedWatchServiceException e) {
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            setStatus("WatchService failed: " + e.getMessage());
        }
    }

    private void updateButtonStates() {
        backButton.setDisable(!navigationHistory.canGoBack());
        forwardButton.setDisable(!navigationHistory.canGoForward());
        undoButton.setDisable(!undoRedoManager.canUndo());
        redoButton.setDisable(!undoRedoManager.canRedo());
    }

    private void setStatus(String message) {
        statusBar.setText(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
