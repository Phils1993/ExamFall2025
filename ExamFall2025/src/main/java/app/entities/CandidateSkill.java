package app.entities;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "candidate_skill", uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "skill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

//FIXME burde have @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CandidateSkill {

    // FIXME burde have @EqualsAndHashCode.Include
    @EmbeddedId
    private CandidateSkillId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    @ToString.Exclude
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    @ToString.Exclude
    private Skill skill;


    public static CandidateSkill of(Candidate candidate, Skill skill) {
        CandidateSkill cs = new CandidateSkill();
        cs.id = new CandidateSkillId(candidate.getId(), skill.getId());
        cs.candidate = candidate;
        cs.skill = skill;
        return cs;
    }

}

