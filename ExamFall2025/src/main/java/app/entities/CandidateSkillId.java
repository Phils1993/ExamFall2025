package app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CandidateSkillId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "candidate_id")
    private Integer candidateId;

    @Column(name = "skill_id")
    private Integer skillId;

}
