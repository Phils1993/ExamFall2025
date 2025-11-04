package app.daos;

import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;

public class AbstractDAO <T> implements IDAO <T>{


    protected final EntityManagerFactory emf;
    private final Class<T> entityClass;

    public AbstractDAO(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    @Override
    public T getById(Long id) {
        try(EntityManager em = emf.createEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public Set<T> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return new HashSet<>(em.createQuery("Select e from "
                    +entityClass.getSimpleName()
                    +" e", entityClass).getResultList());
        }
    }

    @Override
    public T create(T t) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
            return t;
        } catch(ApiException e) {
            throw new ApiException( 500, "Not able to save entity");
        }
    }

    @Override
    public T update(T t ) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T merged = em.merge(t);
            em.getTransaction().commit();
            return merged;
        } catch(ApiException e) {
            throw new ApiException( 500, "Not able to update entity");
        }
    }

    @Override
    public void delete(Long id) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if(entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        }  catch(ApiException e) {
            throw new ApiException( 500, "Not able to delete entity");
        }
    }

}

