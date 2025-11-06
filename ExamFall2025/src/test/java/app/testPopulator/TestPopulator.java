package app.testPopulator;

import app.config.HibernateConfig;
import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.entities.CandidateSkillId;
import app.entities.Skill;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class TestPopulator {

    private final EntityManagerFactory emf;
    private Long testCandidateId;
    private Long testSkillId;

    public TestPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populate() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Clear existing test data in the right order
            em.createQuery("DELETE FROM CandidateSkill").executeUpdate();
            em.createQuery("DELETE FROM Candidate").executeUpdate();
            em.createQuery("DELETE FROM Skill").executeUpdate();

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

            // --- Create candidate-skill links ---
            CandidateSkill cs1 = CandidateSkill.builder()
                    .id(new CandidateSkillId(cand.getId(), java.getId()))
                    .candidate(cand)
                    .skill(java)
                    .build();

            CandidateSkill cs2 = CandidateSkill.builder()
                    .id(new CandidateSkillId(cand.getId(), postgres.getId()))
                    .candidate(cand)
                    .skill(postgres)
                    .build();

            em.persist(cs1);
            em.persist(cs2);

            // Keep both sides in sync (optional but good practice)
            cand.getCandidateSkills().addAll(List.of(cs1, cs2));
            java.getCandidateSkills().add(cs1);
            postgres.getCandidateSkills().add(cs2);

            tx.commit();

            this.testCandidateId = cand.getId();
            this.testSkillId = java.getId();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Long getTestCandidateId() {
        return testCandidateId;
    }

    public Long getTestSkillId() {
        return testSkillId;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        try {
            TestPopulator pop = new TestPopulator(emf);
            pop.populate();
            System.out.println("Candidate ID: " + pop.getTestCandidateId());
            System.out.println("Skill ID: " + pop.getTestSkillId());
        } finally {
            if (emf != null && emf.isOpen()) emf.close();
        }
    }
}
