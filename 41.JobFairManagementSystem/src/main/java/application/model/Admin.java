package application.model;

import application.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {
    private String name;
    private int adminId;

    public Admin(String name, int adminId) {
        this.name = name;
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public int getAdminId() {
        return adminId;
    }

    public int createJobFair(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job fair title cannot be empty");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO job_fairs (title) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, title.trim());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create job fair: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("Admin{name='%s', adminId=%d}", name, adminId);
    }
}
