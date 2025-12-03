package app.config;

import app.daos.CandidateDAO;
import app.routes.CandidateRoutes;
import app.security.SecurityRoutes;
import app.services.ServiceAPI;
import io.javalin.apibuilder.EndpointGroup;

public class RoutesRegistry {

    private final SecurityRoutes securityRoutes;
    private final CandidateRoutes candidateRoutes;

    public RoutesRegistry(CandidateDAO candidateDAO, ServiceAPI serviceAPI) {
        this.securityRoutes = new SecurityRoutes();
        this.candidateRoutes = new CandidateRoutes(candidateDAO, serviceAPI);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            candidateRoutes.getRoutes().addEndpoints();
            securityRoutes.getSecurityRoutes().addEndpoints();
            SecurityRoutes.getSecuredRoutes().addEndpoints();
        };
    }
}
