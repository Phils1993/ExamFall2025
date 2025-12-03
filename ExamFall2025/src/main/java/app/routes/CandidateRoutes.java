package app.routes;

import app.controllers.CandidateController;
import app.daos.CandidateDAO;
import app.security.Roles;
import app.services.ServiceAPI;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class CandidateRoutes {

    private final CandidateController controller;
    private final ServiceAPI serviceAPI;

    public CandidateRoutes(CandidateDAO candidateDAO,  ServiceAPI serviceAPI) {
        this.controller = new CandidateController(candidateDAO);
        this.serviceAPI = serviceAPI;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("candidates", () -> {
                get(controller.getAll(), Roles.USER, Roles.ADMIN);
                post(controller.create(), Roles.USER, Roles.ADMIN);

                path("{id}", () -> {
                    get(controller.getById(), Roles.USER, Roles.ADMIN);
                    put(controller.update(), Roles.USER, Roles.ADMIN);
                    delete(controller.delete(), Roles.USER, Roles.ADMIN);
                });

                path("{candidateId}/skills/{skillId}", () -> {
                    put(controller.linkSkill(), Roles.USER, Roles.ADMIN);
                    delete(controller.removeSkill(), Roles.USER, Roles.ADMIN);
                });
            });

            path("reports", () -> {
                path("candidates", () -> {
                    get("top-by-popularity", controller.getTopCandidateByPopularity(), Roles.USER, Roles.ADMIN);
                });
            });
        };
    }
}
