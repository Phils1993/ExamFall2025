package app.services;

import app.dtos.SkillCreateDTO;
import app.dtos.SkillDTO;
import app.entities.Skill;
import app.daos.SkillDAO;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static app.mapper.SkillMapper.toDto;

public class SkillService {

    private final SkillDAO skillDAO;

    public SkillService(EntityManagerFactory emf) {
        this.skillDAO = new SkillDAO(emf);
    }

    public SkillDTO create(SkillCreateDTO dto) {
        Skill s = new Skill();
        s.setName(dto.getName());
        s.setCategory(dto.getCategory());
        s.setDescription(dto.getDescription());
        Skill saved = skillDAO.create(s);
        return toDto(saved);
    }

    public List<SkillDTO> getAll() {
        return skillDAO.getAll().stream()
                .map(app.mapper.SkillMapper::toDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public SkillDTO getById(Long id) {
        Skill s = skillDAO.getById(id);
        return s == null ? null : toDto(s);
    }

    public SkillDTO update(Long id, SkillCreateDTO dto) {
        Skill existing = skillDAO.getById(id);
        if (existing == null) return null;

        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setDescription(dto.getDescription());

        Skill updated = skillDAO.update(existing);
        return updated == null ? null : toDto(updated);
    }

    /**
     * Delete returns boolean for controller convenience even though DAO.delete is void.
     * We check existence first, then call delete. If DAO throws, we let ApiException bubble (or you can catch and return false).
     */
    public boolean delete(Long id) {
        Skill existing = skillDAO.getById(id);
        if (existing == null) return false;

        skillDAO.delete(id); // void; may throw ApiException on failure
        // assume delete succeeded if no exception
        return true;
    }

}
