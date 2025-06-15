package crimedec.app.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import crimedec.app.dao.EvidenceDAO;
import crimedec.app.entity.Evidence;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EvidenceDAOImpl implements EvidenceDAO {
    private final Connection conn;

    @Override
    public List<Evidence> findByCaseId(Integer caseId) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM evidence WHERE case_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                evidenceList.add(Evidence.builder()
                        .evidenceId(rs.getInt("evidence_id"))
                        .caseId(rs.getInt("case_id"))
                        .description(rs.getString("description"))
                        .type(rs.getString("type"))
                        .dateCollected(rs.getDate("date_collected"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evidenceList;
    }

    @Override
    public Evidence findById(Integer evidenceId) {
        String sql = "SELECT * FROM evidence WHERE evidence_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evidenceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Evidence.builder()
                        .evidenceId(rs.getInt("evidence_id"))
                        .caseId(rs.getInt("case_id"))
                        .description(rs.getString("description"))
                        .type(rs.getString("type"))
                        .dateCollected(rs.getDate("date_collected"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Evidence evidence) {
        String sql = "INSERT INTO evidence (case_id, description, type, date_collected) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evidence.getCaseId());
            ps.setString(2, evidence.getDescription());
            ps.setString(3, evidence.getType());
            ps.setDate(4, new java.sql.Date(evidence.getDateCollected().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Evidence evidence) {
        String sql = "UPDATE evidence SET case_id = ?, description = ?, type = ?, date_collected = ? WHERE evidence_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evidence.getCaseId());
            ps.setString(2, evidence.getDescription());
            ps.setString(3, evidence.getType());
            ps.setDate(4, new java.sql.Date(evidence.getDateCollected().getTime()));
            ps.setInt(5, evidence.getEvidenceId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer evidenceId) {
        String sql = "DELETE FROM evidence WHERE evidence_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evidenceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
