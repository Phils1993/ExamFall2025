package app.entities;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "candidate_skill",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "skill_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CandidateSkill {

    @EmbeddedId
    private CandidateSkillId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    public static CandidateSkill of(Candidate candidate, Skill skill) {
        CandidateSkillId id = new CandidateSkillId(candidate.getId(), skill.getId());
        CandidateSkill cs = new CandidateSkill();
        cs.setId(id);
        cs.setCandidate(candidate);
        cs.setSkill(skill);
        return cs;
    }
}

