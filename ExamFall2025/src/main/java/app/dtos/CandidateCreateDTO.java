package app.dtos;

import java.util.Set;
import lombok.*;

/**
 * DTO used for creating or updating a Candidate from client requests.
 * Contains only the fields the API accepts (no database id or entity references).
 * The service/DAO layer resolves `skillIds` into managed Skill entities and creates the join rows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCreateDTO {
    private String name;
    private String phone;
    private String education;
    private Set<Long> skillIds;
}
