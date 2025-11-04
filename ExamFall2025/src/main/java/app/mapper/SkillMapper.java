package app.mapper;

import app.dtos.SkillDTO;
import app.entities.Skill;

public class SkillMapper {

    public static SkillDTO toDto(Skill s) {
        if (s == null) return null;
        return SkillDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .category(s.getCategory())
                .description(s.getDescription())
                .build();
    }

    public static void updateEntityFromDto(Skill skill, SkillDTO dto) {
        if (dto.getName() != null) skill.setName(dto.getName());
        if (dto.getCategory() != null) skill.setCategory(dto.getCategory());
        if (dto.getDescription() != null) skill.setDescription(dto.getDescription());
    }
}
