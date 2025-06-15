package crimedec.app.dao;
import java.util.List;

import crimedec.app.entity.Evidence;

public interface EvidenceDAO {
    List<Evidence> findByCaseId(Integer caseId);
    Evidence findById(Integer evidenceId);
    void save(Evidence evidence);
    void update(Evidence evidence);
    void delete(Integer evidenceId);
}