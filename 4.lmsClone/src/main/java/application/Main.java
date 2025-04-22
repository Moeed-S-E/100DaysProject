package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private static DataBaseManager studentDB;
    private static DataBaseManager teacherDB;
    private static DataBaseManager courseDB;
    private static DataBaseManager attendanceDB;

    @Override
    public void start(Stage primaryStage) {
        // Initialize separate DataBaseManager instances
        studentDB = new DataBaseManager("src/main/resources/students.csv");
        teacherDB = new DataBaseManager("src/main/resources/teachers.csv");
        courseDB = new DataBaseManager("src/main/resources/courses.csv");
        attendanceDB = new DataBaseManager("src/main/resources/attendance.csv");

        primaryStage.setTitle("School Management System");
        primaryStage.setResizable(false);

        // Main Menu Buttons
        Button manageStudentsBtn = new Button("Manage Students");
        Button manageTeachersBtn = new Button("Manage Teachers");
        Button manageCoursesBtn = new Button("Manage Courses");
        Button attendanceBtn = new Button("Mark Attendance");
        Button exportDataBtn = new Button("Export Data");

        // Set Button Sizes
        manageStudentsBtn.setPrefWidth(200);
        manageStudentsBtn.setPrefHeight(45);
        manageTeachersBtn.setPrefWidth(200);
        manageTeachersBtn.setPrefHeight(45);
        manageCoursesBtn.setPrefWidth(200);
        manageCoursesBtn.setPrefHeight(45);
        attendanceBtn.setPrefWidth(200);
        attendanceBtn.setPrefHeight(45);
        exportDataBtn.setPrefWidth(200);
        exportDataBtn.setPrefHeight(45);

        // Event Handlers for Navigation
        manageStudentsBtn.setOnAction(e -> StudentManager.show(studentDB));
        manageTeachersBtn.setOnAction(e -> TeacherManager.show(teacherDB));
        manageCoursesBtn.setOnAction(e -> CourseManager.show(courseDB));
        attendanceBtn.setOnAction(e -> AttendanceManager.show(attendanceDB));
        exportDataBtn.setOnAction(e -> CSVExporter.show(studentDB, teacherDB, courseDB, attendanceDB));

        // Main Layout
        VBox layout = new VBox(15, manageStudentsBtn, manageTeachersBtn, manageCoursesBtn, attendanceBtn, exportDataBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}