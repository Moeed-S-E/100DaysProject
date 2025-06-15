package crimedec.app.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import crimedec.app.dao.SuspectDAO;
import crimedec.app.entity.Suspect;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspectDAOImpl implements SuspectDAO {
    private final Connection conn;

    @Override
    public List<Suspect> findByCaseId(Integer caseId) {
        List<Suspect> suspects = new ArrayList<>();
        String sql = "SELECT * FROM suspects WHERE case_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                suspects.add(Suspect.builder()
                        .suspectId(rs.getInt("suspect_id"))
                        .caseId(rs.getInt("case_id"))
                        .name(rs.getString("name"))
                        .age(rs.getInt("age"))
                        .gender(rs.getString("gender"))
                        .notes(rs.getString("notes"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suspects;
    }

    @Override
    public Suspect findById(Integer suspectId) {
        String sql = "SELECT * FROM suspects WHERE suspect_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, suspectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Suspect.builder()
                        .suspectId(rs.getInt("suspect_id"))
                        .caseId(rs.getInt("case_id"))
                        .name(rs.getString("name"))
                        .age(rs.getInt("age"))
                        .gender(rs.getString("gender"))
                        .notes(rs.getString("notes"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Suspect suspect) {
        String sql = "INSERT INTO suspects (case_id, name, age, gender, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, suspect.getCaseId());
            ps.setString(2, suspect.getName());
            ps.setInt(3, suspect.getAge());
            ps.setString(4, suspect.getGender());
            ps.setString(5, suspect.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Suspect suspect) {
        String sql = "UPDATE suspects SET case_id = ?, name = ?, age = ?, gender = ?, notes = ? WHERE suspect_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, suspect.getCaseId());
            ps.setString(2, suspect.getName());
            ps.setInt(3, suspect.getAge());
            ps.setString(4, suspect.getGender());
            ps.setString(5, suspect.getNotes());
            ps.setInt(6, suspect.getSuspectId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer suspectId) {
        String sql = "DELETE FROM suspects WHERE suspect_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, suspectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}