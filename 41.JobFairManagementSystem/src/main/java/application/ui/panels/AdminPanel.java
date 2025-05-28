package application.ui.panels;

import application.constants.UIConstants;
import application.controller.JobFairController;
import application.ui.UIComponentFactory;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class AdminPanel extends BasePanel {
    private final JobFairController controller;
    
    public AdminPanel(JobFairController controller) {
        this.controller = controller;
    }
    
    @Override
    public VBox createPanel() {
        VBox panel = createGlassPanel();
        Label titleLabel = createSectionTitle("🔧 Create New Job Fair");

        VBox controls = new VBox(15);
        TextField jobFairTitle = UIComponentFactory.createModernTextField("Enter Job Fair Title", "💼");
        Tooltip.install(jobFairTitle, new Tooltip("Enter the title for the new job fair (e.g., Spring 2025 Job Fair)"));
        
        Button createButton = UIComponentFactory.createModernButton("🚀 Create Job Fair", UIConstants.MEDIUM_GREEN);
        Tooltip.install(createButton, new Tooltip("Create a new job fair with the specified title"));
        
        createButton.setOnAction(e -> {
            String title = jobFairTitle.getText().trim();
            if (!title.isEmpty()) {
                if (showConfirmationDialog(title)) {
                    int jobFairId = controller.createJobFair(title);
                    if (jobFairId != -1) {
                        controller.updateStatus("✅ Job Fair '" + title + "' created successfully! ID: " + jobFairId);
                        jobFairTitle.clear();
                        controller.refreshStudentsTable();
                    } else {
                        controller.updateStatus("❌ Failed to create job fair!");
                    }
                }
            } else {
                controller.updateStatus("⚠️ Please enter a job fair title!");
            }
        });

        controls.getChildren().addAll(jobFairTitle, createButton);
        panel.getChildren().addAll(titleLabel, controls);
        return panel;
    }
    
    private boolean showConfirmationDialog(String title) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Job Fair Creation");
        confirmAlert.setHeaderText("Create Job Fair");
        confirmAlert.setContentText("Are you sure you want to create a job fair titled '" + title + "'?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
