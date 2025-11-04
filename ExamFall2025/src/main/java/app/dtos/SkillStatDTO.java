package app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillStatDTO {
    private String slug;
    private Integer popularityScore;
    private Integer averageSalary;
}
