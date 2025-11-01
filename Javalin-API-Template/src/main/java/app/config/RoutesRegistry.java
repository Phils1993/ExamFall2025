package app.config;

import app.security.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

public class RoutesRegistry {

    private final EntityManagerFactory emf;

    public RoutesRegistry(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //private final WeekRoutes weekRoutes;
    //private final DayRoutes dayRoutes;
    //private final ExerciseRoutes exerciseRoutes;
    //private final DayExerciseRoutes dayExerciseRoutes;
    private final SecurityRoutes securityRoutes = new SecurityRoutes();

    /*
    public RoutesRegistry(ServiceRegistry services) {
        //this.weekRoutes = new WeekRoutes(services.weekService);
        //this.dayRoutes = new DayRoutes(services.dayService);
        //this.exerciseRoutes = new ExerciseRoutes(services.exerciseService);
        //this.dayExerciseRoutes = new DayExerciseRoutes(services.dayExerciseService);
        this.securityRoutes =  new SecurityRoutes();
    }

     */


    public EndpointGroup getRoutes() {
        return () -> {
            //weekRoutes.getRoutes().addEndpoints();
            //dayRoutes.getRoutes().addEndpoints();
            //exerciseRoutes.getRoutes().addEndpoints();
            //dayExerciseRoutes.getRoutes().addEndpoints();
            securityRoutes.getSecurityRoutes().addEndpoints();
            SecurityRoutes.getSecuredRoutes().addEndpoints();

        };
    }
}
