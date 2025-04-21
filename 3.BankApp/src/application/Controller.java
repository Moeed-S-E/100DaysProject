package application;

import javafx.event.ActionEvent;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
	private Stage stage;
	private Scene scene;
	private  Parent root;
	
	public void WelcomePage(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Welcome.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	public void PinPage(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	public void switchToWithdrawPage(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("WithdrawPage.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	public void switchToTranscationsPage(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Transactions.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	public void switchToQuickWithdrawPage(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("QuickWithdraw.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	public void switchToMainWithdraw(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("WithdrawMain.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToBalCheck(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Transactions.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void subBalance(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Transactions.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
    
}
