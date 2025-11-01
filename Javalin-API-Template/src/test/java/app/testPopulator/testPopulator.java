package app.testPopulator;

//TODO: eksempel på en teest populator

/*
import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;

public class TestPopulator {
    private final EntityManagerFactory emf;
    private Long testTripId;
    private Long testGuideId;

    public TestPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populate() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Clear old data
            em.createNativeQuery("TRUNCATE TABLE trips RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE guides RESTART IDENTITY CASCADE").executeUpdate();

            // Insert guide
            Guide guide = Guide.builder()
                    .name("Test Guide")
                    .email("test@example.com")
                    .phone("12345678")
                    .yearsOfExperience(1)
                    .build();

            // Insert trip and link to guide
            Trip trip = Trip.builder()
                    .name("Test Trip")
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .latitude(10.0)
                    .longitude(20.0)
                    .price(100.0)
                    .category(Category.BEACH) // ensures packing API returns items
                    .guide(guide)
                    .build();

            // maintain both sides of the relationship
            guide.getTrips().add(trip);

            em.persist(guide);

            em.getTransaction().commit();

            this.testTripId = trip.getId();
            this.testGuideId = guide.getId();
        }
    }

    public Long getTestTripId() {
        return testTripId;
    }

    public Long getTestGuideId() {
        return testGuideId;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        TestPopulator populator = new TestPopulator(emf);
        populator.populate();
        System.out.println("Trip ID: " + populator.getTestTripId());
        System.out.println("Guide ID: " + populator.getTestGuideId());
        emf.close();
    }
}

 */

