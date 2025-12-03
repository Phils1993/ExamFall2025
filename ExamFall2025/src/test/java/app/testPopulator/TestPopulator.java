package app.testPopulator;

import app.config.HibernateConfig;
import app.entities.*;
import app.enums.Category;
import app.security.ISecurityDAO;
import app.security.SecurityDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public class TestPopulator {

    private final EntityManagerFactory emf;
    private Integer testCandidateId;
    private Integer testSkillId;

    public TestPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populate() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Clear existing test data
            em.createQuery("DELETE FROM CandidateSkill").executeUpdate();
            em.createQuery("DELETE FROM Candidate").executeUpdate();
            em.createQuery("DELETE FROM Skill").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();

            // --- Create skills ---
            Skill java = Skill.builder()
                    .name("Java")
                    .slug("java")
                    .category(Category.PROG_LANG)
                    .description("General purpose language")
                    .build();

            Skill postgres = Skill.builder()
                    .name("PostgreSQL")
                    .slug("postgresql")
                    .category(Category.DB)
                    .description("Relational DB")
                    .build();

            em.persist(java);
            em.persist(postgres);

            // --- Create candidate ---
            Candidate cand = Candidate.builder()
                    .name("Test Candidate")
                    .phone("+45 10000000")
                    .education("MSc Testing")
                    .build();

            em.persist(cand);
            em.flush(); // ensure IDs are assigned

            // --- Link candidate to skills using static factory method ---
            CandidateSkill cs1 = CandidateSkill.of(cand, java);
            CandidateSkill cs2 = CandidateSkill.of(cand, postgres);

            em.persist(cs1);
            em.persist(cs2);

            // Keep both sides in sync
            cand.getCandidateSkills().addAll(List.of(cs1, cs2));
            java.getCandidateSkills().add(cs1);
            postgres.getCandidateSkills().add(cs2);

            tx.commit();

            this.testCandidateId = cand.getId();
            this.testSkillId = java.getId();

            // --- Create users and roles ---
            ISecurityDAO securityDAO = new SecurityDAO(emf);
            securityDAO.createRole("User");
            securityDAO.createRole("Admin");

            securityDAO.createUser("Philip", "pass12345");
            securityDAO.createUser("PhilipAdmin", "pass12345");

            securityDAO.addUserRole("Philip", "User");
            securityDAO.addUserRole("PhilipAdmin", "Admin");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Integer getTestCandidateId() {
        return testCandidateId;
    }

    public Integer getTestSkillId() {
        return testSkillId;
    }
}
