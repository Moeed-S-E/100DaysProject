package crimedec.app.repository;

import crimedec.app.entity.Suspect;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuspectRepository extends JpaRepository<Suspect, Integer> {
    List<Suspect> findByCaseId(Integer caseId);
}
