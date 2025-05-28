package application.model;

import application.DatabaseConnection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Student {
    private String name;
    private int id;
    private String program;
    private BooleanProperty selected;

    public Student(String name, int id, String program) {
        this.name = name;
        this.id = id;
        this.program = program;
        this.selected = new SimpleBooleanProperty(false);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public String getStatus() {
        return selected.get() ? "✅ Selected" : "⏳ Pending";
    }

    public void applyToJobFair(int jobFairId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO students (id, name, program, selected, job_fair_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.id);
            pstmt.setString(2, this.name);
            pstmt.setString(3, this.program);
            pstmt.setBoolean(4, this.selected.get());
            pstmt.setInt(5, jobFairId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public String toString() {
        return String.format("Student{name='%s', id=%d, program='%s', selected=%s}", 
                           name, id, program, selected.get());
    }
}
