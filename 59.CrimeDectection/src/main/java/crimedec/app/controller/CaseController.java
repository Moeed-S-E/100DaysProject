package crimedec.app.controller;

import crimedec.app.entity.Case;
import crimedec.app.services.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final CaseService caseService;

    @GetMapping
    public List<Case> getAllCases() { return caseService.getAllCases(); }

    @GetMapping("/{id}")
    public Case getCase(@PathVariable Integer id) { return caseService.getCaseById(id); }

    @PostMapping
    public Case addCase(@RequestBody Case c) { return caseService.addCase(c); }

    @PutMapping("/{id}")
    public Case updateCase(@PathVariable Integer id, @RequestBody Case c) {
        c.setCaseId(id);
        return caseService.updateCase(c);
    }

    @DeleteMapping("/{id}")
    public void deleteCase(@PathVariable Integer id) { caseService.deleteCase(id); }
}
