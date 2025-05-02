package app;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class ImageFilterUI {
	private BorderPane root;
	private ImageView imageView;
	private BufferedImage originalImage;
	private ImageProcessor processor;
	
	public ImageFilterUI() {
		processor = new ImageProcessor();
		root = new BorderPane();
		
		//Top-Bar of buttons
		HBox buttonBox = new HBox(10);
		buttonBox.setStyle("-fx-background-color: #333333; -fx-padding: 10;");
		Button loadButton = new Button("Load Image");
		loadButton.setPrefWidth(110);
		Button grayScaleButton = new Button("GrayScale");
		grayScaleButton.setPrefWidth(110);
		Button hotButton = new Button("Hot");
		hotButton.setPrefWidth(100);
        Button coldButton = new Button("Cold");
        coldButton.setPrefWidth(110);
        Button brighterButton = new Button("Brighter");
        brighterButton.setPrefWidth(110);
        Button contrastButton = new Button("Contrast");
        contrastButton.setPrefWidth(110);
        Button pixelateButton = new Button("Pixelate");
        pixelateButton.setPrefWidth(110);
        
        String buttonStyle = "-fx-text-fill: #F4F4F4; -fx-background-color: #555555; -fx-font-size: 14;";
        loadButton.setStyle(buttonStyle);
        grayScaleButton.setStyle(buttonStyle);
        hotButton.setStyle(buttonStyle);
        coldButton.setStyle(buttonStyle);
        brighterButton.setStyle(buttonStyle);
        contrastButton.setStyle(buttonStyle);
        pixelateButton.setStyle(buttonStyle);
        
        buttonBox.getChildren().addAll(loadButton,grayScaleButton,hotButton,coldButton,brighterButton,contrastButton,pixelateButton);

        //Image display in Center
        imageView = new ImageView();
        imageView.setFitHeight(400);
        imageView.setFitWidth(600);
		imageView.setPreserveRatio(true);
		
		//Set layout
		root.setTop(buttonBox);
		root.setCenter(imageView);
		
		//Choose File
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
	            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
	        );
		
		//Buttons actions
		loadButton.setOnAction(e->{
			File file = fileChooser.showOpenDialog(root.getScene().getWindow());
			if (file != null) {
				try {
					originalImage = ImageIO.read(file);
					Image fxImage = SwingFXUtils.toFXImage(originalImage, null);
					imageView.setImage(fxImage);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		grayScaleButton.setOnAction(e ->{
			if (originalImage != null) {
				BufferedImage filtered = processor.applyGrayscale(originalImage);
				Image fxImage = SwingFXUtils.toFXImage(filtered, null);
				imageView.setImage(fxImage);
			}
		});
		hotButton.setOnAction(e -> {
            if (originalImage != null) {
                BufferedImage filtered = processor.applyHot(originalImage);
                Image fxImage = SwingFXUtils.toFXImage(filtered, null);
                imageView.setImage(fxImage);
            }
        });

        coldButton.setOnAction(e -> {
            if (originalImage != null) {
                BufferedImage filtered = processor.applyCold(originalImage);
                Image fxImage = SwingFXUtils.toFXImage(filtered, null);
                imageView.setImage(fxImage);
            }
        });

        brighterButton.setOnAction(e -> {
            if (originalImage != null) {
                BufferedImage filtered = processor.applyBrighter(originalImage);
                Image fxImage = SwingFXUtils.toFXImage(filtered, null);
                imageView.setImage(fxImage);
            }
        });

        contrastButton.setOnAction(e -> {
            if (originalImage != null) {
                BufferedImage filtered = processor.applyContrast(originalImage);
                Image fxImage = SwingFXUtils.toFXImage(filtered, null);
                imageView.setImage(fxImage);
            }
        });

        pixelateButton.setOnAction(e -> {
            if (originalImage != null) {
                BufferedImage filtered = processor.applyPixelate(originalImage);
                Image fxImage = SwingFXUtils.toFXImage(filtered, null);
                imageView.setImage(fxImage);
            }
        });
		
		
	}
	
	public BorderPane getRoot() {
		return root;
	}
}
