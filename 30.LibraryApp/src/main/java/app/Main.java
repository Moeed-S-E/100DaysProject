package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        TabPane tabPane = new TabPane();
        
        // Books Tab
        Tab booksTab = new Tab("Books");
        booksTab.setClosable(false);
        booksTab.setContent(FXMLLoader.load(getClass().getResource("/Books.fxml")));
        
        // Members Tab
        Tab membersTab = new Tab("Members");
        membersTab.setClosable(false);
        membersTab.setContent(FXMLLoader.load(getClass().getResource("/Members.fxml")));
        
        // Issued Books Tab
        Tab issuedBooksTab = new Tab("Issued Books");
        issuedBooksTab.setClosable(false);
        issuedBooksTab.setContent(FXMLLoader.load(getClass().getResource("/IssuedBooks.fxml")));
        
        tabPane.getTabs().addAll(booksTab, membersTab, issuedBooksTab);
        
        Scene scene = new Scene(tabPane, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library Management App");
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}