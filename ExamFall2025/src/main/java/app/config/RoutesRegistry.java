package app.config;

import app.routes.CandidateRoutes;
import app.security.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

public class RoutesRegistry {

    private final SecurityRoutes securityRoutes;
    private final CandidateRoutes candidateRoutes;

    public RoutesRegistry(ServiceRegistry serviceRegistry) {
        this.securityRoutes = new SecurityRoutes();
        this.candidateRoutes = new CandidateRoutes(serviceRegistry.candidateService);
    }



    public EndpointGroup getRoutes() {
        return () -> {
            candidateRoutes.getRoutes().addEndpoints();
            securityRoutes.getSecurityRoutes().addEndpoints();
            SecurityRoutes.getSecuredRoutes().addEndpoints();
        };
    }
}
