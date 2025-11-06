package app.mapper;

import app.dtos.SkillCreateDTO;
import app.dtos.SkillDTO;
import app.dtos.SkillEnrichedDTO;
import app.entities.Skill;

public class SkillMapper {

    public Skill fromCreateDto(SkillCreateDTO dto) {
        if (dto == null) return null;

        return Skill.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
    }

    public SkillDTO toDto(Skill entity) {
        if (entity == null) return null;

        return SkillDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .build();
    }
}
