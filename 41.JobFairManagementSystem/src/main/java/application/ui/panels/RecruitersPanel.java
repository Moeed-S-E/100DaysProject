package application.ui.panels;

import application.constants.UIConstants;
import application.controller.JobFairController;
import application.ui.UIComponentFactory;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class RecruitersPanel extends BasePanel {
    private final JobFairController controller;
    
    public RecruitersPanel(JobFairController controller) {
        this.controller = controller;
    }
    
    @Override
    public VBox createPanel() {
        VBox panel = createGlassPanel();
        Label titleLabel = createSectionTitle("🏢 Recruiter Actions");

        GridPane form = createRecruiterForm();
        Button addRecruiterButton = createAddRecruiterButton(form);
        Button selectStudentsButton = createSelectStudentsButton();
        
        VBox controls = new VBox(15);
        controls.getChildren().addAll(form, addRecruiterButton, selectStudentsButton);
        
        panel.getChildren().addAll(titleLabel, controls);
        return panel;
    }
    
    private GridPane createRecruiterForm() {
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        TextField recruiterName = UIComponentFactory.createModernTextField("Recruiter Name", "👤");
        TextField companyName = UIComponentFactory.createModernTextField("Company Name", "🏢");

        Tooltip.install(recruiterName, new Tooltip("Enter the recruiter's full name"));
        Tooltip.install(companyName, new Tooltip("Enter the company name"));

        form.add(new Label("Recruiter Name:"), 0, 0);
        form.add(recruiterName, 1, 0);
        form.add(new Label("Company:"), 0, 1);
        form.add(companyName, 1, 1);
        
        form.setUserData(new TextField[]{recruiterName, companyName});
        
        return form;
    }
    
    private Button createAddRecruiterButton(GridPane form) {
        Button addRecruiterButton = UIComponentFactory.createModernButton("➕ Add Recruiter", UIConstants.MEDIUM_GREEN);
        Tooltip.install(addRecruiterButton, new Tooltip("Add a new recruiter to the current job fair"));
        
        addRecruiterButton.setOnAction(e -> {
            TextField[] fields = (TextField[]) form.getUserData();
            controller.addRecruiter(fields[0], fields[1]);
        });
        
        return addRecruiterButton;
    }
    
    private Button createSelectStudentsButton() {
        Button selectStudentsButton = UIComponentFactory.createModernButton("✅ Select Students", UIConstants.LIGHT_GREEN);
        Tooltip.install(selectStudentsButton, new Tooltip("Mark selected students for recruitment"));
        
        selectStudentsButton.setOnAction(e -> {
            controller.showStudentSelectionDialog();
        });
        
        return selectStudentsButton;
    }
}
