package app.dtos;


import app.enums.Category;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SkillCreateDTO {
    private String name;
    private Category category;
    private String description;

}
