package app;

import app.Populator.Populator;
import app.Populator.UserPopulator;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        System.out.println("Hello world");


        ApplicationConfig.startServer(7070,emf);

        UserPopulator.populateDefaultUsers();
        Populator populator = new Populator(emf);
        populator.populate();
    }
}
