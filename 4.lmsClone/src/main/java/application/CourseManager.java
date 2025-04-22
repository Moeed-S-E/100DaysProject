package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CourseManager {
    private static ArrayList<Course> courseList = new ArrayList<>();
    private static DataBaseManager dbManager;

    public static void show(DataBaseManager db) {
        dbManager = db;
        loadCoursesFromCSV();

        Stage stage = new Stage();
        stage.setTitle("Manage Courses");

        // Input Fields
        TextField idField = new TextField();
        idField.setPromptText("Course ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        // Buttons
        Button addButton = new Button("Add Course");
        Button viewButton = new Button("View Courses");

        // Add Button Action
        addButton.setOnAction(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();

                Course course = new Course(id, name);
                courseList.add(course);

                // Save to CSV
                String record = course.getId() + "," + course.getName();
                dbManager.saveRecord(record);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Course added successfully!");
                alert.show();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input! Please try again.");
                alert.show();
            }
        });

        // View Button Action
        viewButton.setOnAction(e -> {
            Stage viewStage = new Stage();
            viewStage.setTitle("Course List");

            VBox viewLayout = new VBox(10);
            for (Course course : courseList) {
                Label courseLabel = new Label(course.toString());
                viewLayout.getChildren().add(courseLabel);
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
        grid.add(addButton, 0, 2);
        grid.add(viewButton, 1, 2);

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private static void loadCoursesFromCSV() {
        courseList.clear();
        ArrayList<String> records = (ArrayList<String>) dbManager.readRecords();
        for (String record : records) {
            String[] data = record.split(",");
            if (data.length == 2) {
                try {
                    String id = data[0];
                    String name = data[1];
                    Course course = new Course(id, name);
                    courseList.add(course);
                } catch (Exception e) {
                    System.out.println("Error parsing course record: " + record);
                }
            }
        }
    }
}