package app.dtos;

import app.enums.Category;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SkillDTO {
    private Long id;
    private String name;
    private Category category;
    private String description;
}
