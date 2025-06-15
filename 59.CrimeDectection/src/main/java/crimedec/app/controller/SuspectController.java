package crimedec.app.controller;

import crimedec.app.entity.Suspect;
import crimedec.app.services.SuspectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suspects")
@CrossOrigin(origins = "http://localhost:5500")
@RequiredArgsConstructor
public class SuspectController {
    private final SuspectService suspectService;

    @GetMapping("/case/{caseId}")
    public List<Suspect> getSuspectsByCaseId(@PathVariable Integer caseId) {
        return suspectService.getSuspectsByCaseId(caseId);
    }

    @GetMapping("/{id}")
    public Suspect getSuspect(@PathVariable Integer id) {
        return suspectService.getSuspectById(id);
    }

    @PostMapping
    public Suspect addSuspect(@RequestBody Suspect s) {
        return suspectService.addSuspect(s);
    }

    @PutMapping("/{id}")
    public Suspect updateSuspect(@PathVariable Integer id, @RequestBody Suspect s) {
        s.setSuspectId(id);
        return suspectService.updateSuspect(s);
    }

    @DeleteMapping("/{id}")
    public void deleteSuspect(@PathVariable Integer id) {
        suspectService.deleteSuspect(id);
    }
}
