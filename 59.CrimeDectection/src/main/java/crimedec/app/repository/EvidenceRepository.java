package crimedec.app.repository;

import crimedec.app.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Integer> {
    List<Evidence> findByCaseId(Integer caseId);
}
