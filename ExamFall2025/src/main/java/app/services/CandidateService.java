package app.services;

import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.dtos.SkillStatDTO;
import app.dtos.TopCandidatePopularityDTO;
import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.enums.Category;
import app.exceptions.ApiException;
import app.mapper.CandidateMapper;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateDAO candidateDAO;
    private final SkillDAO skillDAO;
    private final ServiceAPI statsApi = new ServiceAPI();

    public CandidateService(EntityManagerFactory emf) {
        this.candidateDAO = new CandidateDAO(emf);
        this.skillDAO = new SkillDAO(emf);
    }

    /**
     * Create a candidate and optionally link existing skills.
     * If a skillId does not exist, it will be skipped (no exception).
     */
    public CandidateDTO create(CandidateCreateDTO dto) {
        Candidate candidate = Candidate.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .education(dto.getEducation())
                .build();

        Candidate saved = candidateDAO.create(candidate);

        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            for (Long skillId : dto.getSkillIds()) {
                // validate skill exists before linking
                if (skillId == null) continue;
                if (skillDAO.getById(skillId) == null) continue;
                candidateDAO.addSkill(saved.getId(), skillId);
            }
            Candidate reloaded = candidateDAO.findByIdWithSkills(saved.getId());
            if (reloaded != null) saved = reloaded;
        }

        return CandidateMapper.toDto(saved);
    }

    /**
     * Return all candidates (no filter).
     */
    public Set<CandidateDTO> getAll() {
        Set<Candidate> candidates = candidateDAO.getAll();
        if (candidates == null || candidates.isEmpty()) return Collections.emptySet();

        return candidates.stream()
                .map(CandidateMapper::toDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Filtered getAll by Category. Pass null to behave like unfiltered getAll.
     * Uses DAO-side filtering for efficiency when category is non-null.
     */
    public Set<CandidateDTO> getAll(Category categoryFilter) {
        if (categoryFilter == null) {
            return getAll();
        }

        Set<Candidate> candidates;
        try {
            candidates = candidateDAO.getAllBySkillCategory(categoryFilter);
        } catch (Exception ex) {
            candidates = candidateDAO.getAll();
            candidates = filterByCategory(candidates, categoryFilter);
        }

        if (candidates == null || candidates.isEmpty()) return Collections.emptySet();

        return candidates.stream()
                .map(CandidateMapper::toDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Get single candidate with skills loaded. Returns null when not found.
     */
    public CandidateDTO getById(Long id) {
        Candidate c = candidateDAO.findByIdWithSkills(id);
        return c == null ? null : CandidateMapper.toDto(c);
    }

    /**
     * Update candidate scalar fields. Skills should be modified via linkSkill/removeSkill.
     * Returns updated DTO or null if update failed or candidate not found.
     */
    public CandidateDTO update(Long id, CandidateCreateDTO dto) {
        Candidate candidateToUpdate = CandidateMapper.toEntity(dto);
        candidateToUpdate.setId(id);
        Candidate updated = candidateDAO.update(candidateToUpdate);
        if (updated == null) return null;
        Candidate reloaded = candidateDAO.findByIdWithSkills(updated.getId());
        return CandidateMapper.toDto(reloaded != null ? reloaded : updated);
    }

    /**
     * Delete candidate. Throws ApiException 404 if not found.
     */
    public boolean delete(Long id) {
        Candidate existing = candidateDAO.findByIdWithSkills(id);
        if (existing == null) {
            throw new ApiException(404, "Candidate not found");
        }
        candidateDAO.delete(id);
        return true;
    }

    /**
     * Link a skill to a candidate. Validates that skill exists first.
     * Returns updated candidate DTO or null if candidate or skill not found.
     */
    public CandidateDTO linkSkill(Long candidateId, Long skillId) {
        if (candidateId == null || skillId == null) return null;
        if (skillDAO.getById(skillId) == null) return null;
        Candidate updated = candidateDAO.addSkill(candidateId, skillId);
        if (updated == null) return null;
        Candidate reloaded = candidateDAO.findByIdWithSkills(updated.getId());
        return CandidateMapper.toDto(reloaded != null ? reloaded : updated);
    }

    /**
     * Remove a linked skill from a candidate. Returns updated candidate DTO or null if candidate not found.
     */
    public CandidateDTO removeSkill(Long candidateId, Long skillId) {
        if (candidateId == null || skillId == null) return null;
        Candidate updated = candidateDAO.removeSkill(candidateId, skillId);
        if (updated == null) return null;
        Candidate reloaded = candidateDAO.findByIdWithSkills(updated.getId());
        return CandidateMapper.toDto(reloaded != null ? reloaded : updated);
    }

    /**
     * Internal helper: filter candidates by Category using streams. Returns an empty set on null inputs.
     */
    private Set<Candidate> filterByCategory(Set<Candidate> source, Category category) {
        if (source == null || source.isEmpty() || category == null) return Collections.emptySet();

        return source.stream()
                .filter(Objects::nonNull)
                .filter(c -> {
                    Set<CandidateSkill> cs = c.getCandidateSkills();
                    if (cs == null) return false;
                    return cs.stream()
                            .anyMatch(x -> x.getSkill() != null && category.equals(x.getSkill().getCategory()));
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Return the candidate with the highest average popularity score.
     * Returns null when no candidate has any scored skills.
     */
    public TopCandidatePopularityDTO getTopCandidateByAveragePopularity() {
        var logger = org.slf4j.LoggerFactory.getLogger(CandidateService.class);

        Set<Candidate> allCandidates = candidateDAO.getAll();
        if (allCandidates == null || allCandidates.isEmpty()) {
            logger.info("No candidates found when computing top popularity");
            return null;
        }

        // collect unique slugs from all candidates
        Set<String> allSlugs = allCandidates.stream()
                .filter(Objects::nonNull)
                .flatMap(c -> {
                    Set<CandidateSkill> cs = c.getCandidateSkills();
                    if (cs == null) return Stream.empty();
                    return cs.stream()
                            .map(CandidateSkill::getSkill)
                            .filter(Objects::nonNull)
                            .map(s -> s.getSlug())
                            .filter(Objects::nonNull);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        logger.info("Collected {} unique slugs for popularity lookup", allSlugs.size());
        if (allSlugs.isEmpty()) {
            logger.info("No skill slugs available on candidates; cannot compute popularity");
            return null;
        }

        Map<String, SkillStatDTO> stats = statsApi.fetchStatsForSlugs(allSlugs);
        if (stats == null || stats.isEmpty()) {
            logger.info("Skill stats provider returned no data for slugs: {}", allSlugs);
            return null;
        }
        logger.info("Fetched {} skill stats from external provider", stats.size());

        Long bestId = null;
        double bestAvg = Double.NEGATIVE_INFINITY;

        for (Candidate c : allCandidates) {
            if (c == null) continue;
            Set<CandidateSkill> skills = c.getCandidateSkills();
            if (skills == null || skills.isEmpty()) continue;

            // build list of available popularity scores for this candidate
            List<Integer> popularityScores = skills.stream()
                    .map(CandidateSkill::getSkill)
                    .filter(Objects::nonNull)
                    .map(s -> s.getSlug())
                    .filter(Objects::nonNull)
                    .map(stats::get) // may return null
                    .filter(Objects::nonNull)
                    .map(SkillStatDTO::getPopularityScore)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (popularityScores.isEmpty()) continue;

            double avg = popularityScores.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
            if (!Double.isFinite(avg)) continue;

            logger.debug("Candidate id={} avgPopularity={}", c.getId(), avg);

            if (avg > bestAvg) {
                bestAvg = avg;
                bestId = c.getId();
            }
        }

        if (bestId == null) {
            logger.info("No candidate had any popularity scores available after filtering");
            return null;
        }

        logger.info("Top candidate by average popularity: id={}, avg={}", bestId, bestAvg);
        return new TopCandidatePopularityDTO(bestId, bestAvg);
    }

}
