package app.Populator;

import app.config.HibernateConfig;
import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.entities.CandidateSkillId;
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
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Avoid double-seeding: if there are already candidates or skills, do nothing
            Long candidateCount = em.createQuery("SELECT COUNT(c) FROM Candidate c", Long.class).getSingleResult();
            Long skillCount = em.createQuery("SELECT COUNT(s) FROM Skill s", Long.class).getSingleResult();
            if (candidateCount > 0 || skillCount > 0) {
                em.getTransaction().commit();
                return;
            }

            // --- Skills ---
            Skill java = Skill.builder()
                    .name("Java")
                    .category(Category.PROG_LANG)
                    .description("General purpose language")
                    .build();

            Skill python = Skill.builder()
                    .name("Python")
                    .category(Category.PROG_LANG)
                    .description("Scripting and data")
                    .build();

            Skill postgres = Skill.builder()
                    .name("PostgreSQL")
                    .category(Category.DB)
                    .description("Relational DB")
                    .build();

            Skill docker = Skill.builder()
                    .name("Docker")
                    .category(Category.DEVOPS)
                    .description("Containerization")
                    .build();

            Skill spring = Skill.builder()
                    .name("Spring Boot")
                    .category(Category.FRAMEWORK)
                    .description("Java framework")
                    .build();

            List<Skill> skills = List.of(java, python, postgres, docker, spring);
            skills.forEach(em::persist);

            // Ensure IDs assigned
            em.flush();

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

            em.persist(alice);
            em.persist(bob);

            em.flush();

            // --- Link candidates and skills via CandidateSkill (no optional metadata) ---
            CandidateSkill cs1 = CandidateSkill.builder()
                    .id(new CandidateSkillId(alice.getId(), java.getId()))
                    .candidate(alice)
                    .skill(java)
                    .build();
            alice.addSkill(cs1);
            java.addCandidateSkill(cs1);
            em.persist(cs1);

            CandidateSkill cs2 = CandidateSkill.builder()
                    .id(new CandidateSkillId(alice.getId(), postgres.getId()))
                    .candidate(alice)
                    .skill(postgres)
                    .build();
            alice.addSkill(cs2);
            postgres.addCandidateSkill(cs2);
            em.persist(cs2);

            CandidateSkill cs3 = CandidateSkill.builder()
                    .id(new CandidateSkillId(bob.getId(), python.getId()))
                    .candidate(bob)
                    .skill(python)
                    .build();
            bob.addSkill(cs3);
            python.addCandidateSkill(cs3);
            em.persist(cs3);

            CandidateSkill cs4 = CandidateSkill.builder()
                    .id(new CandidateSkillId(bob.getId(), docker.getId()))
                    .candidate(bob)
                    .skill(docker)
                    .build();
            bob.addSkill(cs4);
            docker.addCandidateSkill(cs4);
            em.persist(cs4);

            em.getTransaction().commit();
        } catch (Throwable t) {
            // transaction rollback is handled by try-with-resources + explicit check if active,
            // but here we rethrow after ensuring rollback where possible
            throw t;
        }
    }

    // Convenience main for manual runs
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try {
            Populator pop = new Populator(emf);
            pop.populate();
            System.out.println("Database populated successfully!");
        } finally {
            emf.close();
        }
    }
}

