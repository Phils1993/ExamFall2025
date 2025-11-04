package app.controllers;

import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.enums.Category;
import app.services.CandidateService;
import io.javalin.http.Handler;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Optional;
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

    public Handler getAll() {
        return ctx -> {
            Set<CandidateDTO> set = service.getAll(Optional.empty());
            ctx.status(HttpStatus.OK).json(set);
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
}
