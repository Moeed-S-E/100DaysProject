//package main.ui;
//
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import main.analyzer.CodeAnalyzer;
//import main.diagram.UMLDiagramGenerator;
//import main.model.AnalysisResult;
//import main.model.SummaryItem;
//
//public class MainView extends BorderPane {
//    private TextArea codeInput;
//    private Button analyzeButton;
//    private TableView<SummaryItem> summaryTable;
//    private Pane umlDiagramPane;
//    private Pane swingVisualizerPane;
//
//    public MainView() {
//        // Title Bar
//        Label titleLabel = new Label("OOP Code Analyzer & Visualizer");
//        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
//        titleLabel.setTextFill(Color.web("#2d3436"));
//        titleLabel.setPadding(new Insets(20, 0, 10, 20));
//
//        // Input area
//        codeInput = new TextArea();
//        codeInput.setPromptText("Paste or write Java code here...");
//        codeInput.setPrefRowCount(18);
//        codeInput.setFont(Font.font("JetBrains Mono", 14));
//        codeInput.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
//        codeInput.setPrefWidth(800);
//        codeInput.setPrefHeight(400);
//
//        analyzeButton = new Button("🔍 Analyze Code");
//        analyzeButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
//        analyzeButton.setStyle(
//            "-fx-background-color: #0984e3; -fx-text-fill: white; -fx-background-radius: 8;"
//        );
//        analyzeButton.setOnAction(e -> analyzeCode());
//
//        VBox inputBox = new VBox(15, codeInput, analyzeButton);
//        inputBox.setPadding(new Insets(20));
//        inputBox.setStyle(
//            "-fx-background-color: #f5f6fa; -fx-background-radius: 16; -fx-border-radius: 16; " +
//            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);"
//        );
//
//        // Output area
//        summaryTable = SummaryTableFactory.createSummaryTable();
//        summaryTable.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
//        summaryTable.setPrefWidth(900);
//        summaryTable.setPrefHeight(400);
//        umlDiagramPane = new StackPane();
//        umlDiagramPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-border-radius: 10;");
//        swingVisualizerPane = new StackPane();
//        swingVisualizerPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-border-radius: 10;");
//
//        TabPane outputTabs = new TabPane();
//        outputTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
//
//        Tab summaryTab = new Tab("📋 Summary Table", summaryTable);
//        Tab umlTab = new Tab("🗂 UML Diagram", umlDiagramPane);
//
//        outputTabs.getTabs().addAll(summaryTab, umlTab);
//        outputTabs.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
//
//        VBox centerBox = new VBox(20, outputTabs);
//        centerBox.setPadding(new Insets(20));
//        centerBox.setStyle(
//            "-fx-background-color: #f5f6fa; -fx-background-radius: 16; -fx-border-radius: 16; " +
//            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);"
//        );
//
//        setTop(titleLabel);
//        setLeft(inputBox);
//        setCenter(centerBox);
//        setStyle("-fx-background-color: linear-gradient(to bottom right, #dfe6e9, #b2bec3);");
//    }
//
//    private void analyzeCode() {
//        String code = codeInput.getText();
//        AnalysisResult result = CodeAnalyzer.analyze(code);
//        summaryTable.setItems(result.getSummaryItems());
//        umlDiagramPane.getChildren().setAll(
//            UMLDiagramGenerator.generateDiagramNode(result)
//        );
//    }
//}
package main.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.analyzer.CodeAnalyzer;
import main.analyzer.UMLCodeAnalyzer;
import main.diagram.UMLDiagramGenerator;
import main.diagram.PlantUMLGenerator;
import main.model.AnalysisResult;
import main.model.SummaryItem;
import main.model.ClassInfo;

import java.util.List;

public class MainView extends BorderPane {
    private TextArea codeInput;
    private Button analyzeButton;
    private TableView<SummaryItem> summaryTable;
    private TextArea umlCodeArea;
    private Pane umlDiagramPane;

    public MainView() {
        // Title Bar
        Label titleLabel = new Label("OOP Code Analyzer & Visualizer");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#2d3436"));
        titleLabel.setPadding(new Insets(20, 0, 10, 20));

        // Input area
        codeInput = new TextArea();
        codeInput.setPromptText("Paste or write Java code here...");
        codeInput.setPrefRowCount(18);
        codeInput.setFont(Font.font("JetBrains Mono", 14));
        codeInput.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
        codeInput.setPrefWidth(800);
        codeInput.setPrefHeight(400);

        analyzeButton = new Button("🔍 Analyze Code");
        analyzeButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        analyzeButton.setStyle(
            "-fx-background-color: #0984e3; -fx-text-fill: white; -fx-background-radius: 8;"
        );
        analyzeButton.setOnAction(e -> analyzeCode());

        VBox inputBox = new VBox(15, codeInput, analyzeButton);
        inputBox.setPadding(new Insets(20));
        inputBox.setStyle(
            "-fx-background-color: #f5f6fa; -fx-background-radius: 16; -fx-border-radius: 16; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);"
        );

        // Output area
        summaryTable = SummaryTableFactory.createSummaryTable();
        summaryTable.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
        summaryTable.setPrefWidth(900);
        summaryTable.setPrefHeight(400);

        umlCodeArea = new TextArea();
        umlCodeArea.setFont(Font.font("JetBrains Mono", 14));
        umlCodeArea.setEditable(false);
        umlCodeArea.setPrefWidth(900);
        umlCodeArea.setPrefHeight(400);

        umlDiagramPane = new StackPane(umlCodeArea);
        umlDiagramPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-border-radius: 10;");

        TabPane outputTabs = new TabPane();
        outputTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab summaryTab = new Tab("📋 Summary Table", summaryTable);
        Tab umlTab = new Tab("🗂 UML Diagram", umlDiagramPane);

        outputTabs.getTabs().addAll(summaryTab, umlTab);
        outputTabs.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");

        VBox centerBox = new VBox(20, outputTabs);
        centerBox.setPadding(new Insets(20));
        centerBox.setStyle(
            "-fx-background-color: #f5f6fa; -fx-background-radius: 16; -fx-border-radius: 16; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);"
        );

        setTop(titleLabel);
        setLeft(inputBox);
        setCenter(centerBox);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #dfe6e9, #b2bec3);");
    }

//    private void analyzeCode() {
//        String code = codeInput.getText();
//        AnalysisResult result = CodeAnalyzer.analyze(code);
//        summaryTable.setItems(result.getSummaryItems());
//
//        // UML Code Generation
//        List<ClassInfo> classList = UMLCodeAnalyzer.analyze(code);
//        String plantUmlCode = PlantUMLGenerator.generate(classList);
//        umlCodeArea.setText(plantUmlCode);
//    }
    private void analyzeCode() {
        String code = codeInput.getText();
        AnalysisResult result = CodeAnalyzer.analyze(code);
        summaryTable.setItems(result.getSummaryItems());

        List<ClassInfo> classList = UMLCodeAnalyzer.analyze(code);

        // Show PlantUML code (optional)
        String plantUmlCode = PlantUMLGenerator.generate(classList);
        umlCodeArea.setText(plantUmlCode);

        // Show UML diagram image
        umlDiagramPane.getChildren().setAll(
            UMLDiagramGenerator.generateDiagramNode(classList)
        );
    }

}
