package app.Populator;

import app.config.HibernateConfig;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Populator {

    private final EntityManagerFactory emf;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            boolean hasCandidates = em.createQuery("SELECT COUNT(c) FROM Candidate c", Long.class).getSingleResult() > 0;
            boolean hasSkills = em.createQuery("SELECT COUNT(s) FROM Skill s", Long.class).getSingleResult() > 0;
            if (hasCandidates || hasSkills) {
                em.getTransaction().commit();
                return;
            }

            // --- Skills with correct slugs ---
            Skill java = createSkill("Java", Category.PROG_LANG, "General purpose language");
            Skill python = createSkill("Python", Category.PROG_LANG, "Scripting and data");
            Skill postgres = createSkill("PostgreSQL", Category.DB, "Relational DB");
            Skill docker = createSkill("Docker", Category.DEVOPS, "Containerization");
            Skill spring = createSkill("Spring Boot", Category.FRAMEWORK, "Java framework");

            List<Skill> skills = List.of(java, python, postgres, docker, spring);
            skills.forEach(skill -> {
                System.out.println("Persisting skill: " + skill.getName() + " → slug: " + skill.getSlug());
                em.persist(skill);
            });
            em.flush(); // assign IDs

            // --- Candidates ---
            Candidate alice = Candidate.builder()
                    .name("Alice Andersen")
                    .phone("+45 20000001")
                    .education("MSc Computer Science")
                    .build();

            Candidate bob = Candidate.builder()
                    .name("Bob Berg")
                    .phone("+45 20000002")
                    .education("BSc Information Systems")
                    .build();

            Candidate empty = Candidate.builder()
                    .name("Lars N.")
                    .phone("+45 20000003")
                    .education("MSc Distributed Systems")
                    .build();

            em.persist(alice);
            em.persist(bob);
            em.persist(empty);
            em.flush(); // assign IDs

            // --- Link skills using helper methods ---
            alice.addSkill(java);
            alice.addSkill(postgres);
            alice.addSkill(spring);
            bob.addSkill(python);
            bob.addSkill(docker);

            em.getTransaction().commit();
        } catch (Throwable t) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw t;
        } finally {
            em.close();
        }
    }

    private Skill createSkill(String name, Category category, String description) {
        return Skill.builder()
                .name(name)
                .slug(name.toLowerCase().replace(" ", "-"))
                .category(category)
                .description(description)
                .build();
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try {
            new Populator(emf).populate();
            System.out.println("Database populated successfully!");
        } finally {
            emf.close();
        }
    }
}
