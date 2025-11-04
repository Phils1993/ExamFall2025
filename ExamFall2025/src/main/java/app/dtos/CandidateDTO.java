package app.dtos;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CandidateDTO {
    private Long id;
    private String name;
    private String phone;
    private String education;
    private Set<Long> skillIds;
}
