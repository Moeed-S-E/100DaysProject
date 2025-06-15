package crimedec.app.controller;

import crimedec.app.entity.Evidence;
import crimedec.app.services.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evidence")
@CrossOrigin(origins = "http://localhost:5500")
@RequiredArgsConstructor
public class EvidenceController {
    private final EvidenceService evidenceService;

    @GetMapping("/case/{caseId}")
    public List<Evidence> getEvidenceByCaseId(@PathVariable Integer caseId) {
        return evidenceService.getEvidenceByCaseId(caseId);
    }

    @GetMapping("/{id}")
    public Evidence getEvidence(@PathVariable Integer id) {
        return evidenceService.getEvidenceById(id);
    }

    @PostMapping
    public Evidence addEvidence(@RequestBody Evidence e) {
        return evidenceService.addEvidence(e);
    }

    @PutMapping("/{id}")
    public Evidence updateEvidence(@PathVariable Integer id, @RequestBody Evidence e) {
        e.setEvidenceId(id);
        return evidenceService.updateEvidence(e);
    }

    @DeleteMapping("/{id}")
    public void deleteEvidence(@PathVariable Integer id) {
        evidenceService.deleteEvidence(id);
    }
}
