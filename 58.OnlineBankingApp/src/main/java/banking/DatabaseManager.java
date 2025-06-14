package banking;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Update these values for your MySQL setup
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/OnlineBanking?useSSL=false";
    private static final String USER = "";
    private static final String PASSWORD = "";

    public DatabaseManager() {
        createTables();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }

    public void createTables() {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS Users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "password_hash VARCHAR(255) NOT NULL," +
                "account_id INT" +
                ");";

        String createAccountsTableSQL = "CREATE TABLE IF NOT EXISTS Accounts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL UNIQUE," +
                "balance DOUBLE NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES Users(id)" +
                ");";

        String createTransactionsTableSQL = "CREATE TABLE IF NOT EXISTS Transactions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "account_id INT NOT NULL," +
                "type VARCHAR(50) NOT NULL," +
                "amount DOUBLE NOT NULL," +
                "description VARCHAR(255)," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (account_id) REFERENCES Accounts(id)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTableSQL);
            stmt.execute(createAccountsTableSQL);
            stmt.execute(createTransactionsTableSQL);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password, double initialDeposit) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;

        String insertUserSQL = "INSERT INTO Users (username, password_hash) VALUES (?, ?)";
        String insertAccountSQL = "INSERT INTO Accounts (user_id, balance) VALUES (?, ?)";
        String updateUserAccountIDSQL = "UPDATE Users SET account_id = ? WHERE id = ?";
        String recordTransactionSQL = "INSERT INTO Transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    try (PreparedStatement pstmtAccount = conn.prepareStatement(insertAccountSQL, Statement.RETURN_GENERATED_KEYS)) {
                        pstmtAccount.setInt(1, userId);
                        pstmtAccount.setDouble(2, initialDeposit);
                        pstmtAccount.executeUpdate();

                        ResultSet generatedAccountKeys = pstmtAccount.getGeneratedKeys();
                        if (generatedAccountKeys.next()) {
                            int accountId = generatedAccountKeys.getInt(1);

                            try (PreparedStatement pstmtUpdateUser = conn.prepareStatement(updateUserAccountIDSQL)) {
                                pstmtUpdateUser.setInt(1, accountId);
                                pstmtUpdateUser.setInt(2, userId);
                                pstmtUpdateUser.executeUpdate();
                            }
                            try (PreparedStatement pstmtTransaction = conn.prepareStatement(recordTransactionSQL)) {
                                pstmtTransaction.setInt(1, accountId);
                                pstmtTransaction.setString(2, "Deposit");
                                pstmtTransaction.setDouble(3, initialDeposit);
                                pstmtTransaction.setString(4, "Initial deposit during registration");
                                pstmtTransaction.executeUpdate();
                            }
                            conn.commit();
                            return true;
                        }
                    }
                }
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) {}
        }
    }

    public User authenticateUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return null;

        String sql = "SELECT id, username, password_hash, account_id FROM Users WHERE username = ? AND password_hash = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), rs.getInt("account_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    public int getAccountIdForUsername(String username) {
        String sql = "SELECT a.id FROM Accounts a JOIN Users u ON a.user_id = u.id WHERE u.username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Error getting account ID for username: " + e.getMessage());
        }
        return -1;
    }

    public boolean accountExists(int accountId) {
        String sql = "SELECT COUNT(*) FROM Accounts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking account existence: " + e.getMessage());
        }
        return false;
    }

    public double getAccountBalance(int accountId) {
        String sql = "SELECT balance FROM Accounts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            System.err.println("Error getting account balance: " + e.getMessage());
        }
        return 0.0;
    }

    private boolean updateAccountBalance(int accountId, double amount) {
        String sql = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            return false;
        }
    }

    private boolean recordTransaction(int accountId, String type, double amount, String description) {
        String sql = "INSERT INTO Transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
            return false;
        }
    }

    public boolean deposit(int accountId, double amount) {
        if (updateAccountBalance(accountId, amount)) {
            return recordTransaction(accountId, "Deposit", amount, "Cash deposit");
        }
        return false;
    }

    public boolean withdraw(int accountId, double amount) {
        if (getAccountBalance(accountId) < amount) return false;
        if (updateAccountBalance(accountId, -amount)) {
            return recordTransaction(accountId, "Withdrawal", amount, "Cash withdrawal");
        }
        return false;
    }

    public boolean transferFunds(int senderAccountId, int receiverAccountId, double amount) {
        if (getAccountBalance(senderAccountId) < amount) return false;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String debitSQL = "UPDATE Accounts SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(debitSQL)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, senderAccountId);
                if (pstmt.executeUpdate() == 0) { conn.rollback(); return false; }
            }
            String creditSQL = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(creditSQL)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, receiverAccountId);
                if (pstmt.executeUpdate() == 0) { conn.rollback(); return false; }
            }
            String recordTransferSQL = "INSERT INTO Transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmtSender = conn.prepareStatement(recordTransferSQL);
                 PreparedStatement pstmtReceiver = conn.prepareStatement(recordTransferSQL)) {
                pstmtSender.setInt(1, senderAccountId);
                pstmtSender.setString(2, "Transfer_Out");
                pstmtSender.setDouble(3, amount);
                pstmtSender.setString(4, "Transfer to Account " + receiverAccountId);
                pstmtSender.executeUpdate();

                pstmtReceiver.setInt(1, receiverAccountId);
                pstmtReceiver.setString(2, "Transfer_In");
                pstmtReceiver.setDouble(3, amount);
                pstmtReceiver.setString(4, "Transfer from Account " + senderAccountId);
                pstmtReceiver.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) {}
        }
    }

    public List<Transaction> getTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, type, amount, description, timestamp FROM Transactions WHERE account_id = ? ORDER BY timestamp DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("account_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }
}
