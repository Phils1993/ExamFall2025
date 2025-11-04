package app.entities;

import app.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skill", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "skill_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateSkill> candidateSkills = new HashSet<>();

    // Helper methods to keep both sides in sync
    public void addCandidateSkill(CandidateSkill cs) {
        candidateSkills.add(cs);
        cs.setSkill(this);
    }

    public void removeCandidateSkill(CandidateSkill cs) {
        candidateSkills.remove(cs);
        cs.setSkill(null);
    }
}
