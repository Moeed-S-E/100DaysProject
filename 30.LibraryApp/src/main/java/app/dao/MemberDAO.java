package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.database.DatabaseConnection;
import app.entity.Member;

public class MemberDAO {
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM Members";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("MemberID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                ));
            }
        }
        return members;
    }

    public void addMember(Member member) throws SQLException {
        String query = "INSERT INTO Members (Name, Email, Phone) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.executeUpdate();
        }
    }
}