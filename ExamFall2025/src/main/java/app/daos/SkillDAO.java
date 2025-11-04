package app.daos;

import app.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class SkillDAO extends AbstractDAO<Skill> implements ISkillDAO {

    public SkillDAO(EntityManagerFactory emf) {
        super(emf,  Skill.class);
    }

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
