package crimedec.app.dao;

import java.util.List;

import crimedec.app.entity.Case;

public interface CaseDAO {
    List<Case> findAll();
    Case findById(Integer caseId);
    void save(Case c);
    void update(Case c);
    void delete(Integer caseId);
}