package application;

import application.model.JobFair;
import application.model.Student;

import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/jobfair_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            String createJobFairTable = 
                "CREATE TABLE IF NOT EXISTS job_fairs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status VARCHAR(50) DEFAULT 'ACTIVE'" +
                ")";

            String createStudentsTable = 
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "program VARCHAR(255) NOT NULL, " +
                "selected BOOLEAN DEFAULT FALSE, " +
                "job_fair_id INT, " +
                "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (job_fair_id) REFERENCES job_fairs(id)" +
                ")";

            String createRecruitersTable = 
                "CREATE TABLE IF NOT EXISTS recruiters (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "company_name VARCHAR(255) NOT NULL, " +
                "job_fair_id INT, " +
                "added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (job_fair_id) REFERENCES job_fairs(id)" +
                ")";

            Statement stmt = conn.createStatement();
            stmt.execute(createJobFairTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createRecruitersTable);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    // Enhanced Job Fair Methods
    public static List<JobFair> loadAllJobFairs() throws SQLException {
        List<JobFair> jobFairs = new ArrayList<>();
        String sql = "SELECT * FROM job_fairs ORDER BY created_date DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                JobFair jobFair = new JobFair();
                jobFair.setId(rs.getInt("id"));
                jobFair.setTitle(rs.getString("title"));
                jobFair.setCreatedDate(rs.getTimestamp("created_date"));
                jobFair.setStatus(rs.getString("status"));
                jobFairs.add(jobFair);
            }
        }
        return jobFairs;
    }

    public static JobFair getJobFairById(int jobFairId) throws SQLException {
        String sql = "SELECT * FROM job_fairs WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobFairId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                JobFair jobFair = new JobFair();
                jobFair.setId(rs.getInt("id"));
                jobFair.setTitle(rs.getString("title"));
                jobFair.setCreatedDate(rs.getTimestamp("created_date"));
                jobFair.setStatus(rs.getString("status"));
                return jobFair;
            }
        }
        return null;
    }

    public static List<Student> loadStudentsByJobFair(int jobFairId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE job_fair_id = ? ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobFairId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("name"),
                    rs.getInt("id"),
                    rs.getString("program")
                );
                student.selectedProperty().set(rs.getBoolean("selected"));
                students.add(student);
            }
        }
        return students;
    }

    public static List<Map<String, Object>> loadRecruitersByJobFair(int jobFairId) throws SQLException {
        List<Map<String, Object>> recruiters = new ArrayList<>();
        String sql = "SELECT * FROM recruiters WHERE job_fair_id = ? ORDER BY company_name";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobFairId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> recruiter = new HashMap<>();
                recruiter.put("id", rs.getInt("id"));
                recruiter.put("name", rs.getString("name"));
                recruiter.put("companyName", rs.getString("company_name"));
                recruiter.put("jobFairId", rs.getInt("job_fair_id"));
                recruiter.put("addedDate", rs.getTimestamp("added_date"));
                recruiters.add(recruiter);
            }
        }
        return recruiters;
    }

    public static Map<String, Integer> loadJobFairStatistics(int jobFairId) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        
        String studentStatsSQL = "SELECT " +
                               "COUNT(*) as total_students, " +
                               "SUM(CASE WHEN selected = true THEN 1 ELSE 0 END) as selected_students, " +
                               "SUM(CASE WHEN selected = false THEN 1 ELSE 0 END) as pending_students " +
                               "FROM students WHERE job_fair_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(studentStatsSQL)) {
            
            pstmt.setInt(1, jobFairId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.put("totalStudents", rs.getInt("total_students"));
                stats.put("selectedStudents", rs.getInt("selected_students"));
                stats.put("pendingStudents", rs.getInt("pending_students"));
            }
        }
        
        String recruiterCountSQL = "SELECT COUNT(*) as total_recruiters FROM recruiters WHERE job_fair_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(recruiterCountSQL)) {
            
            pstmt.setInt(1, jobFairId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.put("totalRecruiters", rs.getInt("total_recruiters"));
            }
        }
        
        return stats;
    }

    // Update job fair status
    public static boolean updateJobFairStatus(int jobFairId, String status) throws SQLException {
        String sql = "UPDATE job_fairs SET status = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, jobFairId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
