package app.controllers;

import app.daos.CandidateDAO;
import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.dtos.CandidatePopularityReportDTO;
import app.dtos.SkillEnrichedDTO;
import app.entities.Candidate;
import app.enums.Category;
import app.exceptions.ApiException;
import app.exceptions.EntityNotFoundException;
import app.mapper.CandidateMapper;
import app.services.ServiceAPI;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CandidateController implements IController {

    private final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateDAO candidateDAO;
    private final CandidateMapper candidateMapper = new CandidateMapper();

    public CandidateController(CandidateDAO candidateDAO) {
        this.candidateDAO = candidateDAO;
    }

    @Override
    public Handler create() {
        return ctx -> {
            try {
                CandidateCreateDTO dto = ctx.bodyAsClass(CandidateCreateDTO.class);
                Candidate candidate = candidateMapper.fromCreateDto(dto);
                Candidate created = candidateDAO.create(candidate);

                dto.getSkillIds().forEach(skillId -> candidateDAO.linkSkill(created.getId(), skillId));
                CandidateDTO response = new CandidateDTO(candidateDAO.getById(created.getId()));
                ctx.status(HttpStatus.CREATED).json(response);
            } catch (Exception e) {
                logger.error("Failed to create candidate", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to create candidate");
            }
        };
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            try {
                String categoryParam = ctx.queryParam("category");
                List<Candidate> candidates;

                if (categoryParam != null && !categoryParam.isBlank()) {
                    try {
                        Category cat = Category.valueOf(categoryParam.trim().toUpperCase());
                        candidates = candidateDAO.getAllBySkillCategory(cat);
                    } catch (IllegalArgumentException ex) {
                        logger.error("Invalid category: {}", categoryParam, ex);
                        ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                                "error", "Invalid category",
                                "message", "Unknown category: " + categoryParam
                        ));
                        return;
                    }
                } else {
                    candidates = candidateDAO.getAll();
                }

                ServiceAPI apiService = new ServiceAPI();
                List<String> allSlugs = candidates.stream()
                        .flatMap(c -> c.getCandidateSkills().stream())
                        .map(cs -> cs.getSkill().getSlug())
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

                Map<String, SkillEnrichedDTO> enrichedMap = apiService.fetchSkillFromExternalApi(allSlugs);

                Set<CandidateDTO> dtos = candidates.stream()
                        .map(c -> new CandidateDTO(c, enrichedMap))
                        .collect(Collectors.toSet());

                ctx.status(HttpStatus.OK).json(dtos);
            } catch (Exception e) {
                logger.error("Failed to retrieve candidates", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to retrieve candidates");
            }
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            try {
                Integer id = (Integer) Integer.parseInt(ctx.pathParam("id"));
                Candidate candidate = candidateDAO.getById(id);
                ctx.status(HttpStatus.OK).json(new CandidateDTO(candidate));
            } catch (ApiException e) {
                logger.error("Candidate not found", e);
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
            } catch (Exception e) {
                logger.error("Failed to retrieve candidate by ID", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to retrieve candidate");
            }
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            try {
                Integer id = (Integer) Integer.parseInt(ctx.pathParam("id"));
                CandidateCreateDTO dto = ctx.bodyAsClass(CandidateCreateDTO.class);
                Candidate candidate = candidateMapper.fromCreateDto(dto);
                candidate.setId(id);

                Candidate updated = candidateDAO.update(candidate);
                ctx.status(HttpStatus.OK).json(new CandidateDTO(updated));
            } catch (Exception e) {
                logger.error("Failed to update candidate", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to update candidate");
            }
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            try {
                Integer id = (Integer) Integer.parseInt(ctx.pathParam("id"));
                boolean removed = candidateDAO.delete(id);
                if (!removed) {
                    ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
                    return;
                }
                ctx.status(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                logger.error("Failed to delete candidate", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to delete candidate");
            }
        };
    }

    public Handler removeSkill() {
        return ctx -> {
            try {
                Integer candidateId = (Integer) Integer.parseInt(ctx.pathParam("candidateId"));
                Integer skillId = (Integer) Integer.parseInt(ctx.pathParam("skillId"));
                Candidate updated = candidateDAO.removeSkill(candidateId, skillId);
                if (updated == null) {
                    ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Candidate or Skill not found"));
                    return;
                }
                ctx.status(HttpStatus.OK).json(new CandidateDTO(updated));
            } catch (Exception e) {
                logger.error("Failed to remove skill from candidate", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to remove skill");
            }
        };
    }

    public Handler linkSkill() {
        return ctx -> {
            try {
                Integer candidateId = (Integer) Integer.parseInt(ctx.pathParam("candidateId"));
                Integer skillId = (Integer) Integer.parseInt(ctx.pathParam("skillId"));
                Candidate updated = candidateDAO.linkSkill(candidateId, skillId);
                if (updated == null) {
                    ctx.status(HttpStatus.NOT_FOUND).json("Candidate or Skill not found");
                    return;
                }
                ctx.status(HttpStatus.OK).json(new CandidateDTO(updated));
            } catch (Exception e) {
                logger.error("Failed to link skill to candidate", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to link skill");
            }
        };
    }

    public Handler getTopCandidateByPopularity() {
        return ctx -> {
            try {
                ServiceAPI apiService = new ServiceAPI();

                List<String> allSlugs = candidateDAO.getAll().stream()
                        .flatMap(c -> c.getCandidateSkills().stream())
                        .map(cs -> cs.getSkill().getSlug())
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

                Map<String, SkillEnrichedDTO> enrichedMap = apiService.fetchSkillFromExternalApi(allSlugs);
                Candidate topCandidate = candidateDAO.getTopCandidateEntityByAveragePopularity(enrichedMap);

                if (topCandidate == null) {
                    throw new EntityNotFoundException("No candidate with enriched skills was found");
                }

                List<SkillEnrichedDTO> matched = topCandidate.getCandidateSkills().stream()
                        .map(cs -> enrichedMap.get(cs.getSkill().getSlug()))
                        .filter(Objects::nonNull)
                        .toList();

                double avg = matched.stream()
                        .mapToInt(SkillEnrichedDTO::getPopularityScore)
                        .average()
                        .orElse(0);

                CandidatePopularityReportDTO dto = CandidatePopularityReportDTO.builder()
                        .id(topCandidate.getId())
                        .averagePopularity(Double.valueOf(Math.round(avg * 10.0) / 10.0))
                        .build();

                ctx.json(dto).status(200);
            } catch (EntityNotFoundException e) {
                logger.error("No top candidate found", e);
                ctx.status(HttpStatus.NOT_FOUND).json("No candidate with enriched skills was found");
            } catch (Exception e) {
                logger.error("Failed to calculate top candidate by popularity", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to calculate top candidate");
            }
        };
    }


}
