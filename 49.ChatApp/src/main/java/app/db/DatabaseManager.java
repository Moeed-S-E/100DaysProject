package app.db;
import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String USER = "root";
    private static final String PASS = "____________YOUR__PASSWORD________";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static boolean registerUser(String username, String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        
        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt);
            
            return stmt.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String generateSalt() {
        // Implement secure salt generation
        return "SALT";
    }

    private static String hashPassword(String password, String salt) {
        // Implement proper hashing (e.g., SHA-256)
        return password + salt; // Simplified for example
    }
}