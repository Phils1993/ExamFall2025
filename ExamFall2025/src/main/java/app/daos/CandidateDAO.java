package app.daos;


import app.dtos.SkillEnrichedDTO;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.Category;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CandidateDAO implements IDAO<Candidate, Integer> {

    private final Logger logger = LoggerFactory.getLogger(CandidateDAO.class);

    private final EntityManagerFactory emf;

    public CandidateDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    @Override
    public Candidate create(Candidate candidate) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(candidate);
            em.getTransaction().commit();
            return candidate;
        } catch (Exception e) {
            throw new ApiException(500, "Failed to create candidate");
        }
    }

    @Override
    public Candidate getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Candidate c " +
                            "LEFT JOIN FETCH c.candidateSkills cs " +
                            "LEFT JOIN FETCH cs.skill " +
                            "WHERE c.id = :id",
                    Candidate.class
            ).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            throw new ApiException(404, "Candidate not found with id: " + id);
        } catch (Exception e) {
            throw new ApiException(500, "Failed to retrieve candidate by id");
        }
    }


    @Override
    public Candidate update(Candidate candidate) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate updatedCandidate = em.merge(candidate);
            em.getTransaction().commit();
            return updatedCandidate;
        } catch (Exception e) {
            throw new ApiException(500, "Failed to update candidate with id: " + candidate.getId());
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate != null) {
                em.getTransaction().begin();
                em.remove(candidate);
                em.getTransaction().commit();
                return true;
            } else {
                throw new ApiException(404, "Candidate not found with id: " + id);
            }
        } catch (ApiException e) {
            throw new ApiException(500, "no candidate deleted");
        }
    }


    //Får alle kandidater med deres respektive skills
    @Override
    public List<Candidate> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT DISTINCT c FROM Candidate c " +
                            "LEFT JOIN FETCH c.candidateSkills cs " +
                            "LEFT JOIN FETCH cs.skill",
                    Candidate.class
            ).getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Failed to retrieve all candidates");
        }
    }


    public Candidate getTopCandidateEntityByAveragePopularity(Map<String, SkillEnrichedDTO> enrichedMap) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Candidate> candidates = em.createQuery(
                    "SELECT DISTINCT c FROM Candidate c LEFT JOIN FETCH c.candidateSkills cs LEFT JOIN FETCH cs.skill",
                    Candidate.class
            ).getResultList();

            Candidate bestCandidate = null;
            double bestAvg = -1;

            for (Candidate candidate : candidates) {
                List<SkillEnrichedDTO> matched = candidate.getCandidateSkills().stream()
                        .map(cs -> enrichedMap.get(cs.getSkill().getSlug()))
                        .filter(Objects::nonNull)
                        .toList();

                if (matched.isEmpty()) continue;

                double avg = matched.stream()
                        .mapToInt(SkillEnrichedDTO::getPopularityScore)
                        .average()
                        .orElse(0);

                if (avg > bestAvg) {
                    bestAvg = avg;
                    bestCandidate = candidate;
                }
            }

            return bestCandidate;
        } catch (Exception e) {
            e.printStackTrace(); // ← tilføj dette
            throw new ApiException(500, "Failed to calculate top candidate by popularity");
        }

    }


    public Candidate linkSkill(Integer candidateId, Integer skillId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate candidate = em.find(Candidate.class, candidateId);
            Skill skill = em.find(Skill.class, skillId);

            if (candidate == null || skill == null) {
                em.getTransaction().rollback();
                return null;
            }

            candidate.addSkill(skill);
            em.merge(candidate);

            em.getTransaction().commit();
            return candidate;
        } catch (Exception e) {
            throw new ApiException(500, "Failed to link skill to candidate");
        }
    }


    public Candidate removeSkill(Integer candidateId, Integer skillId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate candidate = em.find(Candidate.class, candidateId);
            Skill skill = em.find(Skill.class, skillId);

            if (candidate == null || skill == null) {
                em.getTransaction().rollback();
                return null;
            }

            candidate.removeSkill(skill);
            em.merge(candidate);

            em.getTransaction().commit();
            return candidate;
        } catch (Exception e) {
            throw new ApiException(500, "Failed to remove skill from candidate");
        }
    }

    public List<Candidate> getAllBySkillCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                            SELECT DISTINCT c
                            FROM Candidate c
                            JOIN c.candidateSkills cs
                            JOIN cs.skill s
                            WHERE s.category = :category
                            """, Candidate.class)
                    .setParameter("category", category)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Failed to retrieve candidates by skill category");
        }
    }


}


