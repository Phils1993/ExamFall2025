package app.dtos;


import app.enums.Category;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SkillCreateDTO {
    private String name;
    private Category category;
    private String description;

}
