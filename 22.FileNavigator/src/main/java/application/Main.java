package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.concurrent.Task;
import java.util.*;
import java.io.File;

public class Main extends Application {

    private TreeView<String> treeView;
    private ListView<String> listView;
    private Stack<FileOperation> undoStack = new Stack<>();
    private Stack<FileOperation> redoStack = new Stack<>();
    private NavigationHistory navigationHistory = new NavigationHistory();
    private ProgressBar progressBar = new ProgressBar(0);
    private TextField searchField = new TextField();
    private ObservableList<String> fileList = FXCollections.observableArrayList();
    private FilteredList<String> filteredFileList = new FilteredList<>(fileList, p -> true);

    @Override
    public void start(Stage primaryStage) {
        // Initialize UI components
        treeView = new TreeView<>(createTreeItem(new File("C:\\")));
        listView = new ListView<>(filteredFileList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Buttons for undo/redo
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");
        Button batchDeleteButton = new Button("Batch Delete");

        // Layout setup
        VBox leftPane = new VBox(treeView);
        VBox rightPane = new VBox(searchField, listView, progressBar, batchDeleteButton, undoButton, redoButton);
        HBox root = new HBox(leftPane, rightPane);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Navigator");
        primaryStage.show();

        // 3.1 Connect Navigation History
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String path = getFullPath(newVal);
                navigationHistory.addPath(path);
                updateListView(path);
            }
        });

        // 3.2 Undo/Redo Workflow
        undoButton.setOnAction(e -> undoOperation());
        redoButton.setOnAction(e -> redoOperation());

        // 3.3 Batch Processing
        batchDeleteButton.setOnAction(e -> {
            List<String> selectedFiles = new ArrayList<>(listView.getSelectionModel().getSelectedItems());
            if (!selectedFiles.isEmpty()) {
                processBatchDelete(selectedFiles);
            }
        });

        // 3.4 Search & Filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredFileList.setPredicate(fileName -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return fileName.toLowerCase().contains(newVal.toLowerCase());
            });
        });
    }

    // Helper to create TreeItem structure
    private TreeItem<String> createTreeItem(File file) {
        TreeItem<String> item = new TreeItem<>(file.getName());
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                item.getChildren().add(createTreeItem(subFile));
            }
        }
        return item;
    }

    // Get full path from TreeItem
    private String getFullPath(TreeItem<String> item) {
        List<String> pathParts = new ArrayList<>();
        TreeItem<String> current = item;
        while (current != null) {
            pathParts.add(0, current.getValue());
            current = current.getParent();
        }
        return String.join("\\", pathParts);
    }

    // Update ListView based on selected folder
    private void updateListView(String folderPath) {
        fileList.clear();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                fileList.add(file.getName());
            }
        }
    }

    // Undo operation
    private void undoOperation() {
        if (!undoStack.isEmpty()) {
            FileOperation op = undoStack.pop();
            op.undo();
            redoStack.push(op);
            updateListView(navigationHistory.getCurrentPath());
        }
    }

    // Redo operation
    private void redoOperation() {
        if (!redoStack.isEmpty()) {
            FileOperation op = redoStack.pop();
            op.redo();
            undoStack.push(op);
            updateListView(navigationHistory.getCurrentPath());
        }
    }

    // Batch delete with progress bar
//    private void processBatchDelete(List<String> files) {
//        Task<Void> task = new Task<>() {
//            @Override
//            protected Void call() {
//                int total = files.size();
//                for (int i = 0; i < total; i++) {
//                    String fileName = files.get(i);
//                    FileOperation deleteOp = new DeleteOperation(fileName);
//                    deleteOp.execute();
//                    undoStack.push(deleteOp);
//                    redoStack.clear();
//                    updateProgress(i + 1, total);
//                    try {
//                        Thread.sleep(500); // Simulate work
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return null;
//            }
//        };
//        progressBar.progressProperty().bind(task.progressProperty());
//        new Thread(task).start();
//        task.setOnSucceeded(e -> updateListView(navigationHistory.getCurrentPath()));
//    }
    private void processBatchDelete(List<String> files) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                int total = files.size();
                for (int i = 0; i < total; i++) {
                    String fileName = files.get(i);
                    FileOperation deleteOp = new DeleteOperation(fileName);
                    deleteOp.execute();
                    undoStack.push(deleteOp);
                    redoStack.clear();
                    updateProgress(i + 1, total);
                    try {
                        Thread.sleep(500); // Simulate work
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        task.setOnSucceeded(e -> updateListView(navigationHistory.getCurrentPath()));
    }

    // Navigation history class
    private static class NavigationHistory {
        private List<String> history = new ArrayList<>();
        private int currentIndex = -1;

        public void addPath(String path) {
            if (currentIndex < history.size() - 1) {
                history.subList(currentIndex + 1, history.size()).clear();
            }
            history.add(path);
            currentIndex++;
        }

        public String getCurrentPath() {
            return currentIndex >= 0 ? history.get(currentIndex) : "";
        }
    }

    // Abstract FileOperation class
    private abstract static class FileOperation {
        @SuppressWarnings("unused")
		protected String filePath;

        public FileOperation(String filePath) {
            this.filePath = filePath;
        }

        abstract void execute();
        abstract void undo();
        abstract void redo();
    }

    // Delete operation example
    private static class DeleteOperation extends FileOperation {
        private File file;
        @SuppressWarnings("unused")
		private byte[] fileContent; // Simplified for demo

        public DeleteOperation(String filePath) {
            super(filePath);
            this.file = new File(filePath);
        }

        @Override
        void execute() {
            if (file.exists()) {
                // Simulate file content backup
                fileContent = new byte[1024]; // Placeholder
                file.delete();
            }
        }

        @Override
        void undo() {
            // Simulate restoring file
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        void redo() {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}