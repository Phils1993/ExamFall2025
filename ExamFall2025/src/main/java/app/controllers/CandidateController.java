package app.controllers;

import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.dtos.TopCandidatePopularityDTO;
import app.enums.Category;
import app.services.CandidateService;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;
import java.util.Set;

public class CandidateController implements IController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    public Handler create() {
        return ctx -> {
            CandidateCreateDTO dto = ctx.bodyAsClass(CandidateCreateDTO.class);
            CandidateDTO created = service.create(dto);
            ctx.status(HttpStatus.CREATED).json(created);
        };
    }

    /**
     * Simple unfiltered GET /candidates
     */
    public Handler getAll() {
        return ctx -> {
            Set<CandidateDTO> set = service.getAll();
            ctx.status(HttpStatus.OK).json(set);
        };
    }

    /**
     * GET /candidates?category={category}
     * If category param is missing, clients should call the simple getAll() handler instead.
     * This handler returns 400 when the category value is invalid.
     */
    public Handler getAllCandidates() {
        return ctx -> {
            String categoryParam = ctx.queryParam("category");
            if (categoryParam == null || categoryParam.isBlank()) {
                // fallback to simple unfiltered list
                Set<CandidateDTO> set = service.getAll();
                ctx.status(HttpStatus.OK).json(set);
                return;
            }

            Category cat;
            try {
                cat = Category.valueOf(categoryParam.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                        "error", "Invalid category",
                        "message", "Unknown category: " + categoryParam
                ));
                return;
            }

            Set<CandidateDTO> filtered = service.getAll(cat);
            ctx.status(HttpStatus.OK).json(filtered);
        };
    }

    public Handler getById() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            CandidateDTO dto = service.getById(id);
            if (dto == null) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
                return;
            }
            ctx.status(HttpStatus.OK).json(dto);
        };
    }

    public Handler update() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            CandidateCreateDTO dto = ctx.bodyAsClass(CandidateCreateDTO.class);
            CandidateDTO updated = service.update(id, dto);
            if (updated == null) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
                return;
            }
            ctx.status(HttpStatus.OK).json(updated);
        };
    }

    public Handler delete() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            boolean removed = service.delete(id);
            if (!removed) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate not found");
                return;
            }
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }

    public Handler removeSkill() {
        return ctx -> {
            Long candidateId = Long.parseLong(ctx.pathParam("candidateId"));
            Long skillId = Long.parseLong(ctx.pathParam("skillId"));
            CandidateDTO updated = service.removeSkill(candidateId, skillId);
            if (updated == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Candidate or Skill not found"));
                return;
            }
            ctx.status(HttpStatus.OK).json(updated);
        };
    }

    public Handler linkSkill() {
        return ctx -> {
            Long candidateId = Long.parseLong(ctx.pathParam("candidateId"));
            Long skillId = Long.parseLong(ctx.pathParam("skillId"));
            CandidateDTO updated = service.linkSkill(candidateId, skillId);
            if (updated == null) {
                ctx.status(HttpStatus.NOT_FOUND).json("Candidate or Skill not found");
                return;
            }
            ctx.status(HttpStatus.OK).json(updated);
        };
    }

    public Handler getTopByPopularity() {
        return ctx -> {
            TopCandidatePopularityDTO best = service.getTopCandidateByAveragePopularity();
            if (best != null) {
                ctx.status(HttpStatus.OK).json(best);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("msg", "No candidate with popularity data"));
            }
        };
    }
}
