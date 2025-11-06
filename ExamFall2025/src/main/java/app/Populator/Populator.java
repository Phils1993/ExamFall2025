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

            // --- Skills ---
            Skill java = Skill.builder().name("Java").category(Category.PROG_LANG).description("General purpose language").build();
            Skill python = Skill.builder().name("Python").category(Category.PROG_LANG).description("Scripting and data").build();
            Skill postgres = Skill.builder().name("PostgreSQL").category(Category.DB).description("Relational DB").build();
            Skill docker = Skill.builder().name("Docker").category(Category.DEVOPS).description("Containerization").build();
            Skill spring = Skill.builder().name("Spring Boot").category(Category.FRAMEWORK).description("Java framework").build();

            List<Skill> skills = List.of(java, python, postgres, docker, spring);
            skills.forEach(em::persist);
            em.flush(); // assign IDs

            // --- Candidates ---
            Candidate alice = Candidate.builder().name("Alice Andersen").phone("+45 20000001").education("MSc Computer Science").build();
            Candidate bob = Candidate.builder().name("Bob Berg").phone("+45 20000002").education("BSc Information Systems").build();

            em.persist(alice);
            em.persist(bob);
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
