package app.dtos;
import app.entities.Candidate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateDTO {
    private Integer id;
    private String name;
    private String phone;
    private String education;
    private List<Integer> skillIds;

    // NYT: Enriched skill data fra ekstern API
    private List<SkillEnrichedDTO> enrichedSkills;

    public CandidateDTO(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.phone = candidate.getPhone();
        this.education = candidate.getEducation();
        this.skillIds = candidate.getCandidateSkills().stream()
                .map(cs -> cs.getId().getSkillId())
                .toList();
    }
}
