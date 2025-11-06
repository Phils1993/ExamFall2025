package app.dtos;

import app.entities.Skill;
import app.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillDTO {
    private Integer id;
    private String name;
    private Category category;
    private String description;
    private String slug;
    private Integer popularityScore;
    private Integer averageSalary;

    public SkillDTO(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.description = skill.getDescription();
        this.category = Category.valueOf(skill.getCategory().name());
        this.slug = skill.getName().toLowerCase().replace(" ", "-");
    }
}
