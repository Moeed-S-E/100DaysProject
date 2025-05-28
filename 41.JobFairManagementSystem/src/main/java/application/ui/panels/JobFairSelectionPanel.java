package application.ui.panels;

import application.DatabaseConnection;
import application.constants.UIConstants;
import application.controller.JobFairController;
import application.model.JobFair;
import application.ui.UIComponentFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JobFairSelectionPanel extends BasePanel {
    private final JobFairController controller;
    private TableView<JobFair> jobFairTable;
    private ObservableList<JobFair> jobFairsList = FXCollections.observableArrayList();
    private Label selectedJobFairLabel;
    private VBox statsContainer;
    private Label statusLabel;
    
    public JobFairSelectionPanel(JobFairController controller) {
        this.controller = controller;
    }
    
    @Override
    public VBox createPanel() {
        VBox panel = createGlassPanel();
        Label titleLabel = createSectionTitle("📂 Load Data From Previous Job Fair");

        // Status label for this panel
        statusLabel = new Label("Initializing...");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        statusLabel.setTextFill(Color.web(UIConstants.MEDIUM_GREEN));

        // Current Job Fair Info
        selectedJobFairLabel = new Label("Current: No job fair selected");
        selectedJobFairLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        selectedJobFairLabel.setTextFill(Color.web(UIConstants.MEDIUM_GREEN));
        
        // Control buttons
        HBox controls = createControlButtons();
        
        // Job Fair Table
        jobFairTable = createJobFairTable();
        
        // Statistics container
        statsContainer = createStatsContainer();
        
        panel.getChildren().addAll(
            titleLabel,
            statusLabel,
            selectedJobFairLabel,
            controls,
            new Label("📋 Available Job Fairs:"),
            jobFairTable,
            statsContainer
        );
        
        
        return panel;
    }
     
    private void updateLocalStatus(String message) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText(message);
            System.out.println("[JobFairSelectionPanel] " + message);
        });
    }
    
    private HBox createControlButtons() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        
        Button refreshButton = UIComponentFactory.createModernButton("🔄 Refresh List", UIConstants.MEDIUM_GREEN);
        Button loadButton = UIComponentFactory.createModernButton("📥 Load Selected Fair", UIConstants.LIGHT_GREEN);
        Button deleteButton = UIComponentFactory.createModernButton("🗑️ Delete Fair", UIConstants.ERROR_RED);
        
        refreshButton.setOnAction(e -> {
            updateLocalStatus("🔄 Refreshing job fairs...");
            loadJobFairs();
        });
        
        loadButton.setOnAction(e -> loadSelectedJobFair());
        deleteButton.setOnAction(e -> deleteSelectedJobFair());
        
        controls.getChildren().addAll(refreshButton, loadButton, deleteButton);
        return controls;
    }
    
    @SuppressWarnings("unchecked")
    private TableView<JobFair> createJobFairTable() {
        TableView<JobFair> table = new TableView<>();
        table.setItems(jobFairsList);
        table.setPrefHeight(300);
        
        TableColumn<JobFair, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<JobFair, String> titleCol = new TableColumn<>("📋 Job Fair Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(300);
        
        TableColumn<JobFair, String> dateCol = new TableColumn<>("📅 Created Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        dateCol.setPrefWidth(180);
        
        TableColumn<JobFair, String> statusCol = new TableColumn<>("📊 Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        // Add selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateLocalStatus("📊 Loading details for: " + newSelection.getTitle());
                showJobFairDetails(newSelection);
            }
        });
        
        table.getColumns().addAll(idCol, titleCol, dateCol, statusCol);
        
        table.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + UIConstants.MEDIUM_GREEN + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 2;");
        
        return table;
    }
    
    private VBox createStatsContainer() {
        VBox container = new VBox(10);
        container.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-border-color: " + UIConstants.LIGHT_GREEN + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 2;");
        
        Label statsTitle = new Label("📊 Job Fair Details");
        statsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        statsTitle.setTextFill(Color.web(UIConstants.DARK_GREEN));
        
        Label noSelectionLabel = new Label("Select a job fair to view details");
        noSelectionLabel.setTextFill(Color.web("#666666"));
        
        container.getChildren().addAll(statsTitle, noSelectionLabel);
        return container;
    }
    
    private void loadJobFairs() {
        try {
            updateLocalStatus("📥 Loading job fairs from database...");
            
            List<JobFair> jobFairs = DatabaseConnection.loadAllJobFairs();
            
            javafx.application.Platform.runLater(() -> {
                jobFairsList.clear();
                jobFairsList.addAll(jobFairs);
                
                updateLocalStatus("✅ Loaded " + jobFairs.size() + " job fairs from database");
                controller.updateStatus("✅ Loaded " + jobFairs.size() + " job fairs from database");
                
                if (jobFairs.isEmpty()) {
                    updateLocalStatus("ℹ️ No job fairs found. Create one first in the Admin Panel.");
                }
            });
            
        } catch (SQLException e) {
            String errorMsg = "❌ Error loading job fairs: " + e.getMessage();
            updateLocalStatus(errorMsg);
            controller.updateStatus(errorMsg);
            e.printStackTrace();
        }
    }
    
    private void showJobFairDetails(JobFair jobFair) {
        try {
            Map<String, Integer> stats = DatabaseConnection.loadJobFairStatistics(jobFair.getId());
            @SuppressWarnings("unused")
			List<Map<String, Object>> recruiters = DatabaseConnection.loadRecruitersByJobFair(jobFair.getId());
            
            javafx.application.Platform.runLater(() -> {
                // Clear existing stats (except title)
                if (statsContainer.getChildren().size() > 1) {
                    statsContainer.getChildren().subList(1, statsContainer.getChildren().size()).clear();
                }
                
                // Add new stats
                Label titleLabel = new Label("📋 Title: " + jobFair.getTitle());
                Label dateLabel = new Label("📅 Created: " + jobFair.getFormattedDate());
                Label statusLabel = new Label("📊 Status: " + jobFair.getStatus());
                Label studentsLabel = new Label("👥 Total Students: " + stats.getOrDefault("totalStudents", 0));
                Label selectedLabel = new Label("✅ Selected: " + stats.getOrDefault("selectedStudents", 0));
                Label pendingLabel = new Label("⏳ Pending: " + stats.getOrDefault("pendingStudents", 0));
                Label recruitersLabel = new Label("🏢 Recruiters: " + stats.getOrDefault("totalRecruiters", 0));
                
                statsContainer.getChildren().addAll(
                    titleLabel, dateLabel, statusLabel, studentsLabel, 
                    selectedLabel, pendingLabel, recruitersLabel
                );
                
                // Calculate selection rate
                int total = stats.getOrDefault("totalStudents", 0);
                int selected = stats.getOrDefault("selectedStudents", 0);
                if (total > 0) {
                    double rate = (selected * 100.0) / total;
                    Label rateLabel = new Label(String.format("📈 Selection Rate: %.1f%%", rate));
                    rateLabel.setTextFill(Color.web(UIConstants.MEDIUM_GREEN));
                    statsContainer.getChildren().add(rateLabel);
                }
                
                updateLocalStatus("✅ Details loaded for: " + jobFair.getTitle());
            });
            
        } catch (SQLException e) {
            String errorMsg = "❌ Error loading job fair details: " + e.getMessage();
            updateLocalStatus(errorMsg);
            controller.updateStatus(errorMsg);
            e.printStackTrace();
        }
    }
    
    private void loadSelectedJobFair() {
        JobFair selected = jobFairTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            updateLocalStatus("⚠️ Please select a job fair to load");
            controller.updateStatus("⚠️ Please select a job fair to load");
            return;
        }
        
        // Confirm loading
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Load Job Fair");
        confirmAlert.setHeaderText("Load Job Fair Data");
        confirmAlert.setContentText("Are you sure you want to load '" + selected.getTitle() + "'?\n" +
                                   "This will replace current data with the selected job fair's data.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                updateLocalStatus("📥 Loading job fair data...");
                controller.loadJobFairData(selected.getId());
                selectedJobFairLabel.setText("Current: " + selected.getTitle() + " (ID: " + selected.getId() + ")");
                updateLocalStatus("✅ Successfully loaded: " + selected.getTitle());
                controller.updateStatus("✅ Successfully loaded job fair: " + selected.getTitle());
            }
        });
    }
    
    private void deleteSelectedJobFair() {
        JobFair selected = jobFairTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            updateLocalStatus("⚠️ Please select a job fair to delete");
            controller.updateStatus("⚠️ Please select a job fair to delete");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.WARNING);
        confirmAlert.setTitle("Delete Job Fair");
        confirmAlert.setHeaderText("Delete Job Fair");
        confirmAlert.setContentText("Are you sure you want to delete '" + selected.getTitle() + "'?\n" +
                                   "This action cannot be undone!");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                updateLocalStatus("🗑️ Deleting job fair...");
                controller.deleteJobFair(selected.getId());
                loadJobFairs(); // Refresh the list
                updateLocalStatus("✅ Job fair deleted successfully");
            }
        });
    }
    
    public void updateCurrentJobFairLabel(String jobFairTitle, int jobFairId) {
        javafx.application.Platform.runLater(() -> {
            selectedJobFairLabel.setText("Current: " + jobFairTitle + " (ID: " + jobFairId + ")");
        });
    }
}
