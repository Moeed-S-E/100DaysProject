package crimedec.app.dao.impl;

import java.sql.*;
import java.util.*;

import crimedec.app.dao.CaseDAO;
import crimedec.app.entity.Case;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CaseDAOImpl implements CaseDAO {
    private final Connection conn;

    @Override
    public List<Case> findAll() {
        List<Case> cases = new ArrayList<>();
        String sql = "SELECT * FROM cases";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cases.add(Case.builder()
                        .caseId(rs.getInt("case_id"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .status(rs.getString("status"))
                        .dateReported(rs.getDate("date_reported"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }

    @Override
    public Case findById(Integer caseId) {
        String sql = "SELECT * FROM cases WHERE case_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, caseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Case.builder()
                            .caseId(rs.getInt("case_id"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .status(rs.getString("status"))
                            .dateReported(rs.getDate("date_reported"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Case c) {
        String sql = "INSERT INTO cases (title, description, status, date_reported) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getStatus());
            ps.setDate(4, new java.sql.Date(c.getDateReported().getTime()));
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    c.setCaseId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Case c) {
        String sql = "UPDATE cases SET title = ?, description = ?, status = ?, date_reported = ? WHERE case_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getStatus());
            ps.setDate(4, new java.sql.Date(c.getDateReported().getTime()));
            ps.setInt(5, c.getCaseId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer caseId) {
        String sql = "DELETE FROM cases WHERE case_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, caseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
