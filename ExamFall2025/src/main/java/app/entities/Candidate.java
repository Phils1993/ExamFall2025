package app.entities;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "candidate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "candidate_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(length = 1000)
    private String education;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateSkill> candidateSkills = new HashSet<>();

    // Helper methods to keep both sides in sync
    public void addSkill(CandidateSkill cs) {
        candidateSkills.add(cs);
        cs.setCandidate(this);
    }

    public void removeSkill(CandidateSkill cs) {
        candidateSkills.remove(cs);
        cs.setCandidate(null);
    }
}
