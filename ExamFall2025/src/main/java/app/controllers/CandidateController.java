package app.controllers;

import app.daos.CandidateDAO;
import app.dtos.*;
import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.enums.Category;
import app.exceptions.ApiException;
import app.exceptions.EntityNotFoundException;
import app.mapper.CandidateMapper;
import app.services.ServiceAPI;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
            CandidateCreateDTO dto = ctx.bodyAsClass(CandidateCreateDTO.class);
            Candidate candidate = candidateMapper.fromCreateDto(dto);
            Candidate created = candidateDAO.create(candidate);

            dto.getSkillIds().forEach(skillId -> candidateDAO.linkSkill(created.getId(), skillId));
            CandidateDTO response = new CandidateDTO(candidateDAO.getById(created.getId()));
            ctx.status(HttpStatus.CREATED).json(response);
        };
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            String categoryParam = ctx.queryParam("category");
            List<Candidate> candidates;

            if (categoryParam != null && !categoryParam.isBlank()) {
                try {
                    Category cat = Category.valueOf(categoryParam.trim().toUpperCase());
                    candidates = candidateDAO.getAllBySkillCategory(cat);
                } catch (IllegalArgumentException ex) {
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
        };
    }


    /*

    @Override
    public Handler getAll() {
        return ctx -> {
            List<Candidate> all = candidateDAO.getAll();
            ServiceAPI apiService = new ServiceAPI();

            // Hent alle slugs fra alle kandidaters skills
            List<String> allSlugs = all.stream()
                    .flatMap(c -> c.getCandidateSkills().stream())
                    .map(cs -> cs.getSkill().getSlug())
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            // Hent enrichment fra ekstern API
            Map<String, SkillEnrichedDTO> enrichedMap = apiService.fetchSkillFromExternalApi(allSlugs);

            // Byg DTO'er med enrichment
            Set<CandidateDTO> dtos = all.stream()
                    .map(c -> new CandidateDTO(c, enrichedMap))
                    .collect(Collectors.toSet());

            ctx.status(HttpStatus.OK).json(dtos);
        };
    }


    public Handler getAllCandidates() {
        return ctx -> {
            String categoryParam = ctx.queryParam("category");
            if (categoryParam == null || categoryParam.isBlank()) {
                getAll().handle(ctx);
                return;
            }

            try {
                Category cat = Category.valueOf(categoryParam.trim().toUpperCase());
                List<Candidate> filtered = candidateDAO.getAllBySkillCategory(cat);
                Set<CandidateDTO> dtos = filtered.stream()
                        .map(CandidateDTO::new)
                        .collect(Collectors.toSet());
                ctx.status(HttpStatus.OK).json(dtos);
            } catch (IllegalArgumentException ex) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                        "error", "Invalid category",
                        "message", "Unknown category: " + categoryParam
                ));
            }
        };
    }

     */

    @Override
    public Handler getById() {
        return ctx -> {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            Candidate candidate = candidateDAO.getById(id);
            if (candidate == null) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
                return;
            }
            ctx.status(HttpStatus.OK).json(new CandidateDTO(candidate));
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            CandidateCreateDTO dto = ctx.bodyAsClass(CandidateCreateDTO.class);
            Candidate candidate = candidateMapper.fromCreateDto(dto);
            candidate.setId(id);

            Candidate updated = candidateDAO.update(candidate);
            ctx.status(HttpStatus.OK).json(new CandidateDTO(updated));
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            boolean removed = candidateDAO.delete(id);
            if (!removed) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
                return;
            }
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }

    public Handler removeSkill() {
        return ctx -> {
            Integer candidateId = Integer.parseInt(ctx.pathParam("candidateId"));
            Integer skillId = Integer.parseInt(ctx.pathParam("skillId"));
            Candidate updated = candidateDAO.removeSkill(candidateId, skillId);
            if (updated == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Candidate or Skill not found"));
                return;
            }
            ctx.status(HttpStatus.OK).json(new CandidateDTO(updated));
        };
    }

    public Handler linkSkill() {
        return ctx -> {
            Integer candidateId = Integer.parseInt(ctx.pathParam("candidateId"));
            Integer skillId = Integer.parseInt(ctx.pathParam("skillId"));
            Candidate updated = candidateDAO.linkSkill(candidateId, skillId);
            if (updated == null) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate or Skill not found");
                return;
            }
            ctx.status(HttpStatus.OK).json(new CandidateDTO(updated));
        };
    }

    public Handler getTopCandidateByPopularity() {
        return ctx -> {
            ServiceAPI apiService = new ServiceAPI();

            // Hent alle slugs fra databasen
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

            // Beregn gennemsnit igen for DTO
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
                    .averagePopularity(Math.round(avg * 10.0) / 10.0)
                    .build();

            ctx.json(dto).status(200);
        };
    }


}
