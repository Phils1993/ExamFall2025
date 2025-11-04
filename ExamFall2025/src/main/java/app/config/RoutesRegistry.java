package app.config;

import app.routes.CandidateRoutes;
import app.routes.SkillRoutes;
import app.security.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;

public class RoutesRegistry {

    private final SecurityRoutes securityRoutes;
    private final CandidateRoutes candidateRoutes;
    private final SkillRoutes skillRoutes;

    public RoutesRegistry(ServiceRegistry serviceRegistry) {
        this.securityRoutes = new SecurityRoutes();
        this.candidateRoutes = new CandidateRoutes(serviceRegistry.candidateService);
        this.skillRoutes = new SkillRoutes(serviceRegistry.skillService);
    }


    public EndpointGroup getRoutes() {
        return () -> {
            candidateRoutes.getRoutes().addEndpoints();
            skillRoutes.getRoutes().addEndpoints();
            securityRoutes.getSecurityRoutes().addEndpoints();
            SecurityRoutes.getSecuredRoutes().addEndpoints();
        };
    }
}
