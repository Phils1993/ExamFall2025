package app.routes;

import app.controllers.CandidateController;
import app.services.CandidateService;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
import jakarta.persistence.EntityManagerFactory;

public class CandidateRoutes {

    private final CandidateController controller;

    public CandidateRoutes(CandidateService candidateService) {
        this.controller = new CandidateController(candidateService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("candidates", () -> {
                // Use getAllCandidates which handles both:
                // GET /candidates  (no query param) and
                // GET /candidates?category={category} (filtered)
                get(controller.getAllCandidates());

                post(controller.create());

                path("{id}", () -> {
                    get(controller.getById());
                    put(controller.update());
                    delete(controller.delete());
                });

                path("{candidateId}/skills/{skillId}", () -> {
                    put(controller.linkSkill());
                    delete(controller.removeSkill()); // if you support removing via DELETE
                });
            });

            // Reports namespace
            path("reports", () -> {
                path("candidates", () -> {
                    get("top-by-popularity", controller.getTopByPopularity());
                });
            });
        };
    }
}
