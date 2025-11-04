package app.controllers;

import app.dtos.SkillCreateDTO;
import app.dtos.SkillDTO;
import app.services.SkillService;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class SkillController  implements IController {

    private final SkillService service;

    public SkillController(SkillService service) {
        this.service = service;
    }

    public Handler create() {
        return ctx -> {
            SkillCreateDTO dto = ctx.bodyAsClass(SkillCreateDTO.class);
            SkillDTO created = service.create(dto);
            ctx.status(HttpStatus.CREATED).json(created);
        };
    }

    public Handler getAll() {
        return ctx -> {
            List<SkillDTO> all = service.getAll();
            ctx.status(HttpStatus.OK).json(all);
        };
    }

    public Handler getById() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            SkillDTO dto = service.getById(id);
            if (dto == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("msg", "Skill not found"));
                return;
            }
            ctx.status(HttpStatus.OK).json(dto);
        };
    }

    public Handler update() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            SkillCreateDTO dto = ctx.bodyAsClass(SkillCreateDTO.class);
            SkillDTO updated = service.update(id, dto);
            if (updated == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("msg", "Skill not found"));
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
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("msg", "Skill not found"));
                return;
            }
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }
}
