package app.controllers;

/*
import app.daos.SkillDAO;
import app.dtos.SkillCreateDTO;
import app.dtos.SkillDTO;
import app.entities.Skill;
import app.mapper.SkillMapper;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillController implements IController {

    private final SkillDAO skillDAO;
    private final SkillMapper mapper = new SkillMapper();

    public SkillController(SkillDAO skillDAO) {
        this.skillDAO = skillDAO;
    }

    @Override
    public Handler create() {
        return ctx -> {
            SkillCreateDTO dto = ctx.bodyAsClass(SkillCreateDTO.class);
            Skill skill = mapper.fromCreateDto(dto);
            Skill created = skillDAO.create(skill);
            ctx.status(HttpStatus.CREATED).json(new SkillDTO(created));
        };
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            List<Skill> all = skillDAO.getAll();
            List<SkillDTO> dtos = all.stream()
                    .map(SkillDTO::new)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(dtos);
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            Skill skill = skillDAO.getById(id);
            if (skill == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("msg", "Skill not found"));
                return;
            }
            ctx.status(HttpStatus.OK).json(new SkillDTO(skill));
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            SkillCreateDTO dto = ctx.bodyAsClass(SkillCreateDTO.class);
            Skill skill = mapper.fromCreateDto(dto);
            skill.setId(id);
            Skill updated = skillDAO.update(skill);
            ctx.status(HttpStatus.OK).json(new SkillDTO(updated));
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            boolean removed = skillDAO.delete(id);
            if (!removed) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("msg", "Skill not found"));
                return;
            }
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }
}

 */
