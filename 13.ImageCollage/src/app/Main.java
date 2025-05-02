package app;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private final Pane collagePane = new Pane();
    private final List<ImageView> imageViews = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        collagePane.setPrefSize(800, 600);
        collagePane.setStyle("-fx-background-color: white;");
        
        BorderPane root = new BorderPane();
        root.setCenter(collagePane);
        root.setTop(createToolbar(primaryStage));

        Scene scene = new Scene(root, 500,500);
        primaryStage.setTitle("JavaFX Image Collage App");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        
        primaryStage.show();
    }

    private ToolBar createToolbar(Stage stage) {
        Button loadButton = new Button("Load Images");
        loadButton.setOnAction(e -> loadImages(stage));

        Button exportButton = new Button("Export Collage");
        exportButton.setOnAction(e -> exportCollage(stage));

        Button tileButton = new Button("Auto Tile");
        tileButton.setOnAction(e -> autoTile());

        Button backgroundButton = new Button("Set Background");
        backgroundButton.setOnAction(e -> setBackgroundColor());

        return new ToolBar(loadButton, exportButton, tileButton, backgroundButton);
    }

    private void loadImages(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Images");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        List<File> files = chooser.showOpenMultipleDialog(stage);
        if (files != null) {
            for (File file : files) {
                addImage(file.toURI().toString(), Math.random() * 600, Math.random() * 400);
            }
        }
    }

    private void addImage(String uri, double x, double y) {
        Image image = new Image(uri);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(150);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);

        makeInteractive(imageView);

        collagePane.getChildren().add(imageView);
        imageViews.add(imageView);
    }

    private void makeInteractive(ImageView view) {
        final double[] dragDelta = new double[2];

        view.setOnMousePressed(e -> {
            dragDelta[0] = view.getLayoutX() - e.getSceneX();
            dragDelta[1] = view.getLayoutY() - e.getSceneY();
        });

        view.setOnMouseDragged(e -> {
            view.setLayoutX(e.getSceneX() + dragDelta[0]);
            view.setLayoutY(e.getSceneY() + dragDelta[1]);
        });

        // Right-click to rotate
        view.setOnMouseClicked(e -> {
            if (e.isSecondaryButtonDown()) {
                view.setRotate(view.getRotate() + 15);
            }
        });

        // Scroll to resize
        view.addEventHandler(ScrollEvent.SCROLL, e -> {
            double delta = e.getDeltaY() > 0 ? 1.1 : 0.9;
            view.setFitWidth(view.getFitWidth() * delta);
            e.consume();
        });
    }

    private void autoTile() {
        int cols = 3;
        double padding = 10;
        double tileWidth = (collagePane.getWidth() - (cols + 1) * padding) / cols;
        double x = padding, y = padding;
        int col = 0;

        for (ImageView iv : imageViews) {
            iv.setLayoutX(x);
            iv.setLayoutY(y);
            iv.setFitWidth(tileWidth);
            iv.setRotate(0); // Reset rotation for tiling

            x += tileWidth + padding;
            col++;
            if (col >= cols) {
                col = 0;
                x = padding;
                y += tileWidth + padding;
            }
        }
    }

    private void setBackgroundColor() {
        ColorPicker colorPicker = new ColorPicker();
        Stage popup = new Stage();
        Button okButton = new Button("Apply");
        okButton.setOnAction(e -> {
            Color color = colorPicker.getValue();
            collagePane.setStyle("-fx-background-color: #" + color.toString().substring(2, 8) + ";");
            popup.close();
        });

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setTop(colorPicker);
        pane.setBottom(okButton);

        Scene scene = new Scene(pane, 200, 100);
        popup.setTitle("Choose Background Color");
        popup.setScene(scene);
        popup.show();
    }

    private void exportCollage(Stage stage) {
        WritableImage snapshot = collagePane.snapshot(new SnapshotParameters(), null);
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Collage");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
