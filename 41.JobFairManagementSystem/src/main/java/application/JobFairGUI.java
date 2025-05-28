package application;

import application.constants.UIConstants;
import application.controller.JobFairController;
import application.model.Admin;
import application.model.Student;
import application.ui.panels.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class JobFairGUI extends Application {
    private Label statusLabel;
    private Admin admin;
    private ObservableList<Student> studentsList = FXCollections.observableArrayList();
    private JobFairController controller;

    public JobFairGUI() {
        statusLabel = new Label("🎯 Welcome to Modern Job Fair Management System");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, UIConstants.NORMAL_FONT_SIZE));
        statusLabel.setTextFill(Color.web(UIConstants.DARK_GREEN));
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseConnection.initializeDatabase();
        } catch (RuntimeException e) {
            showDatabaseErrorAlert(e);
            return;
        }

        primaryStage.setTitle("🎯 Modern Job Fair Management System");
        admin = new Admin("Sir Bilal", 1087);
        controller = new JobFairController(admin, studentsList, this::updateStatus);

        BorderPane root = createMainLayout();
        Scene scene = new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        scene.getStylesheets().add(createModernCSS());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        root.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom right, %s, %s);", 
            UIConstants.LIGHT_MINT, UIConstants.LIGHT_CYAN));

        root.setTop(createEnhancedHeader());
        root.setCenter(createModernTabPane());
        root.setBottom(createEnhancedFooter());
        
        return root;
    }
    

    private TabPane createModernTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab adminTab = new Tab("🔧 Admin Panel");
        adminTab.setContent(new AdminPanel(controller).createPanel());

        Tab loadDataTab = new Tab("📂 Load Previous Fair");
        loadDataTab.setContent(new JobFairSelectionPanel(controller).createPanel());

        Tab studentsTab = new Tab("👨‍🎓 Student Management");
        StudentsPanel studentsPanel = new StudentsPanel(controller, studentsList);
        studentsTab.setContent(studentsPanel.createPanel());

        Tab recruitersTab = new Tab("🏢 Recruiter Actions");
        recruitersTab.setContent(new RecruitersPanel(controller).createPanel());

        Tab analyticsTab = new Tab("📊 Analytics Dashboard");
        analyticsTab.setContent(new AnalyticsPanel(controller).createPanel());

        tabPane.getTabs().addAll(adminTab, loadDataTab, studentsTab, recruitersTab, analyticsTab);
        return tabPane;
    }

    
    private VBox createEnhancedHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle(String.format(
            "-fx-background-color: linear-gradient(to right, %s, %s);" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-width: 0 0 2 0;",
            UIConstants.DARK_GREEN, UIConstants.MEDIUM_GREEN));

        Label titleLabel = new Label("🎯 Modern Job Fair Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, UIConstants.TITLE_FONT_SIZE));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 2);");

        Label subtitleLabel = new Label("Streamlined recruitment process for educational institutions");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, UIConstants.SUBTITLE_FONT_SIZE));
        subtitleLabel.setTextFill(Color.web("#e8f5e8"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private HBox createEnhancedFooter() {
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(15, 20, 15, 20));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 2 0 0 0;",
            UIConstants.GLASS_WHITE));

        Label copyrightLabel = new Label("© 2025 Job Fair Management System");
        copyrightLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        copyrightLabel.setTextFill(Color.web(UIConstants.DARK_GREEN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren().addAll(statusLabel, spacer, copyrightLabel);
        return footer;
    }

    private String createModernCSS() {
        String css = 
            ".tab-pane {\n" +
            "    -fx-tab-min-height: 45px;\n" +
            "    -fx-tab-max-height: 45px;\n" +
            "}\n" +
            "\n" +
            ".tab {\n" +
            "    -fx-background-color: rgba(255, 255, 255, 0.1);\n" +
            "    -fx-background-radius: 10 10 0 0;\n" +
            "    -fx-border-color: rgba(255, 255, 255, 0.3);\n" +
            "    -fx-border-width: 1 1 0 1;\n" +
            "    -fx-border-radius: 10 10 0 0;\n" +
            "    -fx-padding: 10 20;\n" +
            "}\n" +
            "\n" +
            ".tab:selected {\n" +
            "    -fx-background-color: rgba(255, 255, 255, 0.9);\n" +
            "}\n" +
            "\n" +
            ".tab .tab-label {\n" +
            "    -fx-font-family: \"Segoe UI\";\n" +
            "    -fx-font-weight: bold;\n" +
            "    -fx-font-size: 14px;\n" +
            "}\n" +
            "\n" +
            ".tab:selected .tab-label {\n" +
            "    -fx-text-fill: " + UIConstants.DARK_GREEN + ";\n" +
            "}\n" +
            "\n" +
            ".table-view {\n" +
            "    -fx-selection-bar: " + UIConstants.LIGHT_GREEN + ";\n" +
            "    -fx-selection-bar-non-focused: " + UIConstants.LIGHT_GREEN + ";\n" +
            "}\n" +
            "\n" +
            ".table-row-cell:selected {\n" +
            "    -fx-background-color: " + UIConstants.LIGHT_GREEN + ";\n" +
            "    -fx-text-fill: " + UIConstants.DARK_GREEN + ";\n" +
            "}\n" +
            "\n" +
            ".scroll-pane {\n" +
            "    -fx-background-color: transparent;\n" +
            "}\n" +
            "\n" +
            ".scroll-pane .viewport {\n" +
            "    -fx-background-color: transparent;\n" +
            "}";
        
        try {
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("jobfair-styles", ".css");
            java.nio.file.Files.write(tempFile, css.getBytes());
            return tempFile.toUri().toString();
        } catch (Exception e) {
            return "data:text/css;base64," + java.util.Base64.getEncoder().encodeToString(css.getBytes());
        }
    }


    private void showDatabaseErrorAlert(RuntimeException e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Database Connection Error");
        errorAlert.setHeaderText("Failed to Initialize Database");
        errorAlert.setContentText("Could not connect to the database. Please check your MySQL server and configuration.\n\n" +
                                 "Error: " + e.getMessage() + "\n\n" +
                                 "Make sure:\n" +
                                 "• MySQL server is running\n" +
                                 "• Database credentials are correct\n" +
                                 "• Required database exists\n" +
                                 "• MySQL JDBC driver is in classpath");
        
        errorAlert.setResizable(true);
        errorAlert.getDialogPane().setPrefSize(600, 400);
        
        TextArea textArea = new TextArea(getStackTrace(e));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        
        Label expandableLabel = new Label("Exception Details:");
        
        errorAlert.getDialogPane().setExpandableContent(new VBox(expandableLabel, textArea));
        errorAlert.showAndWait();
    }

    private String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
        String color = message.contains("✅") ? UIConstants.MEDIUM_GREEN :
                      message.contains("❌") ? UIConstants.ERROR_RED :
                      message.contains("⚠️") ? UIConstants.WARNING_ORANGE : UIConstants.DARK_GREEN;
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
