package application.ui.panels;

import application.constants.UIConstants;
import application.controller.JobFairController;
import application.model.Student;
import application.ui.UIComponentFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class StudentsPanel extends BasePanel {
    private final JobFairController controller;
    private final ObservableList<Student> studentsList;
    private TableView<Student> studentsTable;
    
    public StudentsPanel(JobFairController controller, ObservableList<Student> studentsList) {
        this.controller = controller;
        this.studentsList = studentsList;
    }
    
    @Override
    public VBox createPanel() {
        VBox panel = createGlassPanel();
        Label titleLabel = createSectionTitle("👨‍🎓 Student Registration & Management");

        GridPane form = createStudentForm();
        Button registerButton = createRegisterButton(form);
        studentsTable = createStudentsTable();

        panel.getChildren().addAll(titleLabel, form, registerButton, 
                                 new Label("📋 Registered Students:"), studentsTable);
        return panel;
    }
    
    private GridPane createStudentForm() {
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        TextField studentName = UIComponentFactory.createModernTextField("Student Name", "👤");
        TextField studentId = UIComponentFactory.createModernTextField("Student ID", "🆔");
        TextField program = UIComponentFactory.createModernTextField("Program (e.g., Computer Science)", "📚");

        Tooltip.install(studentName, new Tooltip("Enter the student's full name"));
        Tooltip.install(studentId, new Tooltip("Enter a unique numeric student ID"));
        Tooltip.install(program, new Tooltip("Enter the student's academic program"));

        form.add(new Label("Name:"), 0, 0);
        form.add(studentName, 1, 0);
        form.add(new Label("ID:"), 0, 1);
        form.add(studentId, 1, 1);
        form.add(new Label("Program:"), 0, 2);
        form.add(program, 1, 2);
        
        form.setUserData(new TextField[]{studentName, studentId, program});
        
        return form;
    }
    
    private Button createRegisterButton(GridPane form) {
        Button registerButton = UIComponentFactory.createModernButton("📝 Register & Apply", UIConstants.LIGHT_GREEN);
        Tooltip.install(registerButton, new Tooltip("Register a new student and apply to the current job fair"));
        
        registerButton.setOnAction(e -> {
            TextField[] fields = (TextField[]) form.getUserData();
            controller.registerStudent(fields[0], fields[1], fields[2]);
        });
        
        return registerButton;
    }
    
    @SuppressWarnings("unchecked")
    private TableView<Student> createStudentsTable() {
        TableView<Student> table = new TableView<>();
        table.setItems(studentsList);

        TableColumn<Student, String> nameCol = new TableColumn<>("👤 Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);

        TableColumn<Student, Integer> idCol = new TableColumn<>("🆔 ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);

        TableColumn<Student, String> programCol = new TableColumn<>("📚 Program");
        programCol.setCellValueFactory(new PropertyValueFactory<>("program"));
        programCol.setPrefWidth(200);

        TableColumn<Student, String> statusCol = new TableColumn<>("📊 Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(150);

        table.getColumns().addAll(nameCol, idCol, programCol, statusCol);
        table.setPrefHeight(UIConstants.TABLE_HEIGHT);

        table.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + UIConstants.MEDIUM_GREEN + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 2;");
        return table;
    }
    
    public TableView<Student> getStudentsTable() {
        return studentsTable;
    }
}
