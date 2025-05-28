package application.controller;

import application.DatabaseConnection;
import application.constants.UIConstants;
import application.model.Admin;
import application.model.JobFair;
import application.model.Student;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JobFairController {
    private final Admin admin;
    private final ObservableList<Student> studentsList;
    private final StatusUpdater statusUpdater;
    private int currentJobFairId = -1;
    private String currentJobFairTitle = "";
    
    public interface StatusUpdater {
        void updateStatus(String message);
    }
    
    public JobFairController(Admin admin, ObservableList<Student> studentsList, StatusUpdater statusUpdater) {
        this.admin = admin;
        this.studentsList = studentsList;
        this.statusUpdater = statusUpdater;
    }
    
    public int createJobFair(String title) {
        try {
            currentJobFairId = admin.createJobFair(title);
            currentJobFairTitle = title;
            return currentJobFairId;
        } catch (IllegalArgumentException ex) {
            updateStatus("❌ " + ex.getMessage());
            return -1;
        }
    }
    
    public void loadJobFairData(int jobFairId) {
        try {
            // Load job fair info
            JobFair jobFair = DatabaseConnection.getJobFairById(jobFairId);
            if (jobFair == null) {
                updateStatus("❌ Job fair not found!");
                return;
            }
            
            // Set current job fair
            currentJobFairId = jobFairId;
            currentJobFairTitle = jobFair.getTitle();
            
            // Load students for this job fair
            List<Student> students = DatabaseConnection.loadStudentsByJobFair(jobFairId);
            studentsList.clear();
            studentsList.addAll(students);
            
            updateStatus("✅ Loaded job fair: " + jobFair.getTitle() + " with " + students.size() + " students");
            
        } catch (SQLException e) {
            updateStatus("❌ Error loading job fair data: " + e.getMessage());
        }
    }
    
    public void deleteJobFair(int jobFairId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete in order due to foreign key constraints
            String deleteStudents = "DELETE FROM students WHERE job_fair_id = ?";
            String deleteRecruiters = "DELETE FROM recruiters WHERE job_fair_id = ?";
            String deleteJobFair = "DELETE FROM job_fairs WHERE id = ?";
            
            PreparedStatement pstmt1 = conn.prepareStatement(deleteStudents);
            pstmt1.setInt(1, jobFairId);
            pstmt1.executeUpdate();
            
            PreparedStatement pstmt2 = conn.prepareStatement(deleteRecruiters);
            pstmt2.setInt(1, jobFairId);
            pstmt2.executeUpdate();
            
            PreparedStatement pstmt3 = conn.prepareStatement(deleteJobFair);
            pstmt3.setInt(1, jobFairId);
            int result = pstmt3.executeUpdate();
            
            if (result > 0) {
                updateStatus("✅ Job fair deleted successfully");
                
                // Clear current data if deleted job fair was active
                if (currentJobFairId == jobFairId) {
                    currentJobFairId = -1;
                    currentJobFairTitle = "";
                    studentsList.clear();
                }
            } else {
                updateStatus("❌ Failed to delete job fair");
            }
            
        } catch (SQLException e) {
            updateStatus("❌ Error deleting job fair: " + e.getMessage());
        }
    }
    
    public void registerStudent(TextField nameField, TextField idField, TextField programField) {
        try {
            if (currentJobFairId == -1) {
                updateStatus("⚠️ Please create or load a job fair first!");
                return;
            }
            
            String name = nameField.getText().trim();
            String idText = idField.getText().trim();
            String program = programField.getText().trim();
            
            if (name.isEmpty() || idText.isEmpty() || program.isEmpty()) {
                updateStatus("⚠️ All fields are required!");
                return;
            }
            
            int id = Integer.parseInt(idText);
            Student student = new Student(name, id, program);
            student.applyToJobFair(currentJobFairId);
            
            updateStatus("✅ Student " + student.getName() + " registered successfully!");
            nameField.clear();
            idField.clear();
            programField.clear();
            refreshStudentsTable();
            
        } catch (NumberFormatException ex) {
            updateStatus("❌ Please enter a valid numeric student ID!");
        } catch (SQLException ex) {
            updateStatus("❌ Database error: " + ex.getMessage());
        }
    }
    
    public void addRecruiter(TextField nameField, TextField companyField) {
        try {
            if (currentJobFairId == -1) {
                updateStatus("⚠️ Please create or load a job fair first!");
                return;
            }
            
            String name = nameField.getText().trim();
            String company = companyField.getText().trim();
            
            if (name.isEmpty() || company.isEmpty()) {
                updateStatus("⚠️ All fields are required!");
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO recruiters (name, company_name, job_fair_id) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, company);
                pstmt.setInt(3, currentJobFairId);
                
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    updateStatus("✅ Recruiter " + name + " from " + company + " added successfully!");
                    nameField.clear();
                    companyField.clear();
                } else {
                    updateStatus("❌ Failed to add recruiter!");
                }
            }
        } catch (SQLException ex) {
            updateStatus("❌ Database error: " + ex.getMessage());
        }
    }
    
    public void showStudentSelectionDialog() {
        if (currentJobFairId == -1) {
            updateStatus("⚠️ Please create or load a job fair first!");
            return;
        }
        
        if (studentsList.isEmpty()) {
            updateStatus("⚠️ No students available for selection!");
            return;
        }
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Select Students");
        dialog.setHeaderText("Choose students to mark as selected for: " + currentJobFairTitle);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        for (Student student : studentsList) {
            CheckBox checkBox = new CheckBox(student.getName() + " (" + student.getId() + ") - " + student.getProgram());
            checkBox.setSelected(student.selectedProperty().get());
            checkBox.setUserData(student);
            content.getChildren().add(checkBox);
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setPrefSize(500, 300);
        scrollPane.setFitToWidth(true);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                updateStudentSelections(content);
            }
        });
    }
    
    private void updateStudentSelections(VBox content) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE students SET selected = ? WHERE id = ? AND job_fair_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            int updatedCount = 0;
            for (Node node : content.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) node;
                    Student student = (Student) checkBox.getUserData();
                    
                    pstmt.setBoolean(1, checkBox.isSelected());
                    pstmt.setInt(2, student.getId());
                    pstmt.setInt(3, currentJobFairId);
                    
                    if (pstmt.executeUpdate() > 0) {
                        student.selectedProperty().set(checkBox.isSelected());
                        updatedCount++;
                    }
                }
            }
            
            updateStatus("✅ Updated selection status for " + updatedCount + " students!");
            refreshStudentsTable();
            
        } catch (SQLException ex) {
            updateStatus("❌ Database error: " + ex.getMessage());
        }
    }
    
    public void refreshStudentsTable() {
        updateStatus("⏳ Loading students...");
        studentsList.clear();
        
        if (currentJobFairId != -1) {
            try {
                List<Student> students = DatabaseConnection.loadStudentsByJobFair(currentJobFairId);
                studentsList.addAll(students);
                updateStatus("✅ Students loaded successfully!");
            } catch (SQLException e) {
                updateStatus("❌ Error loading students: " + e.getMessage());
            }
        } else {
            updateStatus("⚠️ No job fair selected!");
        }
    }
    
    public void updateChartData(PieChart pieChart, VBox statsBox) {
        int selectedCount = 0;
        int pendingCount = 0;
        
        if (currentJobFairId != -1) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT selected, COUNT(*) as count FROM students WHERE job_fair_id = ? GROUP BY selected";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, currentJobFairId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    if (rs.getBoolean("selected")) {
                        selectedCount = rs.getInt("count");
                    } else {
                        pendingCount = rs.getInt("count");
                    }
                }
            } catch (SQLException e) {
                updateStatus("❌ Error loading analytics: " + e.getMessage());
                return;
            }
        }

        updatePieChart(pieChart, selectedCount, pendingCount);
        updateStatsBox(statsBox, selectedCount, pendingCount);
    }
    
    private void updatePieChart(PieChart pieChart, int selectedCount, int pendingCount) {
        pieChart.getData().clear();
        
        if (selectedCount > 0 || pendingCount > 0) {
            PieChart.Data selectedData = new PieChart.Data("Selected (" + selectedCount + ")", selectedCount);
            PieChart.Data pendingData = new PieChart.Data("Pending (" + pendingCount + ")", pendingCount);
            
            pieChart.getData().addAll(selectedData, pendingData);
            
            Platform.runLater(() -> {
                selectedData.getNode().setStyle("-fx-pie-color: " + UIConstants.MEDIUM_GREEN + ";");
                pendingData.getNode().setStyle("-fx-pie-color: " + UIConstants.LIGHT_GREEN + ";");
            });
            
            updateStatus("📊 Analytics updated: " + selectedCount + " selected, " + pendingCount + " pending");
        } else {
            updateStatus("📊 No data available for current job fair");
        }
    }
    
    private void updateStatsBox(VBox statsBox, int selectedCount, int pendingCount) {
        int total = selectedCount + pendingCount;
        
        if (statsBox.getChildren().size() >= 4) {
            ((Label) statsBox.getChildren().get(1)).setText("📊 Total Students: " + total);
            ((Label) statsBox.getChildren().get(2)).setText("✅ Selected: " + selectedCount);
            ((Label) statsBox.getChildren().get(3)).setText("⏳ Pending: " + pendingCount);
            
            if (total > 0) {
                double selectionRate = (selectedCount * 100.0) / total;
                Label rateLabel = new Label(String.format("📈 Selection Rate: %.1f%%", selectionRate));
                rateLabel.setTextFill(Color.web(UIConstants.MEDIUM_GREEN));
                
                if (statsBox.getChildren().size() > 4) {
                    statsBox.getChildren().set(4, rateLabel);
                } else {
                    statsBox.getChildren().add(rateLabel);
                }
            }
        }
    }
    
    public void updateStatus(String message) {
        statusUpdater.updateStatus(message);
    }
    
    public int getCurrentJobFairId() {
        return currentJobFairId;
    }
    
    public String getCurrentJobFairTitle() {
        return currentJobFairTitle;
    }
}
