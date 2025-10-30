package app.Populator;

import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;

/*
public class EksForPopulator {

    private final EntityManagerFactory emf;

    public EksForPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populate() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Avoid double-seeding
            Long guideCount = em.createQuery("SELECT COUNT(g) FROM Guide g", Long.class).getSingleResult();
            Long tripCount = em.createQuery("SELECT COUNT(t) FROM Trip t", Long.class).getSingleResult();
            if (guideCount > 0 || tripCount > 0) {
                em.getTransaction().commit();
                return;
            }

            // --- Guides ---
            Guide anna = Guide.builder()
                    .name("Anna Berg")
                    .email("anna@example.com")
                    .phone("12345678")
                    .yearsOfExperience(5)
                    .build();

            Guide jonas = Guide.builder()
                    .name("Jonas Hansen")
                    .email("jonas@example.com")
                    .phone("87654321")
                    .yearsOfExperience(8)
                    .build();

            Guide mia = Guide.builder()
                    .name("Mia Sørensen")
                    .email("mia@example.com")
                    .phone("11223344")
                    .yearsOfExperience(3)
                    .build();

            em.persist(anna);
            em.persist(jonas);
            em.persist(mia);

            // --- Trips ---
            Trip snowAdventure = Trip.builder()
                    .name("Snow Adventure")
                    .startTime(LocalDateTime.now().plusDays(30))
                    .endTime(LocalDateTime.now().plusDays(37))
                    .latitude(61.5)
                    .longitude(8.9)
                    .price(1299.99)
                    .category(Category.SNOW)
                    .guide(anna)
                    .build();

            Trip beachEscape = Trip.builder()
                    .name("Beach Escape")
                    .startTime(LocalDateTime.now().plusDays(10))
                    .endTime(LocalDateTime.now().plusDays(14))
                    .latitude(12.34)
                    .longitude(-45.67)
                    .price(899.50)
                    .category(Category.BEACH)
                    .guide(jonas)
                    .build();

            Trip cityWeekend = Trip.builder()
                    .name("City Weekend")
                    .startTime(LocalDateTime.now().plusDays(5))
                    .endTime(LocalDateTime.now().plusDays(7))
                    .latitude(55.6761)
                    .longitude(12.5683)
                    .price(499.00)
                    .category(Category.CITY)
                    .guide(mia)
                    .build();

            Trip noCategoryTrip = Trip.builder()
                    .name("Mystery Trip")
                    .startTime(LocalDateTime.now().plusDays(20))
                    .endTime(LocalDateTime.now().plusDays(22))
                    .latitude(50.0)
                    .longitude(10.0)
                    .price(299.00)
                    .category(null) // test "none" filter
                    .guide(anna)
                    .build();

            List.of(snowAdventure, beachEscape, cityWeekend, noCategoryTrip)
                    .forEach(em::persist);

            em.getTransaction().commit();
        }
    }

    // --- Main method to run standalone ---
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EksForPopulator populator = new EksForPopulator(emf);
        populator.populate();
        emf.close();
        System.out.println("Database populated successfully!");
    }
}
