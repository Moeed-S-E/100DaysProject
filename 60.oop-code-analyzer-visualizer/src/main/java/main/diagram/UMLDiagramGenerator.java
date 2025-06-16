package main.diagram;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.model.AnalysisResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import net.sourceforge.plantuml.SourceStringReader;
import main.model.ClassInfo;

public class UMLDiagramGenerator {
    public static ImageView generateDiagramNode(List<ClassInfo> classInfoList) {
        // Generate PlantUML code
        String plantUmlCode = PlantUMLGenerator.generate(classInfoList);

        try {
            SourceStringReader reader = new SourceStringReader(plantUmlCode);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // Write the first image to "os"
            reader.generateImage(os);
            os.close();
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            Image image = new Image(is);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
//            imageView.setFitWidth(800); // You can adjust the size as needed
            return imageView;
        } catch (IOException e) {
            e.printStackTrace();
            // If error, return a placeholder
            return new ImageView();
        }
    }
}
