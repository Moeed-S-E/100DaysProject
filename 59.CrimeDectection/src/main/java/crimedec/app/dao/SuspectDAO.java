package crimedec.app.dao;

import java.util.List;

import crimedec.app.entity.Suspect;

public interface SuspectDAO {
    List<Suspect> findByCaseId(Integer caseId);
    Suspect findById(Integer suspectId);
    void save(Suspect suspect);
    void update(Suspect suspect);
    void delete(Integer suspectId);
}