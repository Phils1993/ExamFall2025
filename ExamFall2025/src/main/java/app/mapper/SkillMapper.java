package app.mapper;

import app.dtos.SkillCreateDTO;
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
                // .slug(s.getSlug()) // FIXME: Burde have været på i build fra api'et så jeg får slugs korrekt
                .build();
    }

    public static void updateEntityFromDto(Skill skill, SkillDTO dto) {
        if (dto.getName() != null) skill.setName(dto.getName());
        if (dto.getCategory() != null) skill.setCategory(dto.getCategory());
        if (dto.getDescription() != null) skill.setDescription(dto.getDescription());
    }

    public static Skill toEntity(SkillCreateDTO dto) {
        if (dto == null) return null;
        Skill s = new Skill();
        s.setName(dto.getName());
        s.setCategory(dto.getCategory());
        s.setDescription(dto.getDescription());
        // if your entity has a slug field you may want to set it here or let the DB/logic generate it
        if (s.getSlug() == null || s.getSlug().isBlank()) {
            String derived = deriveSlug(dto.getName());
            s.setSlug(derived);
        }
        return s;
    }

    private static String deriveSlug(String name) {
        if (name == null) return null;
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
