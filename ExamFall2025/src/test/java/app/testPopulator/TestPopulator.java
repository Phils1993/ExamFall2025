package app.testPopulator;

import app.config.HibernateConfig;
import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.entities.CandidateSkillId;
import app.entities.Skill;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TestPopulator {
    private final EntityManagerFactory emf;
    private Long testCandidateId;
    private Long testSkillId;

    public TestPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populate() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Clear old data — adjust table names if your schema differs
            em.createNativeQuery("TRUNCATE TABLE candidate_skill RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE candidate RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE skill RESTART IDENTITY CASCADE").executeUpdate();

            // Create skills
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

            List<Skill> skills = List.of(java, postgres);
            skills.forEach(em::persist);
            em.flush(); // ensure IDs assigned

            // Create candidate
            Candidate cand = Candidate.builder()
                    .name("Test Candidate")
                    .phone("+45 10000000")
                    .education("MSc Testing")
                    .build();

            em.persist(cand);
            em.flush(); // ensure candidate id assigned

            // Link candidate to skills using CandidateSkill and keep both sides in sync
            CandidateSkill cs1 = CandidateSkill.builder()
                    .id(new CandidateSkillId(cand.getId(), java.getId()))
                    .candidate(cand)
                    .skill(java)
                    .build();
            cand.addSkill(cs1);
            java.addCandidateSkill(cs1);
            em.persist(cs1);

            CandidateSkill cs2 = CandidateSkill.builder()
                    .id(new CandidateSkillId(cand.getId(), postgres.getId()))
                    .candidate(cand)
                    .skill(postgres)
                    .build();
            cand.addSkill(cs2);
            postgres.addCandidateSkill(cs2);
            em.persist(cs2);

            em.getTransaction().commit();

            this.testCandidateId = cand.getId();
            this.testSkillId = java.getId(); // return one skill id for convenience
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
