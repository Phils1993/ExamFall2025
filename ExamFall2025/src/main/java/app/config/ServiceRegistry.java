package app.config;

import app.services.CandidateService;
import jakarta.persistence.EntityManagerFactory;

public class ServiceRegistry {

    //TODO: use this if I make a service layer
    public final CandidateService candidateService;

    public ServiceRegistry(EntityManagerFactory emf) {
        this.candidateService = new CandidateService(emf);
    }
}
