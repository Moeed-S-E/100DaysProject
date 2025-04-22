package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TeacherManager {
    private static ArrayList<Teacher> teacherList = new ArrayList<>();
    private static DataBaseManager dbManager;

    public static void show(DataBaseManager db) {
        dbManager = db;
        loadTeachersFromCSV();

        Stage stage = new Stage();
        stage.setTitle("Manage Teachers");

        // Input Fields
        TextField idField = new TextField();
        idField.setPromptText("Teacher ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");

        // Buttons
        Button addButton = new Button("Add Teacher");
        Button viewButton = new Button("View Teachers");

        // Add Button Action
        addButton.setOnAction(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                String subject = subjectField.getText();

                Teacher teacher = new Teacher(id, name, subject);
                teacherList.add(teacher);

                // Save to CSV
                String record = teacher.getId() + "," + teacher.getName() + "," + teacher.getSubject();
                dbManager.saveRecord(record);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Teacher added successfully!");
                alert.show();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input! Please try again.");
                alert.show();
            }
        });

        // View Button Action
        viewButton.setOnAction(e -> {
            Stage viewStage = new Stage();
            viewStage.setTitle("Teacher List");

            VBox viewLayout = new VBox(10);
            for (Teacher teacher : teacherList) {
                Label teacherLabel = new Label(teacher.toString());
                viewLayout.getChildren().add(teacherLabel);
            }

            Scene viewScene = new Scene(viewLayout, 300, 400);
            viewStage.setScene(viewScene);
            viewStage.show();
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Subject:"), 0, 2);
        grid.add(subjectField, 1, 2);
        grid.add(addButton, 0, 3);
        grid.add(viewButton, 1, 3);

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private static void loadTeachersFromCSV() {
        teacherList.clear();
        ArrayList<String> records = (ArrayList<String>) dbManager.readRecords();
        for (String record : records) {
            String[] data = record.split(",");
            if (data.length == 3) {
                try {
                    String id = data[0];
                    String name = data[1];
                    String subject = data[2];
                    Teacher teacher = new Teacher(id, name, subject);
                    teacherList.add(teacher);
                } catch (Exception e) {
                    System.out.println("Error parsing teacher record: " + record);
                }
            }
        }
    }
}