package crimedec.app.services;

import crimedec.app.entity.Suspect;
import crimedec.app.repository.SuspectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuspectService {
    private final SuspectRepository suspectRepository;

    public List<Suspect> getSuspectsByCaseId(Integer caseId) { return suspectRepository.findByCaseId(caseId); }
    public Suspect getSuspectById(Integer id) { return suspectRepository.findById(id).orElse(null); }
    public Suspect addSuspect(Suspect s) { return suspectRepository.save(s); }
    public void deleteSuspect(Integer id) { suspectRepository.deleteById(id); }
    public Suspect updateSuspect(Suspect s) { return suspectRepository.save(s); }
}
