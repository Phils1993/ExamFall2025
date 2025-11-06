package app.controllers;

import app.daos.CandidateDAO;
import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.dtos.CandidatePopularityReportDTO;
import app.dtos.SkillEnrichedDTO;
import app.enums.Category;
import app.entities.Candidate;
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
            List<Candidate> all = candidateDAO.getAll();
            Set<CandidateDTO> dtos = all.stream()
                    .map(CandidateDTO::new)
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

    public Handler getTopByPopularity() {
        return ctx -> {
            // 1. Hent alle slugs fra dine lokale skills
            List<Candidate> candidates = candidateDAO.getAll();
            Set<String> slugs = candidates.stream()
                    .flatMap(c -> c.getCandidateSkills().stream())
                    .map(cs -> cs.getSkill().getSlug())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 2. Fetch enriched data fra ekstern API
            ServiceAPI api = new ServiceAPI();
            Map<String, SkillEnrichedDTO> enrichedMap = api.fetchStatsForSlugs(slugs);

            // 3. Konverter til liste og send til DAO
            List<SkillEnrichedDTO> enrichedList = new ArrayList<>(enrichedMap.values());
            CandidatePopularityReportDTO best = candidateDAO.getTopCandidateByAveragePopularity(enrichedList);

            ctx.status(HttpStatus.OK).json(best);
        };
    }



}
