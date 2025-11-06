package app.routes;

import app.controllers.CandidateController;
import app.daos.CandidateDAO;
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
                get(controller.getAllCandidates());
                post(controller.create());

                path("{id}", () -> {
                    get(controller.getById());
                    put(controller.update());
                    delete(controller.delete());
                });

                path("{candidateId}/skills/{skillId}", () -> {
                    put(controller.linkSkill());
                    delete(controller.removeSkill());
                });
            });

            path("reports", () -> {
                path("candidates", () -> {
                    get("top-by-popularity", controller.getTopByPopularity());
                });
            });
        };
    }
}
