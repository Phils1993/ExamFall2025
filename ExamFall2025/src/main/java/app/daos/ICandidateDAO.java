package app.daos;

import app.entities.Candidate;

import java.util.Optional;

public interface ICandidateDAO extends IDAO<Candidate> {
    Candidate findByIdWithSkills(Long id);
    Candidate addSkill(Long candidateId, Long skillId);
    Candidate removeSkill(Long candidateId, Long skillId);
}
