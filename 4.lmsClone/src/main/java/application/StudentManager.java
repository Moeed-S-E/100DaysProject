package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class StudentManager {
    private static ArrayList<Student> studentList = new ArrayList<>();
    private static DataBaseManager dbManager;

    public static void show(DataBaseManager db) {
        dbManager = db;
        loadStudentsFromCSV();

        Stage stage = new Stage();
        stage.setTitle("Manage Students");

        // Input Fields
        TextField idField = new TextField();
        idField.setPromptText("Student ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        TextField classField = new TextField();
        classField.setPromptText("Class");

        // Buttons
        Button addButton = new Button("Add Student");
        Button viewButton = new Button("View Students");

        // Add Button Action
        addButton.setOnAction(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String className = classField.getText();

                Student student = new Student(id, name, age, className);
                studentList.add(student);

                // Save to CSV
                String record = student.getId() + "," + student.getName() + "," + student.getAge() + "," + student.getClassName();
                dbManager.saveRecord(record);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student added successfully!");
                alert.show();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input! Please try again.");
                alert.show();
            }
        });

        // View Button Action
        viewButton.setOnAction(e -> {
            Stage viewStage = new Stage();
            viewStage.setTitle("Student List");

            VBox viewLayout = new VBox(10);
            for (Student student : studentList) {
                Label studentLabel = new Label(student.toString());
                viewLayout.getChildren().add(studentLabel);
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
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);
        grid.add(new Label("Class:"), 0, 3);
        grid.add(classField, 1, 3);
        grid.add(addButton, 0, 4);
        grid.add(viewButton, 1, 4);

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private static void loadStudentsFromCSV() {
        studentList.clear();
        ArrayList<String> records = (ArrayList<String>) dbManager.readRecords();
        for (String record : records) {
            String[] data = record.split(",");
            if (data.length == 4) {
                try {
                    String id = data[0];
                    String name = data[1];
                    int age = Integer.parseInt(data[2]);
                    String className = data[3];
                    Student student = new Student(id, name, age, className);
                    studentList.add(student);
                } catch (Exception e) {
                    System.out.println("Error parsing student record: " + record);
                }
            }
        }
    }
}