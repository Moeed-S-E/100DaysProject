package crimedec.app.services;

import crimedec.app.entity.Evidence;
import crimedec.app.repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final EvidenceRepository evidenceRepository;

    public List<Evidence> getEvidenceByCaseId(Integer caseId) { return evidenceRepository.findByCaseId(caseId); }
    public Evidence getEvidenceById(Integer id) { return evidenceRepository.findById(id).orElse(null); }
    public Evidence addEvidence(Evidence e) { return evidenceRepository.save(e); }
    public void deleteEvidence(Integer id) { evidenceRepository.deleteById(id); }
    public Evidence updateEvidence(Evidence e) { return evidenceRepository.save(e); }
}
