package app.daos;

/*
import app.entities.Skill;
import app.enums.Category;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillDAO implements IDAO<Skill, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SkillDAO.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    private final EntityManagerFactory emf;

    public SkillDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Skill getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT s FROM Skill s WHERE s.id = :id", Skill.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new ApiException(404, "Skill not found with id: " + id);
        } catch (Exception e) {
            throw new ApiException(500, "Failed to retrieve skill by id");
        }
    }

    @Override
    public List<Skill> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT s FROM Skill s", Skill.class).getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Failed to retrieve all skills");
        }
    }

    @Override
    public Skill create(Skill skill) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(skill);
            em.getTransaction().commit();
            return skill;
        } catch (Exception e) {
            throw new ApiException(500, "Failed to create skill");
        }
    }

    @Override
    public Skill update(Skill skill) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill updatedSkill = em.merge(skill);
            em.getTransaction().commit();
            return updatedSkill;
        } catch (Exception e) {
            throw new ApiException(500, "Failed to update skill with id: " + skill.getId());
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill skill = em.find(Skill.class, id);
            if (skill != null) {
                em.getTransaction().begin();
                em.remove(skill);
                em.getTransaction().commit();
                return true;
            } else {
                throw new ApiException(404, "Skill not found with id: " + id);
            }
        } catch (Exception e) {
            throw new ApiException(500, "Failed to delete skill with id: " + id);
        }
    }

    public List<Skill> getByCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT s FROM Skill s WHERE s.category = :cat", Skill.class)
                    .setParameter("cat", category)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Failed to retrieve skills by category: " + category);
        }
    }


}

 */
