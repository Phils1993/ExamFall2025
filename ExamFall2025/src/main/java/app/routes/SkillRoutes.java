package app.routes;

import app.controllers.SkillController;
import app.services.SkillService;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SkillRoutes {
    private final SkillController controller;

    public SkillRoutes(SkillService skillService) {
        this.controller = new SkillController(skillService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("skills", () -> {
                get(controller.getAll());
                post(controller.create());
                path("{id}", () -> {
                    get(controller.getById());
                    put(controller.update());
                    delete(controller.delete());
                });
            });
        };
    }
}
