import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class WordCounterApp extends Application{
	private final String title = "Word Counter";
	private int count;
	public void start(Stage primaryStage) throws Exception {
		Pane root = new Pane();
		root.setStyle("-fx-background-color: #333;-fx-text-fill: #fff;");
		
		Label label = new Label(title);
		label.setPrefSize(500, 30);
		label.setStyle("-fx-background-color: #222;-fx-text-fill: #fff;-fx-alignment: center;");
		label.setFont(new Font("Arial", 18));
		
		
		TextArea textArea = new TextArea();
		textArea.setLayoutY(30);
		textArea.setPrefSize(500-5, 400);
		textArea.setWrapText(true);
		
		Button button = new Button("Count");
		button.setPrefSize(80, 40);
		button.setLayoutY(430);
		button.setLayoutX(210);
		
		button.setOnMouseClicked(e ->{
			count = textArea.getText().length();
			System.out.println(count);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(title);
			alert.setContentText("Words: "+count);
			alert.showAndWait();
			textArea.setText("");
		});
		
		
				
				
				
		
		root.getChildren().add(textArea);
		root.getChildren().add(label);
		root.getChildren().add(button);
		Scene scene = new Scene(root);
		primaryStage.setTitle(title+" App");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
//		AppFrame app = new AppFrame();
	}
}
