package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.database.DatabaseConnection;
import app.entity.IssuedBook;

public class IssuedBookDAO {
    public List<IssuedBook> getAllIssuedBooks() throws SQLException {
        List<IssuedBook> issuedBooks = new ArrayList<>();
        String query = "SELECT * FROM IssuedBooks";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                issuedBooks.add(new IssuedBook(
                        rs.getInt("IssueID"),
                        rs.getInt("BookID"),
                        rs.getInt("MemberID"),
                        rs.getDate("IssueDate") != null ? rs.getDate("IssueDate").toLocalDate() : null,
                        rs.getDate("ReturnDate") != null ? rs.getDate("ReturnDate").toLocalDate() : null
                ));
            }
        }
        return issuedBooks;
    }

    public void issueBook(IssuedBook issuedBook) throws SQLException {
        String query = "INSERT INTO IssuedBooks (BookID, MemberID, IssueDate, ReturnDate) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, issuedBook.getBookID());
            stmt.setInt(2, issuedBook.getMemberID());
            stmt.setDate(3, issuedBook.getIssueDate() != null ? Date.valueOf(issuedBook.getIssueDate()) : null);
            stmt.setDate(4, issuedBook.getReturnDate() != null ? Date.valueOf(issuedBook.getReturnDate()) : null);
            stmt.executeUpdate();
        }
    }
}