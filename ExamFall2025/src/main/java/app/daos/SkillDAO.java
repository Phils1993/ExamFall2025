package app.daos;

import app.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SkillDAO implements ISkillDAO {

    private static final Logger logger = LoggerFactory.getLogger(SkillDAO.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    private final EntityManagerFactory emf;

    public SkillDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Skill getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Skill.class, id);
        }
    }

    @Override
    public Set<Skill> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Skill> list = em.createQuery("SELECT s FROM Skill s", Skill.class).getResultList();
            return new HashSet<>(list);
        }
    }

    @Override
    public Skill create(Skill s) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(s);
            em.getTransaction().commit();
            return s;
        } catch (RuntimeException e) {
            logger.error("Failed to create Skill", e);
            throw e;
        }
    }

    @Override
    public Skill update(Skill s) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill merged = em.merge(s);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            logger.error("Failed to update Skill", e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill found = em.find(Skill.class, id);
            if (found != null) {
                em.remove(found);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.error("Failed to delete Skill id={}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<Skill> findByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Skill> q = em.createQuery("SELECT s FROM Skill s WHERE s.name = :name", Skill.class);
            q.setParameter("name", name);
            try {
                return Optional.of(q.getSingleResult());
            } catch (NoResultException nre) {
                return Optional.empty();
            }
        }
    }
}
