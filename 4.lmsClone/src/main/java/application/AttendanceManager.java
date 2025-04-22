package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Map;

public class AttendanceManager {
    private static Attendance attendance = new Attendance();
    private static DataBaseManager dbManager;

    public static void show(DataBaseManager db) {
        dbManager = db;
        attendance.loadFromCSV(dbManager);

        Stage stage = new Stage();
        stage.setTitle("Mark Attendance");

        // Input Fields
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        CheckBox presentCheckBox = new CheckBox("Present");

        // Buttons
        Button markButton = new Button("Mark Attendance");
        Button viewButton = new Button("View Attendance");

        // Mark Button Action
        markButton.setOnAction(e -> {
            try {
                String studentId = studentIdField.getText();
                LocalDate date = datePicker.getValue();
                boolean present = presentCheckBox.isSelected();

                attendance.markAttendance(studentId, date, present);
                attendance.saveToCSV(dbManager);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance marked successfully!");
                alert.show();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input! " + ex.getMessage());
                alert.show();
            }
        });

        // View Button Action
        viewButton.setOnAction(e -> {
            Stage viewStage = new Stage();
            viewStage.setTitle("Attendance Records");

            VBox viewLayout = new VBox(10);
            for (Map.Entry<LocalDate, Map<String, Boolean>> entry : attendance.getAttendance().entrySet()) {
                LocalDate date = entry.getKey();
                Map<String, Boolean> records = entry.getValue();
                for (Map.Entry<String, Boolean> record : records.entrySet()) {
                    Label label = new Label("Date: " + date + ", Student ID: " + record.getKey() + ", Present: " + record.getValue());
                    viewLayout.getChildren().add(label);
                }
            }

            Scene viewScene = new Scene(viewLayout, 400, 400);
            viewStage.setScene(viewScene);
            viewStage.show();
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(presentCheckBox, 1, 2);
        grid.add(markButton, 0, 3);
        grid.add(viewButton, 1, 3);

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}