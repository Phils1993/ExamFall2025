package app.daos;

import app.config.HibernateConfig;
import app.entities.Candidate;
import app.entities.Skill;
import app.daos.AbstractDAO;
import app.daos.CandidateDAO;
import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.entities.CandidateSkillId;
import app.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.logging.Logger;

public class CandidateDAO extends AbstractDAO<Candidate>  implements ICandidateDAO {

    private static Logger logger = (Logger) LoggerFactory.getLogger(CandidateDAO.class);

    // Logger til debug-specifik information (kan bruges til tracing)
    private static final Logger debugLogger = (Logger) LoggerFactory.getLogger("app");

    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private EntityManager em;


    public CandidateDAO(EntityManagerFactory emf) {
        super(emf, Candidate.class);
    }

    @Override
    public Optional<Candidate> findByIdWithSkills(Long id) {
        return Optional.empty();
    }

    @Override
    public Candidate addSkill(Long candidateId, Long skillId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate c = em.find(Candidate.class, candidateId);
            Skill s = em.find(Skill.class, skillId);
            if (c == null || s == null) {
                em.getTransaction().commit();
                return null;
            }

            boolean exists = c.getCandidateSkills().stream()
                    .anyMatch(cs -> cs.getSkill() != null && skillId.equals(cs.getSkill().getId()));
            if (!exists) {
                CandidateSkill cs = CandidateSkill.builder()
                        .id(new CandidateSkillId(c.getId(), s.getId()))
                        .candidate(c)
                        .skill(s)
                        .build();
                c.addSkill(cs);
                s.addCandidateSkill(cs);
                em.persist(cs);
            }

            em.getTransaction().commit();

            // return managed, refreshed candidate
            return em.find(Candidate.class, candidateId);
        }
    }

    @Override
    public Candidate removeSkill(Long candidateId, Long skillId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate c = em.find(Candidate.class, candidateId);
            if (c == null) {
                em.getTransaction().commit();
                return null;
            }

            CandidateSkill target = c.getCandidateSkills().stream()
                    .filter(cs -> cs.getSkill() != null && skillId.equals(cs.getSkill().getId()))
                    .findFirst()
                    .orElse(null);

            if (target != null) {
                // detach relationships on both sides
                c.removeSkill(target);
                Skill s = target.getSkill();
                if (s != null) s.removeCandidateSkill(target);

                CandidateSkill managed = em.find(CandidateSkill.class, target.getId());
                if (managed != null) em.remove(managed);
            }

            em.getTransaction().commit();

            return em.find(Candidate.class, candidateId);
        }
    }

    /*
    // Optional: override getAll to join-fetch skills efficiently
    @Override
    public java.util.List<Candidate> getAll() {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Candidate> q = em.createQuery(
                    "SELECT DISTINCT c FROM Candidate c LEFT JOIN FETCH c.candidateSkills cs LEFT JOIN FETCH cs.skill",
                    Candidate.class);
            return q.getResultList();
        }
    }
     */
}
