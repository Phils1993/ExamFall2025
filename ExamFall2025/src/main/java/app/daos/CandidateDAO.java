package app.daos;

import app.entities.Candidate;
import app.entities.CandidateSkill;
import app.entities.CandidateSkillId;
import app.entities.Skill;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class CandidateDAO implements ICandidateDAO {

    private static final Logger logger = LoggerFactory.getLogger(CandidateDAO.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    private final EntityManagerFactory emf;

    public CandidateDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Candidate getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Candidate.class, id);
        }
    }

    @Override
    public Set<Candidate> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT DISTINCT c FROM Candidate c " +
                    "LEFT JOIN FETCH c.candidateSkills cs " +
                    "LEFT JOIN FETCH cs.skill s";
            TypedQuery<Candidate> q = em.createQuery(jpql, Candidate.class);
            List<Candidate> list = q.getResultList();
            return new HashSet<>(list);
        }
    }

    @Override
    public Candidate create(Candidate c) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
            // return a reloaded instance with skills (likely empty) to be explicit
            return findByIdWithSkills(c.getId());
        } catch (RuntimeException ex) {
            logger.error("Failed to create candidate", ex);
            throw ex;
        }
    }

    @Override
    public Candidate update(Candidate c) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate merged = em.merge(c);
            em.getTransaction().commit();
            // return merged with skills loaded
            return findByIdWithSkills(merged.getId());
        } catch (RuntimeException ex) {
            logger.error("Failed to update candidate id={}", c.getId(), ex);
            throw ex;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate found = em.find(Candidate.class, id);
            if (found != null) {
                em.remove(found);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            logger.error("Failed to delete candidate id={}", id, ex);
            throw ex;
        }
    }

    public Set<Candidate> getAllBySkillCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT DISTINCT c FROM Candidate c " +
                    "LEFT JOIN FETCH c.candidateSkills cs " +
                    "LEFT JOIN FETCH cs.skill s " +
                    "WHERE s.category = :cat";
            TypedQuery<Candidate> q = em.createQuery(jpql, Candidate.class);
            q.setParameter("cat", category);
            List<Candidate> list = q.getResultList();
            return new HashSet<>(list);
        }
    }

    @Override
    public Candidate findByIdWithSkills(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "select c from Candidate c left join fetch c.candidateSkills " +
                                    "cs left join fetch cs.skill where c.id " +
                                    "= :id", Candidate.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public Candidate addSkill(Long candidateId, Long skillId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            Candidate managedCandidate = em.find(Candidate.class, candidateId);
            Skill managedSkill = em.find(Skill.class, skillId);

            if (managedCandidate == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Candidate not found: " + candidateId);
            }
            if (managedSkill == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Skill not found: " + skillId);
            }

            CandidateSkill cs = CandidateSkill.builder()
                    .id(new CandidateSkillId(managedCandidate.getId(), managedSkill.getId()))
                    .candidate(managedCandidate)
                    .skill(managedSkill)
                    .build();

            // keep both sides consistent
            managedCandidate.addSkill(cs);
            managedSkill.addCandidateSkill(cs);

            em.persist(cs);
            em.getTransaction().commit();

            // reload candidate with skills in a new EntityManager to return a safe detached instance
            try (EntityManager em2 = emf.createEntityManager()) {
                return em2.createQuery(
                                "select c from Candidate c " +
                                        "left join fetch c.candidateSkills cs left join fetch cs.skill " +
                                        "where c.id = :id", Candidate.class)
                        .setParameter("id", candidateId)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
            }
        } catch (RuntimeException ex) {
            if (em != null && em.getTransaction().isActive()) {
                try { em.getTransaction().rollback(); } catch (Exception ignored) {}
            }
            logger.error("addSkill failed", ex);
            throw ex;
        }
    }


    @Override
    public Candidate removeSkill(Long candidateId, Long skillId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // find the CandidateSkill relation directly
            TypedQuery<CandidateSkill> q = em.createQuery(
                    "SELECT cs FROM CandidateSkill cs WHERE cs.candidate.id = :cid AND cs.skill.id = :sid",
                    CandidateSkill.class);
            q.setParameter("cid", candidateId);
            q.setParameter("sid", skillId);

            List<CandidateSkill> matches = q.getResultList();
            if (matches.isEmpty()) {
                em.getTransaction().commit(); // nothing to remove
                return findByIdWithSkills(candidateId);
            }

            for (CandidateSkill cs : matches) {
                CandidateSkill managed = em.find(CandidateSkill.class, cs.getId());
                if (managed != null) {
                    // remove relation from owning side and delete
                    if (managed.getCandidate() != null && managed.getCandidate().getCandidateSkills() != null) {
                        managed.getCandidate().getCandidateSkills().removeIf(x -> x.getId().equals(managed.getId()));
                    }
                    em.remove(managed);
                }
            }

            em.getTransaction().commit();
            return findByIdWithSkills(candidateId);
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Failed to remove skill {} from candidate {}", skillId, candidateId, ex);
            throw ex;
        } finally {
            em.close();
        }
    }
}
