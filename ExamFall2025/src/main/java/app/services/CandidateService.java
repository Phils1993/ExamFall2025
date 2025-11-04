package app.services;

import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;
import app.enums.Category;
import app.exceptions.ApiException;
import app.mapper.CandidateMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CandidateService {
    private final CandidateDAO candidateDAO;
    private final SkillDAO skillDAO;


    public CandidateService(EntityManagerFactory emf) {
        this.candidateDAO = new CandidateDAO(emf);
        this.skillDAO = new SkillDAO(emf);
    }

    public CandidateDTO create(CandidateCreateDTO dto) {
        // Create entity -> persist -> map to DTO
        Candidate candidate = Candidate.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .education(dto.getEducation())
                .build();

        Candidate saved = candidateDAO.create(candidate);
        // If skillIds provided, link them (single tx per link)
        if (dto.getSkillIds() != null) {
            for (Long skillId : dto.getSkillIds()) {
                candidateDAO.addSkill(saved.getId(), skillId);
            }
            saved = candidateDAO.findByIdWithSkills(saved.getId()).orElse(saved);
        }

        return CandidateMapper.toDto(saved);
    }

    public Set<CandidateDTO> getAll(Optional<Category> categoryFilter) {
        // load all domain entities (inherited from AbstractDAO#getAll)
        Set<Candidate> all = candidateDAO.getAll();

        // if a category filter is present, apply a null-safe filter; otherwise keep all
        Set<Candidate> filtered = categoryFilter
                .map(cat -> filterByCategory(all, cat))
                .orElse(all);

        // map domain entities to DTOs preserving insertion order uniqueness
        return filtered.stream()
                .map(CandidateMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public CandidateDTO getById(Long id) {
        return candidateDAO.findByIdWithSkills(id)
                .map(CandidateMapper::toDto)
                .orElse(null);
    }

    public CandidateDTO update(Long id, CandidateCreateDTO dto) {
        Candidate candidateToUpdate = CandidateMapper.toEntity(dto); // maps scalar fields and optionally skill ids -> skill stubs
        candidateToUpdate.setId(id);
        Candidate updated = candidateDAO.update(candidateToUpdate);
        return CandidateMapper.toDto(updated);
    }

    public boolean delete(Long id) {
        // check existence; use a DAO method that returns Optional
        boolean exists = candidateDAO.findByIdWithSkills(id).isPresent();
        if (!exists) {
            throw new ApiException(404, "Candidate not found");
        }

        candidateDAO.delete(id);
        return exists;
    }

    public CandidateDTO linkSkill(Long candidateId, Long skillId) {
        Candidate updated = candidateDAO.addSkill(candidateId, skillId);
        return updated == null ? null : CandidateMapper.toDto(updated);
    }

    private Set<Candidate> filterByCategory(Set<Candidate> source, Category category) {
        return source.stream()
                .filter(c -> {
                    if (c.getCandidateSkills() == null) return false;
                    return c.getCandidateSkills().stream()
                            .anyMatch(cs -> cs.getSkill() != null && category.equals(cs.getSkill().getCategory()));
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
