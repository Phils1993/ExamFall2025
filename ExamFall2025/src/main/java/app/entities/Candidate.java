package app.entities;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "candidate_id")
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(length = 1000)
    private String education;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    // FIXME burde have en @ToString.Exclude for at undgå rekursion
    private Set<CandidateSkill> candidateSkills = new HashSet<>();

    // Helper methods to keep both sides in sync
    public void addSkill(Skill skill) {
        CandidateSkill cs = CandidateSkill.of(this, skill);
        candidateSkills.add(cs);
        skill.getCandidateSkills().add(cs);
    }

    public void removeSkill(Skill skill) {
        candidateSkills.removeIf(cs -> cs.getSkill().equals(skill));
        skill.getCandidateSkills().removeIf(cs -> cs.getCandidate().equals(this));
    }

}
