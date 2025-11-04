package app.testPopulator;

import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.entities.CandidateSkillId;
import app.entities.Skill;
import app.enums.Category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * app.testPopulator.TestPopulator used by integration tests.
 * - Populates deterministic data (skills with slugs, two candidates, links).
 * - Provides helper getters for tests.
 *
 * Intended for test code only (put under src/test/java).
 */
public class TestPopulator {

    private static final Logger LOG = Logger.getLogger(TestPopulator.class.getName());
    private final EntityManagerFactory emf;

    public TestPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Populate a small deterministic dataset for integration tests:
     * - five skills (with slugs)
     * - two candidates
     * - four candidate-skill links
     *
     * Idempotent: if candidates already exist, the method returns without changing data.
     */
    public void populate() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            Long candidateCount = em.createQuery("SELECT COUNT(c) FROM Candidate c", Long.class)
                    .getSingleResult();
            if (candidateCount != null && candidateCount > 0) {
                em.getTransaction().commit();
                return;
            }

            // --- create skills with explicit slugs ---
            Skill java = Skill.builder()
                    .name("Java")
                    .slug("java")
                    .category(Category.PROG_LANG)
                    .description("General purpose language")
                    .build();

            Skill python = Skill.builder()
                    .name("Python")
                    .slug("python")
                    .category(Category.PROG_LANG)
                    .description("Scripting and data")
                    .build();

            Skill postgres = Skill.builder()
                    .name("PostgreSQL")
                    .slug("postgresql")
                    .category(Category.DB)
                    .description("Relational DB")
                    .build();

            Skill docker = Skill.builder()
                    .name("Docker")
                    .slug("docker")
                    .category(Category.DEVOPS)
                    .description("Containerization")
                    .build();

            Skill spring = Skill.builder()
                    .name("Spring Boot")
                    .slug("spring-boot")
                    .category(Category.FRAMEWORK)
                    .description("Java framework")
                    .build();

            List<Skill> skills = List.of(java, python, postgres, docker, spring);
            skills.forEach(em::persist);
            em.flush(); // assign skill IDs

            // --- create candidates ---
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

            em.persist(alice);
            em.persist(bob);
            em.flush(); // assign candidate IDs

            // reload managed instances to avoid detached-entity issues
            Candidate managedAlice = em.find(Candidate.class, alice.getId());
            Candidate managedBob = em.find(Candidate.class, bob.getId());

            Skill managedJava = em.createQuery("SELECT s FROM Skill s WHERE s.slug = :slug", Skill.class)
                    .setParameter("slug", "java")
                    .getSingleResult();

            Skill managedPostgres = em.createQuery("SELECT s FROM Skill s WHERE s.slug = :slug", Skill.class)
                    .setParameter("slug", "postgresql")
                    .getSingleResult();

            Skill managedPython = em.createQuery("SELECT s FROM Skill s WHERE s.slug = :slug", Skill.class)
                    .setParameter("slug", "python")
                    .getSingleResult();

            Skill managedDocker = em.createQuery("SELECT s FROM Skill s WHERE s.slug = :slug", Skill.class)
                    .setParameter("slug", "docker")
                    .getSingleResult();

            // --- link candidates and skills ---
            CandidateSkill cs1 = CandidateSkill.builder()
                    .id(new CandidateSkillId(managedAlice.getId(), managedJava.getId()))
                    .candidate(managedAlice)
                    .skill(managedJava)
                    .build();
            managedAlice.addSkill(cs1);
            managedJava.addCandidateSkill(cs1);
            em.persist(cs1);

            CandidateSkill cs2 = CandidateSkill.builder()
                    .id(new CandidateSkillId(managedAlice.getId(), managedPostgres.getId()))
                    .candidate(managedAlice)
                    .skill(managedPostgres)
                    .build();
            managedAlice.addSkill(cs2);
            managedPostgres.addCandidateSkill(cs2);
            em.persist(cs2);

            CandidateSkill cs3 = CandidateSkill.builder()
                    .id(new CandidateSkillId(managedBob.getId(), managedPython.getId()))
                    .candidate(managedBob)
                    .skill(managedPython)
                    .build();
            managedBob.addSkill(cs3);
            managedPython.addCandidateSkill(cs3);
            em.persist(cs3);

            CandidateSkill cs4 = CandidateSkill.builder()
                    .id(new CandidateSkillId(managedBob.getId(), managedDocker.getId()))
                    .candidate(managedBob)
                    .skill(managedDocker)
                    .build();
            managedBob.addSkill(cs4);
            managedDocker.addCandidateSkill(cs4);
            em.persist(cs4);

            em.getTransaction().commit();
        } catch (Throwable t) {
            if (em != null && em.getTransaction().isActive()) {
                try { em.getTransaction().rollback(); } catch (Exception ignored) {}
            }
            LOG.log(Level.SEVERE, "TestPopulator.populate failed", t);
            throw new RuntimeException(t);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Long getAnyCandidateId() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c.id FROM Candidate c ORDER BY c.id ASC", Long.class)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    public Long getAnySkillId() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT s.id FROM Skill s ORDER BY s.id ASC", Long.class)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

}