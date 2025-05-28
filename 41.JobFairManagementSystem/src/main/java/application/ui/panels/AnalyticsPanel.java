package application.ui.panels;

import application.constants.UIConstants;
import application.controller.JobFairController;
import application.ui.UIComponentFactory;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AnalyticsPanel extends BasePanel {
    private final JobFairController controller;
    private PieChart pieChart;
    private VBox statsBox;
    
    public AnalyticsPanel(JobFairController controller) {
        this.controller = controller;
    }
    
    @Override
    public VBox createPanel() {
        VBox panel = createGlassPanel();
        Label titleLabel = createSectionTitle("📊 Analytics Dashboard");

        Button refreshButton = UIComponentFactory.createModernButton("🔄 Refresh Data", UIConstants.MEDIUM_GREEN);
        pieChart = createPieChart();
        statsBox = createStatsBox();
        
        setupRefreshButton(refreshButton);
        
        HBox chartAndStats = new HBox(20);
        chartAndStats.getChildren().addAll(pieChart, statsBox);
        chartAndStats.setAlignment(Pos.CENTER);
        
        panel.getChildren().addAll(titleLabel, refreshButton, chartAndStats);
        
        updateAnalytics();
        
        return panel;
    }
    
    private PieChart createPieChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Student Selection Status");
        chart.setPrefSize(UIConstants.CHART_WIDTH, UIConstants.CHART_HEIGHT);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setLegendSide(Side.BOTTOM);
        chart.setLabelsVisible(true);
        return chart;
    }
    
    private VBox createStatsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 10; -fx-padding: 15;");
        
        Label statsTitle = new Label("📈 Quick Statistics");
        statsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, UIConstants.SUBTITLE_FONT_SIZE));
        statsTitle.setTextFill(Color.web(UIConstants.DARK_GREEN));
        
        Label totalLabel = new Label("Total Students: Calculating...");
        Label selectedLabel = new Label("Selected: Calculating...");
        Label pendingLabel = new Label("Pending: Calculating...");
        
        statsBox.getChildren().addAll(statsTitle, totalLabel, selectedLabel, pendingLabel);
        return statsBox;
    }
    
    private void setupRefreshButton(Button refreshButton) {
        refreshButton.setOnAction(e -> updateAnalytics());
    }
    
    public void updateAnalytics() {
        controller.updateChartData(pieChart, statsBox);
    }
}
