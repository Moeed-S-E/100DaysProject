package application;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    public static void show(DataBaseManager studentDB, DataBaseManager teacherDB, DataBaseManager courseDB, DataBaseManager attendanceDB) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        try {
            // Export Students
            FileWriter writer = new FileWriter(fileChooser.showSaveDialog(stage));
            writer.write("Students\n");
            writer.write("ID,Name,Age,Class\n");
            List<String> studentRecords = studentDB.readRecords();
            for (String record : studentRecords) {
                writer.write(record + "\n");
            }

            // Export Teachers
            writer.write("\nTeachers\n");
            writer.write("ID,Name,Subject\n");
            List<String> teacherRecords = teacherDB.readRecords();
            for (String record : teacherRecords) {
                writer.write(record + "\n");
            }

            // Export Courses
            writer.write("\nCourses\n");
            writer.write("ID,Name\n");
            List<String> courseRecords = courseDB.readRecords();
            for (String record : courseRecords) {
                writer.write(record + "\n");
            }

            // Export Attendance
            writer.write("\nAttendance\n");
            writer.write("Date,StudentID,Present\n");
            List<String> attendanceRecords = attendanceDB.readRecords();
            for (String record : attendanceRecords) {
                writer.write(record + "\n");
            }

            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "CSV exported successfully!");
            alert.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error exporting CSV!");
            alert.show();
        }
    }
}