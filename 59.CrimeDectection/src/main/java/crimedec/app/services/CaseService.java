package crimedec.app.services;

import crimedec.app.entity.Case;
import crimedec.app.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseService {
    private final CaseRepository caseRepository;

    public List<Case> getAllCases() { return caseRepository.findAll(); }
    public Case getCaseById(Integer id) { return caseRepository.findById(id).orElse(null); }
    public Case addCase(Case c) { return caseRepository.save(c); }
    public void deleteCase(Integer id) { caseRepository.deleteById(id); }
    public Case updateCase(Case c) { return caseRepository.save(c); }
}
